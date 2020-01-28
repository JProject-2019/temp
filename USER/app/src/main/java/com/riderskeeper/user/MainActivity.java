package com.riderskeeper.user;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.w3c.dom.Text;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity{

    double latitude;
    double longitude;
    boolean isRunningForeground = true;      //APP running status
    gpsAsyncTask gps_asyncTask;              //update location
    MapPOIItem userLocation = new MapPOIItem(); //user location marker
    static HashSet<String> ID_list = new HashSet<String>();

//onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

//execute gps_asyncTask - updates user's location
        gps_asyncTask = new gpsAsyncTask(getApplicationContext());
        try {
            double[] coor = gps_asyncTask.execute().get();
            latitude = coor[0];
            longitude = coor[1];
            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true);
        }catch (Exception e){}

        Timer gps_timer = new Timer();
        TimerTask gps_TT = new TimerTask() {
            @Override
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable(){
                    public void run(){
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
                        }
                    }
                });
            }
        };
        gps_timer.schedule(gps_TT, 0, 2000);

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
                intent.putExtra("list", ID_list);

                int[] con_list = new int[ID_list.size()];
                int count = 0;
                LinearLayout l = findViewById(R.id.root);
                for(String id : ID_list){
                    LinearLayout ll = l.findViewWithTag("ll"+id);
                    LinearLayout llayout = ll.findViewWithTag("llayout"+id);
                    TextView col = llayout.findViewWithTag("color"+id);

                    if (col.getCurrentTextColor() == -16733184){ //green
                        con_list[count] = 1;
                    }else { //red
                        con_list[count] = 0;
                    }
                    count++;
                }
                intent.putExtra("connection", con_list);

                startActivityForResult(intent, 0);
            }
        });
    }
//End of onCreate



//onActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        String id = data.getStringExtra("id");

        switch (resultCode) {
            //do nothing
            case 0:
                break;

            //edit > addButton
            case 1:
                ID_list.add(id);
                addBicycle(id);
                break;

            //edit > deleteButton
            case 2:
                ID_list.remove(id);
                deleteBicycle(id);
                break;

            //edit > on/off
            case 3:
                int c = data.getIntExtra("color",0);
                changeColor(id, c);
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
        Button imageButton = new Button(this);
        imageButton.setTag("image"+id);
        imageButton.setBackground(ContextCompat.getDrawable(this, R.mipmap.ic_launcher_round));
        ViewGroup.LayoutParams image_lp = new ViewGroup.LayoutParams(260,260);
        imageButton.setLayoutParams(image_lp);

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
        connection_color.setTextColor(Color.parseColor("#00AC00"));
        connection_color.setText(" ●");

        //addView
        ll.addView(imageButton);
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
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "hi image", Toast.LENGTH_LONG).show();
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
    public void changeColor(String id, int c){
        LinearLayout rootLayout = findViewById(R.id.root);
        LinearLayout ll = rootLayout.findViewWithTag("ll"+id);
        LinearLayout llayout = ll.findViewWithTag("llayout"+id);
        TextView colorText = llayout.findViewWithTag("color"+id);

        if (c == 0) {
            colorText.setTextColor(Color.parseColor("#FF4040")); //red
        }
        else {
            colorText.setTextColor(Color.parseColor("#00AC00")); //green
        }
    }


//onStop
    @Override
    protected  void onStop(){
        try
        {
            if (gps_asyncTask.getStatus().toString() == "RUNNING")
            {
                gps_asyncTask.cancel(true);
            }
        }
        catch (Exception e)
        {
        }
        isRunningForeground = false;
        super.onStop();
    }

//onResume
    @Override
    protected  void onResume(){
        isRunningForeground = true;
        super.onResume();
    }
}





























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