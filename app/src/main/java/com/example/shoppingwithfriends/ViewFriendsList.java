package com.example.shoppingwithfriends;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ViewFriendsList extends ActionBarActivity {

    static String username, password;
    EditText etFindFriend;
    static Context mContext;
    static ListView lvAllFriends;
    static int completed = 0;
    static GridViewAdapter nadapter;
    static FriendsListAdapter adapter;
    static UsersListAdapter madapter;
    static ArrayList<String> allFriends, searchedFriends;
    static ArrayList<String> allUsers, searchedUsers;
    static View viewWithFocus;

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
        allUsers = new ArrayList<String>();
        searchedUsers = new ArrayList<String>();

        etFindFriend = (EditText) findViewById(R.id.VIEWFRIENDSLIST_EDITTEXT_ADDFRIEND);
        lvAllFriends = (ListView) findViewById(R.id.VIEWFRIENDSLIST_LISTVIEW_FRIENDS);

        new GetFriendsTask(username, password).execute(getApplicationContext());
        new GetUsersTask(username, password).execute(getApplicationContext());
        etFindFriend.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                InputMethodManager imm = (InputMethodManager) getBaseContext().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }
        });
        etFindFriend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() == 0) {
                    searchedFriends = allFriends;
                } else {
                    searchedFriends = SearchFriends.search(allFriends, s.toString());
                }
                updateAdapter();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_friends_list, menu);
        return true;
    }

    /**
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_FRIENDSLIST_ICON_ADD) {
            Toast.makeText(getApplicationContext(), "Hit!", Toast.LENGTH_SHORT).show();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Find a user");
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.setIcon(R.drawable.ic_action_add_person_light);
            final EditText input = new EditText(ViewFriendsList.this);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            input.setHint("Search all users");
            input.setLayoutParams(lp);
            builder.setAdapter(madapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new AddFriendTask(username, password, searchedUsers.get(which)).execute(mContext);
                }
            });
            //builder.setView(input);
            final AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        return false;
    }

    /**
     * Adds all friends to the friends-list adapter
     * @param arr Resulting list of friends returned from server
     */
    public static void onGetFriendsReturn(ArrayList<String> arr) {
        allFriends = arr;
        searchedFriends = arr;
        updateAdapter();
        completed = 1;
    }

    /**
     * Removes friend from friends-list and adds to users-list
     * @param friend The friend that was removed
     */
    public static void onRemoveFriendReturn(String friend) {
        allUsers.add(friend);
        allFriends.remove(friend);
        searchedFriends.remove(friend);
        updateAdapter();
    }

    /**
     * Updates the friends-list and users-list adapter on each add/remove task
     * Updates the onClickListener to allow deleting a friend or viewing his page
     */
    private static void updateAdapter() {
        nadapter = new GridViewAdapter(mContext, searchedFriends);
        lvAllFriends.setAdapter(nadapter);
        lvAllFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (nadapter.isOpen(position)) {
                    new RemoveFriendTask(username, password, searchedFriends.get(position)).execute(mContext);
                } else {
                    Intent intent = new Intent(mContext, fViewFriend.class);
                    intent.putExtra("username", username);
                    intent.putExtra("password", password);
                    intent.putExtra("friend", searchedFriends.get(position));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            }
        });
    }

    /**
     * Adds friend to friends-list and removes from users-list
     * @param friend The friend that was added
     */
    public static void onAddFriendReturn(String friend) {
        allFriends.add(friend);
        updateAdapter();
        allUsers.remove(friend);
        searchedUsers.remove(friend);
    }

    /**
     * Returns an array of all users not on the friends-list
     * @param arr All the users not on the friends-list
     */
    public static void onGetUsersReturn(ArrayList<String> arr) {
        allUsers = arr;
        while (completed != 1);
        Set<String> set = new HashSet<String>();
        set.add(username);
        for (String friend : allFriends) {
            for (String user : allUsers) {
                if (friend.equals(user)) {
                    set.add(user);
                }
            }
        }

        for (String s : set) {
            allUsers.remove(s);
        }
        searchedUsers = allUsers;
        madapter = new UsersListAdapter(mContext, allUsers);
    }
}