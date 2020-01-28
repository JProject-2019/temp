package com.riderskeeper.riderskeeper;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;

public class gpsService extends Service {
    // Binder given to clients
    private final IBinder binder = new LocalBinder();

    private double lat;
    private double lon;

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        gpsService getService() {
            // Return this instance of LocalService so clients can call public methods
            return gpsService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public double getLat() {
        Context gpsContext = getApplicationContext();
        LocationManager locationManager = (LocationManager) gpsContext.getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        lat = location.getLatitude();
        return lat;
    }

    public double getLon() {
        Context gpsContext = getApplicationContext();
        LocationManager locationManager = (LocationManager) gpsContext.getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        lon = location.getLongitude();
        return lon;
    }
}