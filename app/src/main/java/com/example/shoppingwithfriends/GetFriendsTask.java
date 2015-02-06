package com.example.shoppingwithfriends;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpStatus;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class GetFriendsTask extends AsyncTask<Context, Void, Integer> {

    private String username, password, friend;
    private Context mContext;
    public static ArrayList<String> arrlist;

    public GetFriendsTask(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    protected Integer doInBackground(Context... params) {
        mContext = params[0];
        arrlist = new ArrayList<String>();
        HttpURLConnection conn = null;
        URL url = null;
        int response = 400;
        String query = String.format("username=%s&password=%s", username, password);
        try {
            url = new URL("http://ythogh.com/shopwf/get_friends.php");
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
                arrlist.add(inputLine);
                Log.e("Friend", inputLine);
            }
            conn.disconnect();
            out.close();
            return response;
        } catch (Exception e) {
            conn.disconnect();
            Log.e("Login", "Exception when logging in: " + response);
            e.printStackTrace();
            return HttpStatus.SC_SERVICE_UNAVAILABLE;
        }
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        System.out.println("result: " + result);
        if (result == HttpStatus.SC_ACCEPTED) {
            ViewFriendsList.onGetFriendsReturn(arrlist);
        } else {
            Toast.makeText(mContext, "Problem occurred!", Toast.LENGTH_SHORT).show();
        }
    }

}
