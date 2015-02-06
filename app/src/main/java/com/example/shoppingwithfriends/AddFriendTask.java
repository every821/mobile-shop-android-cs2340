package com.example.shoppingwithfriends;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpStatus;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddFriendTask extends AsyncTask<Context, Void, Integer> {

    private String username, password, friend;
    private Context mContext;

    public AddFriendTask(String username, String password, String friend) {
        this.friend = friend;
        this.username = username;
        this.password = password;
    }

    @Override
    protected Integer doInBackground(Context... params) {
        mContext = params[0];
        HttpURLConnection conn = null;
        URL url = null;
        int response = 400;
        String query = String.format("username=%s&password=%s&friend=%s", username, password, friend);
        try {
            url = new URL("http://ythogh.com/shopwf/add_friend.php");
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
            conn.disconnect();
            out.close();
            System.out.println(response);
            System.out.println(HttpStatus.SC_ACCEPTED);
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
            Toast.makeText(mContext, "Friend added!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "Problem occurred!", Toast.LENGTH_SHORT).show();
        }
    }

}
