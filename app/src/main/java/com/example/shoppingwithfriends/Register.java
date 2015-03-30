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

import org.apache.http.HttpStatus;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@SuppressWarnings("ALL")
public class Register extends Activity {

    private EditText etName, etPassword;
    private static EditText etUsername;
    // --Commented out by Inspection (3/29/2015 8:14 PM):private TextView tvRegister;
    // --Commented out by Inspection (3/29/2015 8:14 PM):private HashMap<String, String> hm;
    public static String username = "", password = "", name = "";
    public static int color = Color.BLACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.e("initial call", "initial call");
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

    public static void onRegisterFail(Context mContext, Integer result) {
        int r = result;
        String msg = "";
        switch (result) {
            case HttpStatus.SC_CONFLICT:
                msg = "Username already taken!";
                break;
            case HttpStatus.SC_PRECONDITION_FAILED:
                msg = "Must enter a username!";
                break;
            case HttpStatus.SC_SERVICE_UNAVAILABLE:
                msg = "Cannot connect to server";
                break;
            default:
                msg = "An unexpected error has occured";
                break;

        }
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
        etUsername.setTextColor(Color.RED);
    }

    private void onRegisterSuccess() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("password", password);
        startActivity(intent);
    }

    private class RegisterTask extends AsyncTask<Context, Void, Integer> {

        private String name, username, password;
        private Context mContext;

        public RegisterTask(String name, String username, String password) {
            this.name = name;
            this.username = username;
            this.password = password;
        }

        @Override
        protected Integer doInBackground(Context... params) {
            mContext = params[0];
            HttpURLConnection conn = null;
            URL url = null;
            int response = 400;
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
            if (result == HttpStatus.SC_OK) {
                onRegisterSuccess();
            } else {
                Register.onRegisterFail(mContext, result);
                System.out.println("Problem");
            }
        }

    }


}
