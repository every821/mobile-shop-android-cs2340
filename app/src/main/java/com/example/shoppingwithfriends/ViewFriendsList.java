package com.example.shoppingwithfriends;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpStatus;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class ViewFriendsList extends ActionBarActivity {

    String username, password;
    EditText etFindFriend;
    Context mContext;
    ListView lvAllFriends;
    int completed = 0;
    GridViewAdapter nadapter;
    FriendsListAdapter adapter;
    UsersListAdapter madapter;
    ArrayList<String> allFriends, searchedFriends;
    ArrayList<String> allUsers, searchedUsers;

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
                    searchedFriends = new SearchFriends().search(allFriends, s.toString());
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
    private void onGetFriendsReturn(ArrayList<String> arr) {
        allFriends = arr;
        searchedFriends = arr;
        updateAdapter();
        completed = 1;
    }

    /**
     * Removes friend from friends-list and adds to users-list
     * @param friend The friend that was removed
     */
    private void onRemoveFriendReturn(String friend) {
        allUsers.add(friend);
        allFriends.remove(friend);
        searchedFriends.remove(friend);
        updateAdapter();
    }

    /**
     * Updates the friends-list and users-list adapter on each add/remove task
     * Updates the onClickListener to allow deleting a friend or viewing his page
     */
    private void updateAdapter() {
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
    private void onAddFriendReturn(String friend) {
        allFriends.add(friend);
        updateAdapter();
        allUsers.remove(friend);
        searchedUsers.remove(friend);
    }

    /**
     * Returns an array of all users not on the friends-list
     * @param arr All the users not on the friends-list
     */
    private void onGetUsersReturn(ArrayList<String> arr) {
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

    private class SearchFriends {

        /**
         * @param items The objects to search
         * @param query The search parameter
         * @return The filtered objects
         */
        public ArrayList<String> search(ArrayList<String> items, String query) {
            query = query.toLowerCase(Locale.ENGLISH);
            ArrayList<String> results = new ArrayList<String>();
            for (String item : items) {
                if ((item != null) && item.toLowerCase(Locale.ENGLISH).contains(query)) {
                    results.add(item);
                }
            }
            return results;
        }
    }

    private class GetUsersTask extends AsyncTask<Context, Void, Boolean> {

        private String username, password, friend;
        private Context mContext;
        public ArrayList<String> arrlist;

        public GetUsersTask(String username, String password) {
            this.username = username;
            this.password = password;
        }

        /**
         * Connects to ythogh.com and gets all users in the database
         * @param params passes in getApplicationContext() from calling Activity
         * @return Result code from php request
         */
        @Override
        protected Boolean doInBackground(Context... params) {
            mContext = params[0];
            arrlist = new ArrayList<String>();
            HttpURLConnection conn = null;
            URL url = null;
            int response = 400;
            String query = String.format("username=%s&password=%s", username, password);
            try {
                url = new URL("http://ythogh.com/shopwf/scripts/get_users.php");
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
                return true;
            } catch (Exception e) {
                conn.disconnect();
                Log.e("Login", "Exception when logging in: " + response);
                e.printStackTrace();
                return false;
            }
        }

        /**
         * Updates uses-list with returned users' names
         * @param result Result code from php request
         */
        @Override
        protected void onPostExecute(Boolean result) {
            System.out.println("result: " + result);
            if (result) {
                onGetUsersReturn(arrlist);
            } else {
                Toast.makeText(mContext, "Problem occurred!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private class GetFriendsTask extends AsyncTask<Context, Void, Boolean> {

        private String username, password, friend;
        private Context mContext;
        public ArrayList<String> arrlist;

        public GetFriendsTask(String username, String password) {
            this.username = username;
            this.password = password;
        }

        /**
         * Connects to ythogh.com and requests friends of 'username'
         * @param params passes in getApplicationContext() from calling Activity
         * @return Result code from php request
         */
        @Override
        protected Boolean doInBackground(Context... params) {
            mContext = params[0];
            arrlist = new ArrayList<String>();
            HttpURLConnection conn = null;
            URL url = null;
            int response = 400;
            String query = String.format("username=%s&password=%s", username, password);
            try {
                url = new URL("http://ythogh.com/shopwf/scripts/get_friends.php");
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
                return true;
            } catch (Exception e) {
                conn.disconnect();
                Log.e("Login", "Exception when logging in: " + response);
                e.printStackTrace();
                return false;
            }
        }

        /**
         * Updates friends-list with returned friends' names
         * @param result Result code from php request
         */
        @Override
        protected void onPostExecute(Boolean result) {
            System.out.println("result: " + result);
            if (result) {
                onGetFriendsReturn(arrlist);
            } else {
                Toast.makeText(mContext, "Problem occurred!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class AddFriendTask extends AsyncTask<Context, Void, Integer> {

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
                url = new URL("http://ythogh.com/shopwf/scripts/add_friend.php");
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
                onAddFriendReturn(friend);
            } else {
                Toast.makeText(mContext, "Problem occurred!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private class RemoveFriendTask extends AsyncTask<Context, Void, Integer> {

        private String username, password, friend;
        private Context mContext;

        public RemoveFriendTask(String username, String password, String friend) {
            this.username = username;
            this.password = password;
            this.friend = friend;
        }

        /**
         * Connects to ythogh.com and removes 'friend' from 'username' friends-list
         * @param params passes in getApplicationContext() from calling Activity
         * @return Result code from php request
         */
        @Override
        protected Integer doInBackground(Context... params) {
            mContext = params[0];
            HttpURLConnection conn = null;
            URL url = null;
            int response = 400;
            String query = String.format("username=%s&password=%s&friend=%s", username, password, friend);
            try {
                url = new URL("http://ythogh.com/shopwf/scripts/remove_friend.php");
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
                Log.e("Remove friend", "Error " + response);
                e.printStackTrace();
                return HttpStatus.SC_SERVICE_UNAVAILABLE;
            }
        }

        /**
         * Updates friends-list and users-list with returned friends' names
         * @param result Result code from php request
         */
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            System.out.println("result: " + result);
            if (result == HttpStatus.SC_ACCEPTED) {
                onRemoveFriendReturn(friend);
            } else {

            }
        }
    }
}