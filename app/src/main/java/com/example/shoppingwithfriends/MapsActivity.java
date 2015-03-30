package com.example.shoppingwithfriends;

import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

@SuppressWarnings("ALL")
public class MapsActivity extends FragmentActivity  implements ConnectionCallbacks,
        OnConnectionFailedListener, OnClickListener, GoogleMap.OnMapClickListener {

    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private boolean mResolvingError = false;

    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private LatLng myLocation;
    private ArrayList<SaleItem> arr;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        username = getIntent().getExtras().getString("username");
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setOnMapClickListener(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        Log.i("Map", "Created Api Client");
        mGoogleApiClient.connect();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_post:
                Intent intent = new Intent(getApplicationContext(), PostSale.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Handles the work done when connected to Google Places API server
     * @param connectionHint details about the connection
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            myLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            if (map != null) {
                Marker me = map.addMarker(new MarkerOptions().position(myLocation).title("Me")
                        .snippet("You are here.")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
                map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
            }
            new GetAllSalesTask(username).execute();
        } else {
            Log.e("Map", "My location is null");
        }
    }

    /**
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
        Log.e("OnConnectionSuspended", cause + "");
    }

    @Override
    public void onMapClick(LatLng latLng) {
        //map.addMarker(new MarkerOptions().position(latLng).alpha(.5f));
    }

    private class ParseJson extends AsyncTask<String, Void, MarkerOptions> {
        private String address = "http://ythogh.com/helpster/scripts/json_request.php";

        /**
         * <p>Runs on the UI thread after {@link #doInBackground}. The
         * specified result is the value returned by {@link #doInBackground}.</p>
         * <p/>
         * <p>This method won't be invoked if the task was cancelled.</p>
         *
         * @param markerOptions The result of the operation computed by {@link #doInBackground}.
         * @see #onPreExecute
         * @see #doInBackground
         * @see #onCancelled(Object)
         */
        @Override
        protected void onPostExecute(MarkerOptions markerOptions) {
            if (markerOptions != null) {
                map.addMarker(markerOptions);
            }
        }

        protected MarkerOptions doInBackground(String... params) {
            double lat = myLocation.latitude;
            double lng = myLocation.longitude;
            int radius = 5000;
            String place = params[0];
            String location = String.format("%f,%f", lat, lng);
            String query = String.format("radius=%d&location=%s&place=%s", radius, location, place);
            // System.out.println(query);
            HttpURLConnection conn = null;
            URL url = null;
            String str = "";
            try {
                url = new URL(address);
                String agent = "Applet";
                String type = "application/x-www-form-urlencoded";
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("User-Agent", agent);
                conn.setRequestProperty("Content-Type", type);
                conn.setRequestProperty("Content-Length", "" + query.length());
                OutputStream out = conn.getOutputStream();
                out.write(query.getBytes());
                Log.d("Progress", "Connected to host server");
                String inputLine = "";
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                int j = 0;
                while ((inputLine = in.readLine()) != null) {
                    //Log.d("Output", inputLine);
                    str = str + inputLine;
                    j++;
                }
                //Log.e("Finished one", "finally: " + j);

                JSONObject obj = new JSONObject(str);
                JSONArray arr = obj.getJSONArray("results");
                int i = 0;
                String nme = arr.getJSONObject(i).getString("name");
                String adrs = arr.getJSONObject(i).getString("vicinity");
                JSONObject loc = arr.getJSONObject(i).getJSONObject("geometry")
                        .getJSONObject("location");
                String lat2 = loc.getString("lat");
                String lng2 = loc.getString("lng");
                double lat3 = Double.parseDouble(lat2);
                double lng3 = Double.parseDouble(lng2);
                return new MarkerOptions().position(new LatLng(lat3, lng3)).title(place);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }
    }

    private void onGetSalesReturn() {
        for (SaleItem item : arr) {
            new ParseJson().execute(item.getItem());
        }
    }

    private class GetAllSalesTask extends AsyncTask<Void, Void, Boolean> {

        String username;

        public GetAllSalesTask(String username) {
            this.username = username;
            arr = new ArrayList<>();
        }

        /**
         * @param params passes in getApplicationContext() from calling Activity
         * @return Result code from php request
         */
        @Override
        protected Boolean doInBackground(Void... params) {

            HttpURLConnection conn = null;
            URL url = null;
            int response = 400;
            String query = String.format("username=%s", username);
            try {
                url = new URL("http://ythogh.com/shopwf/scripts/get_sales.php");
                String agent = "Applet";
                String type = "application/x-www-form-urlencoded";
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("User-Agent", agent);
                conn.setRequestProperty("Content-Type", type);
                conn.setRequestProperty("Content-Length", "" + query.length());
                OutputStream out = conn.getOutputStream();
                out.write(query.getBytes());
                response = conn.getResponseCode();
                System.out.println(response);
                String inputLine = "";
                String result = "";
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((inputLine = in.readLine())!= null) {
                    result += inputLine;
                    Log.e("Sale", inputLine);
                }
                JSONArray jArr = new JSONArray(result);
                JSONObject jObj;
                SaleItem mItem;
                for (int i = 0; i < jArr.length(); i++) {
                    jObj = jArr.getJSONObject(i);
                    mItem = new SaleItem(jObj.getString("username"), jObj.getString("item"),
                            jObj.getString("location"), jObj.getString("price"));
                    System.out.println("Username: " + mItem.getUsername());
                    System.out.println("Item: " + mItem.getItem());
                    System.out.println("Price: " + mItem.getPrice());
                    arr.add(mItem);
                }
                conn.disconnect();
                out.close();
                return true;
            } catch (Exception e) {
                conn.disconnect();
                e.printStackTrace();
                return false;
            }
        }

        /**
         * Updates friends-list with returned friends' names
         * @param result Result code from php request
         */
        @Override
        protected void onPostExecute(Boolean result) {
            onGetSalesReturn();
        }

    }

}

