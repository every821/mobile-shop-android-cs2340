package com.example.shoppingwithfriends;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("ALL")
public class MainActivity extends ActionBarActivity {

    String username, password;
    Context mContext;
    ImageView ivPost, ivBrowse, ivProfile, ivFriends;
    User user;
    public static ArrayList<SaleItem> arr;
    Set<String> friends;
    double mLow = 0f, mHigh = 100f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        username = getIntent().getExtras().getString("username");
        password = getIntent().getExtras().getString("password");

        new GetFriendsTask(username, password).execute();

        ivPost = (ImageView) findViewById(R.id.MAIN_IMAGEVIEW_POST);
        ivBrowse = (ImageView) findViewById(R.id.MAIN_IMAGEVIEW_BROWSE);
        ivProfile = (ImageView) findViewById(R.id.MAIN_IMAGEVIEW_PROFILE);
        ivFriends = (ImageView) findViewById(R.id.MAIN_IMAGEVIEW_FRIENDS);
        ivPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), PostSale.class);
                i.putExtra("username", username);
                i.putExtra("password", password);
                startActivity(i);
            }
        });
        ivBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), BrowseSales.class);
                i.putExtra("username", username);
                i.putExtra("password", password);
                startActivity(i);
            }
        });
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Profile", Toast.LENGTH_SHORT).show();
            }
        });
        ivFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ViewFriendsList.class);
                i.putExtra("username", username);
                i.putExtra("password", password);
                startActivity(i);
            }
        });
    }

    private void onGetSalesReturn() {
        new AlertDialog.Builder(this).setTitle(arr.size() + " items found!")
                .setMessage("We found " + arr.size() + " items that fit your price range. View them or ignore?")
                .setPositiveButton("Show me!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(getApplicationContext(), BrowseSales.class);
                        i.putExtra("username", username);
                        i.putExtra("min", mLow);
                        i.putExtra("max", mHigh);
                        startActivity(i);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(R.drawable.ic_action_settings)
                .show();
        //Toast.makeText(getApplicationContext(), arr.size() + " items found!", Toast.LENGTH_LONG).show();
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
                System.out.println(user.getLow() + " -- " + user.getHigh());
                mLow = Double.parseDouble(user.getLow());
                mHigh = Double.parseDouble(user.getHigh());
                for (String s : friends) {
                    Log.e("Friend", s);
                }
                for (int i = 0; i < jArr.length(); i++) {
                    jObj = jArr.getJSONObject(i);
                    mItem = new SaleItem(jObj.getString("username"), jObj.getString("item"),
                            jObj.getString("location"), jObj.getString("price"));
                    System.out.println("Username: " + mItem.getUsername());
                    System.out.println("Item: " + mItem.getItem());
                    System.out.println("Price: " + mItem.getPrice());
                    if ((Double.parseDouble(mItem.getPrice()) < mHigh) && (Double.parseDouble(mItem.getPrice()) > mLow)) {
                        System.out.println("Price is right");
                        if (friends.contains(mItem.getUsername().trim())) {
                            arr.add(mItem);
                            System.out.println("*Added*");
                        } else {
//                            /System.out.println(mItem.getUsername().getBytes());
                        }
                    } else {
                        System.out.println("Price wrong: " + mItem.getItem());
                    }
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

    private class GetFriendsTask extends AsyncTask<Context, Void, Boolean> {

        private String username, password; // --Commented out by Inspection (3/29/2015 8:13 PM):friend;
        private Context mContext;

        public GetFriendsTask(String username, String password) {
            this.username = username;
            this.password = password;
        }

        /**
         * Connects to ythogh.com and requests friends of 'username'
         * @param params passes in getApplicationContext() from calling Activity
         * @return Result code from php request
         */
        @Override
        protected Boolean doInBackground(Context... params) {
            friends = new HashSet<>();
            HttpURLConnection conn = null;
            URL url = null;
            int response = 400;
            String query = String.format("username=%s&password=%s", username, password);
            try {
                url = new URL("http://ythogh.com/shopwf/scripts/get_friends.php");
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
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((inputLine = in.readLine())!= null) {
                    friends.add(inputLine.trim());
                    Log.e("Friend", inputLine);
                }
                conn.disconnect();
                out.close();
                return true;
            } catch (Exception e) {
                conn.disconnect();
                Log.e("Login", "Exception when logging in: " + response);
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
            System.out.println("result: " + result);
            if (result) {
                new GetAllSalesTask(username).execute();
            } else {
                Toast.makeText(mContext, "Problem occurred!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_map:
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("password", password);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



}
