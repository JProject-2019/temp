package com.riderskeeper.riderskeeper;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.widget.TextView;

public class gpsAsyncTask extends AsyncTask<Void, Integer, Boolean> {

    TextView textView;
    Context gpsContext;
    String latitude;
    String longitude;

    public gpsAsyncTask(TextView textView, Context gpsContext)
    {
        this.textView = textView;
        this.gpsContext = gpsContext;
    }

    @Override
    protected Boolean doInBackground(Void... strings){

        LocationManager locationManager = (LocationManager) gpsContext.getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());

        publishProgress();

        return true;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Boolean s) {
        super.onPostExecute(s);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        textView.setText("Latitude: " + latitude +"\nLongitude: " + longitude);
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled(Boolean s) {
        super.onCancelled(s);
    }
}