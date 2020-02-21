package com.riderskeeper.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import io.realm.Realm;
import io.realm.RealmResults;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity{

    double latitude;
    double longitude;
    boolean isRunningForeground = true;      //APP running status
    MapPOIItem userLocation = new MapPOIItem(); //user location marker
    String imageViewID;     //imageView id
    Realm realm; //local database

//onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//local database
        Realm.init(this);
        realm = Realm.getDefaultInstance();

//initialize UI
        initUI();

//Create MapView
        final MapView mapView = new MapView(this);
        ViewGroup mapViewContainer = findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

//set userLocation marker
        userLocation.setItemName("You are Here");
        userLocation.setTag(0);
        userLocation.setMarkerType(MapPOIItem.MarkerType.CustomImage); // marker design
        userLocation.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
        userLocation.setCustomImageResourceId(R.mipmap.usermarker);

//user's location
        LocationManager locationManager = (LocationManager) getApplication().getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        if (location == null){
            location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
        }
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true);

//user location update
        Timer gps_timer = new Timer();
        TimerTask gps_TT = new TimerTask() {
            @Override
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable(){
                    public void run(){
                        if (isRunningForeground) {
                            //get user location
                            LocationManager locationManager = (LocationManager) getApplication().getSystemService(Context.LOCATION_SERVICE);
                            Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
                            if (location == null){
                                location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                            }
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            //update user location on map
                            mapView.removePOIItem(userLocation);
                            userLocation.setMapPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude));
                            mapView.addPOIItem(userLocation);
                        }
                    }
                });
            }
        };
        gps_timer.schedule(gps_TT, 0, 2000);

//other RKs location update
        Timer gps_timer2 = new Timer();
        TimerTask gps_TT2 = new TimerTask() {
            @Override
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable(){
                    public void run(){
                        if (isRunningForeground) {
                            //





                        }
                    }
                });
            }
        };
        gps_timer2.schedule(gps_TT2, 0, 2000);

//gps button - toasts current location
        Button gpsButton = findViewById(R.id.clickGPS);
        gpsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true);
                Toast toast = Toast.makeText(MainActivity.this, "Latitude:" + latitude + "\nLongitude:" + longitude, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            }
        });

//edit button - add/delete bicycles
        Button editButton = findViewById(R.id.edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, edit.class);
                startActivityForResult(intent, 0);
            }
        });
    }
//End of onCreate



//onActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        //imageVIew
        if(requestCode == 1 && resultCode == RESULT_OK){
            Uri selectedImageUri = data.getData();

            RealmResults<localDB> results = realm.where(localDB.class).equalTo("ID", imageViewID).findAll();
            for(localDB RK : results){
                realm.beginTransaction();
                RK.setImageURI(selectedImageUri.toString());
                realm.commitTransaction();
            }

            LinearLayout rootLayout = findViewById(R.id.root);
            LinearLayout ll = rootLayout.findViewWithTag("ll"+ imageViewID);
            ImageView imageView = ll.findViewWithTag("image"+ imageViewID);
            imageView.setImageURI(selectedImageUri);
        }

        switch (resultCode) {
            //do nothing
            case 0:
                break;

            //edit > addButton
            case 1:
                //DB add
                realm.beginTransaction();
                localDB localdb = realm.createObject(localDB.class);

                localdb.setID(data.getStringExtra("id"));
                localdb.setStatus("OFF");
                localdb.setImageURI(null);

                realm.commitTransaction();

                //UI add
                addBicycle(data.getStringExtra("id"));
                break;

            //edit > deleteButton
            case 2:
                //DB delete
                RealmResults<localDB> results = realm.where(localDB.class).equalTo("ID", data.getStringExtra("id")).findAll();
                realm.beginTransaction();
                results.deleteAllFromRealm();
                realm.commitTransaction();

                //UI delete
                deleteBicycle(data.getStringExtra("id"));
                break;

            //edit > on/off
            case 3:
                //UI update
                int c = data.getIntExtra("color",0);
                setConnectionColor(data.getStringExtra("id"), c);

                //DB update
                RealmResults<localDB> results2 = realm.where(localDB.class).equalTo("ID", data.getStringExtra("id")).findAll();
                realm.beginTransaction();
                for(localDB localdb2 : results2){
                    if (c == 1) localdb2.setStatus("ON");
                    else if (c == 0) localdb2.setStatus("OFF");
                    else localdb2.setStatus("ERROR");
                }
                realm.commitTransaction();
                break;

            //default
            default:
                break;
        }
    }

//add new bicycles
    public void addBicycle(String id){

        LinearLayout rootLayout = findViewById(R.id.root);

        //LinearLayout
        LinearLayout ll = new LinearLayout(this);
        ll.setTag("ll" + id);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(410, LinearLayout.LayoutParams.MATCH_PARENT);
        ll.setLayoutParams(layoutParams);

        //Camera Button
        Button cameraButton = new Button(this);
        cameraButton.setTag("camera"+id);
        cameraButton.setBackground(ContextCompat.getDrawable(this, R.mipmap.camera));
        ll.addView(cameraButton);
        ViewGroup.MarginLayoutParams camera_params = (ViewGroup.MarginLayoutParams) cameraButton.getLayoutParams();
        camera_params.width = 100; camera_params.height = 100;
        camera_params.leftMargin = 90; camera_params.topMargin = 15; camera_params.bottomMargin = 10;
        cameraButton.setLayoutParams(camera_params);

        //Image Button
        ImageView imageView = new ImageView(this);
        imageView.setTag("image"+id);

        RealmResults<localDB> results = realm.where(localDB.class).equalTo("ID", id).findAll();
        for(localDB RK : results){
            if (RK.getImageURI() == null)imageView.setImageResource(R.drawable.ic_launcher_foreground);
            else imageView.setImageURI(Uri.parse(RK.getImageURI()));
        }

        GradientDrawable drawable= (GradientDrawable) this.getDrawable(R.drawable.background_rounding);
        imageView.setBackground(drawable);
        imageView.setClipToOutline(true);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        ll.addView(imageView);
        imageView.getLayoutParams().width = 275;
        imageView.getLayoutParams().height = 275;
        imageView.requestLayout();

        //LinearLayout - id, connection
        LinearLayout llayout = new LinearLayout(this);
        llayout.setTag("llayout"+id);
        llayout.setOrientation(LinearLayout.HORIZONTAL);
        llayout.setGravity(Gravity.CENTER);

        //TextView - id
        TextView id_text = new TextView(this);
        id_text.setTag("ID"+id);
        id_text.setTextColor(Color.parseColor("#FFFFFF"));
        id_text.setText(id);

        //TextView - connection
        TextView connection_color = new TextView(this);
        connection_color.setTag("color"+id);
        connection_color.setTextColor(Color.parseColor("#FF4040"));
        connection_color.setText(" ●");

        //addView
        llayout.addView(id_text);
        llayout.addView(connection_color);
        ll.addView(llayout);
        rootLayout.addView(ll);

        //camera button onClickListener
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "hi camera", Toast.LENGTH_LONG).show();
            }
        });

        //image button onClickListener
        imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String s = v.getTag().toString();
                String s2 = s.replace("image", "");
                imageViewID = s2;

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 1);
            }
        });
    }

//delete selected bicycle
    public void deleteBicycle(String id){
        LinearLayout rootLayout = findViewById(R.id.root);
        LinearLayout ll = rootLayout.findViewWithTag("ll"+id);
        rootLayout.removeView(ll);
    }

//change connection color
    public void setConnectionColor(String id, int c){
        LinearLayout rootLayout = findViewById(R.id.root);
        LinearLayout ll = rootLayout.findViewWithTag("ll"+id);
        LinearLayout llayout = ll.findViewWithTag("llayout"+id);
        TextView colorText = llayout.findViewWithTag("color"+id);

        //ON: 1, OFF: 0, ERROR: -1
        if (c != 1) {
            colorText.setTextColor(Color.parseColor("#FF4040")); //red
        }
        else {
            colorText.setTextColor(Color.parseColor("#00AC00")); //green
        }
    }

//initialize UI
    public void initUI(){
        RealmResults<localDB> results = realm.where(localDB.class).findAll();
        for(localDB RK : results){
            addBicycle(RK.getID());

            realm.beginTransaction();
            RK.setStatus("OFF");
            realm.commitTransaction();
        }
    }

//onStop
    @Override
    protected  void onStop(){
        isRunningForeground = false;
        super.onStop();
    }

//onResume
    @Override
    protected  void onResume(){
        isRunningForeground = true;
        super.onResume();
    }

//onDestroy
    @Override
    protected void onDestroy() {
        realm.close();
        super.onDestroy();
    }
}



   /*

        int count = 0;
        for (String id : idList) {
            appendList(id);

            //layouts - edit_xml
            LinearLayout ll = findViewById(R.id.scrollLinear);
            LinearLayout layer = ll.findViewWithTag("layer" + id);

            //delete button
            Button b = layer.findViewWithTag("button" + id);
            b.setOnClickListener(onClickListener);

            //ON/OFF button
            Button b2 = layer.findViewWithTag("button2" + id);
            b2.setOnClickListener(onClickListener2);

            if (conn[count] == 1){ //green
                b2.setText("ON");
            }else { //red
                b2.setText("OFF");
            }
            count++;
        }
*/




 /*
                        if (isRunningForeground && gps_asyncTask.getStatus().toString() == "FINISHED") {
                            gps_asyncTask = new gpsAsyncTask(getApplicationContext());

                            try {
                                double[] coor = gps_asyncTask.execute().get();
                                latitude = coor[0];
                                longitude = coor[1];

                                mapView.removePOIItem(userLocation);

                                userLocation.setMapPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude));
                                mapView.addPOIItem(userLocation);
                            }
                            catch (Exception e){}
                        }*/



        /*
    getCurrentLocation gService;
    boolean gBound = false;

    double latitude;
    double longitude;

        //gpsButton
        final Button gpsButton = findViewById(R.id.clickGPS);
        gpsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (gBound) {
                    // Call a method from the gpsService.
                    // However, if this call were something that might hang, then this request should
                    // occur in a separate thread to avoid slowing down the activity performance.
                    latitude = gService.getLat();
                    longitude = gService.getLon();
                    Toast.makeText(MainActivity.this, "Latitude:" + latitude + "  Longitude:" + longitude, Toast.LENGTH_LONG).show();

                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, getCurrentLocation.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        gBound = false;
    }
    */

    /** Defines callbacks for service binding, passed to bindService() */
    /*
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            getCurrentLocation.LocalBinder binder = (getCurrentLocation.LocalBinder) service;
            gService = binder.getService();
            gBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            gBound = false;
        }
    };
}*/