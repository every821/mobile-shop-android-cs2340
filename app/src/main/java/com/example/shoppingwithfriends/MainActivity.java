package com.example.shoppingwithfriends;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

    TextView tvText1, tvText2;
    String username, password;
    EditText etAddFriend;
    Button btAddFriend, btGetFriends;
    static ListView lvAllFriends;
    static ArrayAdapter<String> adapter;
    static Context mContext;
    GridLayout gl;
    GridView gv;
    ImageView ivPost, ivShare, ivProfile, ivFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        Bundle extras = getIntent().getExtras();
        username = extras.getString("username");
        password = extras.getString("password");
        ivPost = (ImageView) findViewById(R.id.MAIN_IMAGEVIEW_POST);
        ivShare = (ImageView) findViewById(R.id.MAIN_IMAGEVIEW_SHARE);
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
        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Share", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(mContext, "Friends", Toast.LENGTH_SHORT).show();
            }
        });


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
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
