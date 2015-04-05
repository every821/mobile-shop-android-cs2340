package com.example.shoppingwithfriends;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.NinePatchDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@SuppressWarnings("ALL")
public class Register extends Activity {

    private EditText etName, etPassword;
    private EditText etUsername;
    // --Commented out by Inspection (3/29/2015 8:14 PM):private TextView tvRegister;
    // --Commented out by Inspection (3/29/2015 8:14 PM):private HashMap<String, String> hm;
    public String username = "", password = "", name = "";
    public static int color = Color.BLACK;
    public Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.e("initial call", "initial call");
        mContext = getApplicationContext();
        NinePatchDrawable etError = (NinePatchDrawable) getResources().getDrawable(R.drawable.apptheme_textfield_activated_holo_light);
        etName = (EditText) findViewById(R.id.REGISTER_EDITTEXT_NAME);
        etName.setBackgroundResource(R.drawable.edittext);
        etName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                InputMethodManager imm = (InputMethodManager) getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                return true;
            }
        });
        etUsername = (EditText) findViewById(R.id.REGISTER_EDITTEXT_USERNAME);
        etUsername.setBackgroundResource(R.drawable.edittext);
        etUsername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                InputMethodManager imm = (InputMethodManager) getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }
        });
        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etUsername.getCurrentTextColor() == Color.RED) {
                    etUsername.setTextColor(Color.BLACK);
                }
            }
        });
        etPassword = (EditText) findViewById(R.id.REGISTER_EDITTEXT_PASSWORD);
        etPassword.setTransformationMethod(new PasswordTransformationMethod());
        etPassword.setBackgroundResource(R.drawable.edittext);
        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                InputMethodManager imm = (InputMethodManager) getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }
        });
        Button btRegister = (Button) findViewById(R.id.REGISTER_BUTTON_REGISTER);
        Button btCancel = (Button) findViewById(R.id.REGISTER_BUTTON_CANCEL);
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString();
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                new RegisterTask(name, username, password).execute(getApplicationContext());
            }
        });
    }

    @Override
    protected void onResume() {
        etUsername.setText(username);
        etUsername.setTextColor(color);
        etName.setText(name);
        super.onResume();
    }

    @Override
    protected void onPause() {
        username =  etUsername.getText().toString();
        name =  etName.getText().toString();
        color = etUsername.getCurrentTextColor();
        super.onPause();
    }

    public void onRegisterFail() {
        Toast.makeText(getApplicationContext(), "Can't register with that.", Toast.LENGTH_LONG).show();
        etUsername.setTextColor(Color.RED);
    }

    private void onRegisterSuccess() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("password", password);
        startActivity(intent);
    }

    private class RegisterTask extends AsyncTask<Context, Void, Boolean> {

        private String name, username, password;
        private Context mContext;

        public RegisterTask(String name, String username, String password) {
            this.name = name;
            this.username = username;
            this.password = password;
        }

        @Override
        protected Boolean doInBackground(Context... params) {
            HttpURLConnection conn = null;
            URL url = null;

            if (name.trim().length() == 0) {
                return false;
            }
            if (username.trim().length() == 0) {
                return false;
            }
            if (password.trim().length() == 0) {
                return false;
            }

            String query = String.format("name=%s&username=%s&password=%s", name, username, password);
            try {
                url = new URL("http://ythogh.com/shopwf/scripts/register.php");
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
                String res = in.readLine();
                boolean result = res.equals("1");
                in.close();
                conn.disconnect();
                out.close();
                return result;
            } catch (Exception e) {
                conn.disconnect();
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            System.out.println("result: " + result);
            if (result) {
                onRegisterSuccess();
            } else {
                onRegisterFail();
                System.out.println("Problem");
            }
        }

    }


}
