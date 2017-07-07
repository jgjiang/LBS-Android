package com.ucc.tony.myhangout;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

/**
 * Created by tony on 11/02/2017.
 */

public class LocationUtils {

    private static final long REFRESH_TIME = 5000L;
    private static final float METER_POSITION = 0.0f;
    private static ILocationListener mLocationListener;
    private static LocationListener listener = new MyLocationListener();


    private static class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {//location change listening
            if (mLocationListener != null) {
                mLocationListener.onSuccessLocation(location);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {//status change listening

        }

        @Override
        public void onProviderEnabled(String provider) {// enabled status listening

        }

        @Override
        public void onProviderDisabled(String provider) {// disabled status listening

        }
    }

    /**
     * GPS-based location
     */
    public static Location getGPSLocation(@NonNull Context context) {
        Location location = null;
        LocationManager manager = getLocationManager(context);
        //permissions checking
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {//checking if the location is supported

            location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        return location;
    }

    /**
     * network-based location
     */
    public static Location getNetWorkLocation(Context context) {
        Location location = null;
        LocationManager manager = getLocationManager(context);
        //permissions checking
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //use the network-based location
            location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        return location;
    }

    /**
     * obtain the best location method
     */
    public static Location getBestLocation(Context context, Criteria criteria) {
        Location location;
        LocationManager manager = getLocationManager(context);
        if (criteria == null) {
            criteria = new Criteria();
        }
        String provider = manager.getBestProvider(criteria, true);
        if (TextUtils.isEmpty(provider)) {
            // if cannot fine the best location method, use network-based location
            location = getNetWorkLocation(context);
        } else {
            // permissions checking
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            // use best location method
            location = manager.getLastKnownLocation(provider);
        }
        return location;
    }

    /**
     * location listening
     */
    public static void addLocationListener(Context context, String provider, ILocationListener locationListener) {

        addLocationListener(context, provider, REFRESH_TIME, METER_POSITION, locationListener);
    }

    /**
     * location listening calling method
     */
    public static void addLocationListener(Context context, String provider, long time, float meter, ILocationListener locationListener) {
        if (locationListener != null) {
            mLocationListener = locationListener;
        }
        if (listener == null) {
            listener = new MyLocationListener();
        }
        LocationManager manager = getLocationManager(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.requestLocationUpdates(provider, time, meter, listener);
    }

    /**
     * cancel locating
     */
    public static void unRegisterListener(Context context) {
        if (listener != null) {
            LocationManager manager = getLocationManager(context);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            //remove the listener
            manager.removeUpdates(listener);
        }
    }

    private static LocationManager getLocationManager(@NonNull Context context) {
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    /**
     * customize the location interface
     */
    public interface ILocationListener {
        void onSuccessLocation(Location location);
    }



}
