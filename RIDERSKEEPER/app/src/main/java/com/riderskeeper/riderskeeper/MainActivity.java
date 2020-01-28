package com.riderskeeper.riderskeeper;

import android.os.Bundle;
import android.widget.TextView;
import java.util.Timer;
import java.util.TimerTask;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    boolean RidersKeeper = true;    //lock status
    String[] Users;                 //connected users
    gpsAsyncTask gps_asyncTask;     //update location
    boolean CAMERA = false;         //camera status
    boolean ALARM = false;          //alarm status

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //update location
        gps_asyncTask = new gpsAsyncTask((TextView) findViewById(R.id.position), getApplicationContext());
        gps_asyncTask.execute();

        Timer gps_timer = new Timer();
        TimerTask gps_TT = new TimerTask() {
            @Override
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable(){
                    public void run(){
                        if(RidersKeeper == true) {
                            if (gps_asyncTask.getStatus().toString() == "FINISHED") {
                                gps_asyncTask = new gpsAsyncTask((TextView) findViewById(R.id.position), getApplicationContext());
                                gps_asyncTask.execute();
                            }
                        }
                    }
                });
            }
        };
        gps_timer.schedule(gps_TT, 0, 1000);


    }
}











    /*
    gpsService gService;
    boolean gBound = false;

    double latitude;
    double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onStart();

        final TextView tv = findViewById(R.id.position);
        tv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { // 서비스 시작
                if (gBound) {
                    // Call a method from the gpsService.
                    // However, if this call were something that might hang, then this request should
                    // occur in a separate thread to avoid slowing down the activity performance.
                    latitude = gService.getLat();
                    longitude = gService.getLon();
                    tv.setText("Latitude: " + latitude + "\nLongitude: " + longitude);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, gpsService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        gBound = false;
    }*/

    /**Defines callbacks for service binding, passed to bindService()
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            gpsService.LocalBinder binder = (gpsService.LocalBinder) service;
            gService = binder.getService();
            gBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            gBound = false;
        }
    };
}*/