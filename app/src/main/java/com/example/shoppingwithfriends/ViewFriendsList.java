package com.example.shoppingwithfriends;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ViewFriendsList extends ActionBarActivity {

    String username, password;
    EditText etAddFriend;
    Button btAddFriend;
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
        btAddFriend = (Button) findViewById(R.id.VIEWFRIENDSLIST_BUTTON_ADDFRIEND);;
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
        etAddFriend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
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

        btAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddFriendTask(username, password, etAddFriend.getText().toString()).execute(getApplicationContext());
            }
        });
    }

    /**
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_friendslist) {
            Toast.makeText(getApplicationContext(), "Add friend!", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_friends_list, menu);
        Log.e("Created", "yea");
        return true;
    }
    /**
     *
     * @param arr Resulting list of friends returned from server
     */
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
