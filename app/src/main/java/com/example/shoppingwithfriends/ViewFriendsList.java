package com.example.shoppingwithfriends;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ViewFriendsList extends Activity {

    String username, password;
    EditText etAddFriend;
    Button btAddFriend, btGetFriends, btFindFriend;
    static ListView lvAllFriends;
    static FriendsListAdapter adapter;
    static Context mContext;
    int state = 0;
    static ArrayList<String> allFriends, searchedFriends;
    static ArrayList<String> allUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friends_list);
        mContext = getApplicationContext();
        Bundle extras = getIntent().getExtras();
        username = extras.getString("username");
        password = extras.getString("password");

        allFriends = new ArrayList<String>();
        searchedFriends = new ArrayList<String>();
        etAddFriend = (EditText) findViewById(R.id.VIEWFRIENDSLIST_EDITTEXT_ADDFRIEND);
        btAddFriend = (Button) findViewById(R.id.VIEWFRIENDSLIST_BUTTON_ADDFRIEND);
        btGetFriends = (Button) findViewById(R.id.VIEWFRIENDSLIST_BUTTON_GETFRIENDS);
        btGetFriends.setVisibility(View.GONE);
        btFindFriend = (Button) findViewById(R.id.VIEWFRIENDSLIST_BUTTON_FINDFRIEND);
        lvAllFriends = (ListView) findViewById(R.id.VIEWFRIENDSLIST_LISTVIEW_FRIENDS);

        new GetFriendsTask(username, password).execute(getApplicationContext());
        etAddFriend.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                InputMethodManager imm = (InputMethodManager) getBaseContext().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                return true;
            }
        });

        btAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddFriendTask(username, password, etAddFriend.getText().toString()).execute(getApplicationContext());
            }
        });
        btFindFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etAddFriend.getText().toString().trim().length() == 0) {
                    searchedFriends = allFriends;
                } else {
                    searchedFriends = SearchFriends.search(allFriends, etAddFriend.getText().toString());
                }
                adapter = new FriendsListAdapter(mContext, searchedFriends);
                //adapter = new ArrayAdapter<String>(mContext, R.layout.friends_list_item, searchedFriends);
                lvAllFriends.setAdapter(adapter);
            }
        });
    }

    public static void onGetFriendsReturn(ArrayList<String> arr) {
        allFriends = arr;
        searchedFriends = arr;
        adapter = new FriendsListAdapter(mContext, arr);
        lvAllFriends.setAdapter(adapter);
        lvAllFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Click", "Item clicked: " + position);
            }
        });
    }
}
