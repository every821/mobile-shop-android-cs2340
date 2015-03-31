package com.example.shoppingwithfriends;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@SuppressWarnings("ALL")
public class Login extends Activity {

    private Button btLogin;
    private EditText etUsername, etPassword;
    private TextView tvInvalidLogin;
    private String username, password;
    public static User user;
    public static Context mContext;

    /**
     * @param savedInstanceState Loads the state from the previous time the activity was started
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = getApplicationContext();
        btLogin = (Button) findViewById(R.id.LOGIN_BUTTON_LOGIN);
        Button btCancel = (Button) findViewById(R.id.LOGIN_BUTTON_CANCEL);
        etUsername = (EditText) findViewById(R.id.LOGIN_EDITTEXT_USERNAME);
        etUsername.setBackgroundResource(R.drawable.edittext);
        etPassword = (EditText) findViewById(R.id.LOGIN_EDITTEXT_PASSWORD);
        etPassword.setBackgroundResource(R.drawable.edittext);
        etPassword.setTransformationMethod(new PasswordTransformationMethod());
        etUsername.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                InputMethodManager imm = (InputMethodManager) getBaseContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }

        });
        etPassword.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                InputMethodManager imm = (InputMethodManager) getBaseContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }

        });
        tvInvalidLogin = (TextView) findViewById(R.id.LOGIN_TEXTVIEW_INVALIDLOGIN);
        btLogin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                username = etUsername.getText().toString().trim();
                password = etPassword.getText().toString().trim();
                new LoginTask(username, password).execute(Login.this.getApplicationContext());
            }
        });
        btCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }

        });
    }

// --Commented out by Inspection START (3/29/2015 8:13 PM):
//    public static User getUser() {
//        return user;
//    }
// --Commented out by Inspection STOP (3/29/2015 8:13 PM)

    @Override
    protected void onResume() {
        etUsername.setText(username);
        super.onResume();
    }

    @Override
    protected void onPause() {
        username =  etUsername.getText().toString();
        super.onPause();
    }

    /**
     * Displays login error message on login fail
     */
    public static void onLoginFail() {
        System.out.println("Problem");
        Toast.makeText(mContext, "Problem logging in", Toast.LENGTH_SHORT).show();

    }

    /**
     * Displays login success message on login success
     */
    public static void onLoginSuccess() {
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    public static class LoginTask extends AsyncTask<Context, Void, Boolean> {

        private String username, password;

        public LoginTask(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected Boolean doInBackground(Context... params) {
            HttpURLConnection conn = null;
            URL url = null;
            int response = 9999;
            String query = String.format("username=%s&password=%s&action=app", username, password);
            System.out.println(query);
            try {
                url = new URL("http://ythogh.com/shopwf/scripts/verify_login.php");
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

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine = "";
                user = new User(username, password);
                user.setLow(in.readLine());
                user.setHigh(in.readLine());
                if (user.getLow().equals("null")) {
                    throw new Exception("Invalid credentials");
                }
                System.out.println(user.getLow() + "," + user.getHigh());
                response = conn.getResponseCode();
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

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                onLoginSuccess();
            } else {
                onLoginFail();
            }
        }
    }
}
