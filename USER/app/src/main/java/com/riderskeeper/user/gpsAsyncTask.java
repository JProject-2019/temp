package com.riderskeeper.user;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;

public class gpsAsyncTask extends AsyncTask<double[], Integer, double[]> {

    Context gpsContext;
    double[] coordinates = new double[2];
    double latitude;
    double longitude;

    public gpsAsyncTask(Context gpsContext)
    {
        this.gpsContext = gpsContext;
    }

    @Override
    protected double[] doInBackground(double[]... params){

        LocationManager locationManager = (LocationManager) gpsContext.getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        if (location == null){
            location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
        }
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        coordinates[0] = latitude;
        coordinates[1] = longitude;

        publishProgress();

        return coordinates;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(double[] params) {
        super.onPostExecute(params);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        //textView.setText("Latitude: " + latitude +"\nLongitude: " + longitude);
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled(double[] params) {
        super.onCancelled(params);
    }
}