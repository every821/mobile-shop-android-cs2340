package com.example.shoppingwithfriends;

import android.content.IntentSender.SendIntentException;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity  implements ConnectionCallbacks,
        OnConnectionFailedListener, OnClickListener  {

    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private boolean mResolvingError = false;

    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private LatLng myLocation;
    private Location mLastLocation;
    private Marker me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        Log.i("Map", "Created Api Client");
        mGoogleApiClient.connect();
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * Handles the work done when connected to Google Places API server
     * @param connectionHint details about the connection
     */
    @Override
    public void onConnected(Bundle connectionHint) {
       mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            myLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            if (map != null) {
                me = map.addMarker(new MarkerOptions().position(myLocation).title("Me")
                        .snippet("You are here.")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
                map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
            }
        } else {
            Log.e("Map", "My location is null");
        }

    }

    /**
     *
     * @param result The error from the failed connection
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (SendIntentException e) {
                mGoogleApiClient.connect();
            }
        } else {
            Log.e("Map", "Can't connect");
            mResolvingError = true;
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.e("OnConnectionSuspended", "How'd we get here!?");
    }
}

