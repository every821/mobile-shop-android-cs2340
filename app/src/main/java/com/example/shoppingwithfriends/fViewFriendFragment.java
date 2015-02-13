package com.example.shoppingwithfriends;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class fViewFriendFragment extends Fragment implements View.OnClickListener {

    FragmentActivity listener;
    String username, password, friend, email, rating, report_count;
    Bundle extras;
    TextView tvFriend, tvEmail, tvRating, tvReportCount;
    HashMap<String, String> hm;
    static int nuevo = 0;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.listener = (FragmentActivity) activity;
        Log.e("Attached", "View Friend");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_f_view_friend, container, false);
        Button btReturn = (Button) v.findViewById(R.id.VIEWFRIEND_BUTTON_SEEPOSTS);
        btReturn.setOnClickListener(this);
        extras = getArguments();
        username = extras.getString("username");
        password = extras.getString("password");
        friend = extras.getString("friend");
        if (hm == null) {
            tvFriend = (TextView) v.findViewById(R.id.VIEWFRIEND_TEXTVIEW_FRIEND);
            tvEmail = (TextView) v.findViewById(R.id.VIEWFRIEND_TEXTVIEW_EMAIL);
            tvRating = (TextView) v.findViewById(R.id.VIEWFRIEND_TEXTVIEW_RATING);
            tvReportCount = (TextView) v.findViewById(R.id.VIEWFRIEND_TEXTVIEW_REPORTCOUNT);
            new GetFriendInfoTask(username, password, friend).execute();
        } else {
            onGetFriendInfoTaskReturn(hm);
        }
        return v;
    }

    public void onGetFriendInfoTaskReturn(HashMap<String, String> h) {
        this.hm = h;
        tvFriend.setText("Username: " + friend);
        email = hm.get("email");
        rating = hm.get("rating");
        report_count = hm.get("report_count");
        tvEmail.setText("Email: " + email);
        tvRating.setText("Rating: " + rating);
        tvReportCount.setText("Report count: " + report_count);
    }

    @Override
    public void onClick(View v) {
        Log.e("Click", "clicked");
        fFriendPostsFragment fragment = new fFriendPostsFragment();
        fragment.setArguments(extras);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.container, fragment);
        ft.commit();
    }

    private class GetFriendInfoTask extends AsyncTask<Context, Void, Boolean> {

        private String username, password, friend;
        public HashMap<String, String> hm;

        public GetFriendInfoTask(String username, String password, String friend) {
            this.username = username;
            this.password = password;
            this.friend = friend;
        }

        @Override
        protected Boolean doInBackground(Context... params) {
            HttpURLConnection conn = null;
            URL url = null;
            int response = 400;
            hm = new HashMap<String, String>();
            String query = String.format("username=%s&password=%s&friend=%s", username, password, friend);
            try {
                url = new URL("http://ythogh.com/shopwf/get_friend_info.php");
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
                String str = "";
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((inputLine = in.readLine()) != null) {
                    System.out.println(inputLine);
                    str = str + inputLine;
                }
                in.close();
                out.close();
                conn.disconnect();
                System.out.println("\n" + str);
                JSONObject obj = new JSONObject(str);
                hm.put("email", obj.getString("email"));
                hm.put("rating", obj.getString("rating"));
                hm.put("report_count", obj.getString("report_count"));
                return true;
            } catch (Exception e) {
                conn.disconnect();
                e.printStackTrace();
                return false;
            }
        }


        /**
         * @see #onPostExecute
         * @see #doInBackground
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            Log.e("Result", result + "");
            onGetFriendInfoTaskReturn(hm);
        }
    }


}