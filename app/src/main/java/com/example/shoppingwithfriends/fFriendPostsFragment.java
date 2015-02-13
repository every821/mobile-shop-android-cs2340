package com.example.shoppingwithfriends;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class fFriendPostsFragment extends Fragment implements View.OnClickListener {

    FragmentActivity listener;
    String username, password, friend;
    Bundle extras;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.listener = (FragmentActivity) activity;
        Log.e("Attached", "Friend posts");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_f_friend_posts, container, false);
        Button btReturn = (Button) v.findViewById(R.id.FRIENDPOSTS_BUTTON_RETURN);
        btReturn.setOnClickListener(this);
        extras = getArguments();
        username = extras.getString("username");
        password = extras.getString("password");
        friend = extras.getString("friend");
        return v;
    }

    @Override
    public void onClick(View v) {
        Log.e("Click", "clicked");
        fViewFriendFragment fragment = new fViewFriendFragment();
        fragment.setArguments(extras);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.container, fragment);
        ft.commit();
    }
}