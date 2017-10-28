package com.liftindia.app.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.liftindia.app.activity.HomeActivity;

public class LocationManager implements LocationListener, ConnectionCallbacks, OnConnectionFailedListener, ResultCallback<LocationSettingsResult>,
        android.location.LocationListener {

    private Activity activity;
    private LocationHandlerListener listener;
    private static LocationManager instance;
    public static final int REQUEST_CHECK_SETTINGS = 1005;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest request;
    private boolean isReqLocation;
    private LocationSettingsRequest.Builder builder;
    private PendingResult<LocationSettingsResult> result;
    private Location currentLocation;
    private android.location.LocationManager locationManager;
    private LocationSettingsRequest settingReq;
    public static boolean isActivityForeground = true;
    public static boolean isMainActivityForeground = true;

    private LocationManager(Activity activity) {
        this.activity = activity;
    }

    public static LocationManager getInstance(Activity activity) {
        if (instance == null) {
            instance = new LocationManager(activity);
        }
        return instance;
    }

    public LocationManager buildAndConnectClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(activity).addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
        }
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();

        }
        if (locationManager == null) {
            locationManager = (android.location.LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
            int permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(android.location.LocationManager.GPS_PROVIDER, 0, 0, this);
            }
        }
        return this;
    }

    public LocationManager setLocationHandlerListener(LocationHandlerListener listener) {
        this.listener = listener;
        return this;
    }

    public LocationManager buildLocationRequest() {
        if (settingReq == null) {
            request = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            request.setInterval(5000);
            request.setFastestInterval(2000);
            builder = new LocationSettingsRequest.Builder().addLocationRequest(request);
            settingReq = builder.build();
        }
        return this;
    }

    public boolean requestLocation() {
        if (!mGoogleApiClient.isConnected()) {
            isReqLocation = true;
            return false;
        }
        result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, settingReq);
        result.setResultCallback(this);
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        int permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
                        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, request, instance);
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(activity, "You Must enable Location Service for app functionality", Toast.LENGTH_SHORT).show();
                        requestLocation();
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @Override
    public void onLocationChanged(Location arg0) {
        if (listener != null) {
            listener.locationChanged(arg0);
            currentLocation = arg0;
        }
    }

    public interface LocationHandlerListener {
        public void locationChanged(Location location);

        public void lastKnownLocationAfterConnection(Location location);
    }

    @Override
    public void onConnected(Bundle arg0) {
        // Toast.makeText(activity, "Conn", Toast.LENGTH_SHORT).show();
        if (isReqLocation) {
            requestLocation();
        }
        int permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        if (listener != null) {
            listener.lastKnownLocationAfterConnection(currentLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        // Toast.makeText(activity, "Conn Sus", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        // Toast.makeText(activity, "Conn Fail", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResult(LocationSettingsResult result) {
        final Status status = result.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                int permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    // Should we show an explanation?
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, request, instance);
                }
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    boolean isRideActive = false;
                    if (activity != null) {
                        isRideActive = SharedPreference.getInstance(activity, SharedPreference.PREF_TYPE_GENERAL).getBoolean(Const.IS_RIDE_ACTIVE, false);
                    }
                    if (activity != null && isRideActive) {
                        status.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
                    }
                    if (activity != null && isActivityForeground) {
                        status.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
                    }

                } catch (SendIntentException e) {
                    e.printStackTrace();
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                break;
        }
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void stopTracking() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, instance);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        if (result != null) {
            result.cancel();
        }

    }

    @Override
    public void onProviderDisabled(String provider) {
        if (result != null) {
            result.cancel();
            requestLocation();
        }
    }

    public void removeLocationUpdate() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (LocationListener) listener);
    }
}
