package com.example.shoppingwithfriends;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


@SuppressWarnings("ALL")
public class fViewFriend extends ActionBarActivity {

    // --Commented out by Inspection (3/29/2015 8:13 PM):TextView tvFriend, tvEmail, tvRating, tvReportCount;
    // --Commented out by Inspection (3/29/2015 8:13 PM):String username, password, friend;
    // --Commented out by Inspection (3/29/2015 8:13 PM):Context mContext;
    // --Commented out by Inspection (3/29/2015 8:13 PM):static HashMap<String, String> hm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_f_view_friend);

        Bundle extras = getIntent().getExtras();
        String username = extras.getString("username");
        String password = extras.getString("password");
        String friend = extras.getString("friend");

        fViewFriendFragment fragment = new fViewFriendFragment();
        fragment.setArguments(extras);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.container, fragment);
        ft.commit();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_f_view_friend, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_f_view_friend, container, false);
        }
    }


}
