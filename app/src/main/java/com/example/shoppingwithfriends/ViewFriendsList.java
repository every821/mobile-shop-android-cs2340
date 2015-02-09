package com.example.shoppingwithfriends;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ViewFriendsList extends ActionBarActivity implements SearchView.OnQueryTextListener {

    static String username, password;
    EditText etFindFriend;
    Button btAddFriend;
    static Context mContext;
    static ListView lvAllFriends;
    static int completed = 0;
    static FriendsListAdapter adapter;
    static UsersListAdapter madapter;
    static ArrayList<String> allFriends, searchedFriends;
    static ArrayList<String> allUsers, searchedUsers;

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
        btAddFriend = (Button) findViewById(R.id.VIEWFRIENDSLIST_BUTTON_ADDFRIEND);
        btAddFriend.setVisibility(View.GONE);
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
                adapter = new FriendsListAdapter(mContext, searchedFriends);
                //adapter = new ArrayAdapter<String>(mContext, R.layout.friends_list_item, searchedFriends);
                lvAllFriends.setAdapter(adapter);
            }
        });

        btAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddFriendTask(username, password, etFindFriend.getText().toString()).execute(getApplicationContext());
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
            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Log.e("TAG", "Click OK");
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
                public void onClick(DialogInterface dialog, int position) {
                    new AddFriendTask(username, password, searchedUsers.get(position)).execute(mContext);
                }
            });
            builder.setView(input);
            AlertDialog alertDialog = builder.create();

            alertDialog.show();
        }
        return false;
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
        completed = 1;
    }

    public static void onAddFriendReturn(String friend) {
        allFriends.add(friend);
        adapter = new FriendsListAdapter(mContext, allFriends);
        lvAllFriends.setAdapter(adapter);
        lvAllFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Click", "Item clicked: " + position);
            }
        });
        allUsers.remove(friend);
        searchedUsers.remove(friend);
    }

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
            System.out.println("Removed " + s);
            allUsers.remove(s);
        }
        searchedUsers = allUsers;
        madapter = new UsersListAdapter(mContext, allUsers);
    }

    /**
     * Called when the user submits the query. This could be due to a key press on the
     * keyboard or due to pressing a submit button.
     * The listener can override the standard behavior by returning true
     * to indicate that it has handled the submit request. Otherwise return false to
     * let the SearchView handle the submission by launching any associated intent.
     *
     * @param query the query text that is to be submitted
     * @return true if the query has been handled by the listener, false to let the
     * SearchView perform the default action.
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    /**
     * Called when the query text is changed by the user.
     *
     * @param newText the new content of the query text field.
     * @return false if the SearchView should perform the default action of showing any
     * suggestions if available, true if the action was handled by the listener.
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }
}
