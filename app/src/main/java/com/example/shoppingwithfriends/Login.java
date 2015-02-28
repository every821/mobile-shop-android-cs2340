package com.example.shoppingwithfriends;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import org.apache.http.HttpStatus;

public class Login extends Activity {

    private Button btLogin, btCancel;
    private EditText etUsername, etPassword;
    private static TextView tvInvalidLogin;
    public static String username, password;

    /**
     *
     * @param savedInstanceState Loads the state from the previous time the activity was started
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btLogin = (Button) findViewById(R.id.LOGIN_BUTTON_LOGIN);
        btCancel = (Button) findViewById(R.id.LOGIN_BUTTON_CANCEL);
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
                new LoginTask(etUsername.getText().toString().trim(), etPassword.getText().toString())
                        .execute(Login.this.getApplicationContext());
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
    public static void onLoginFail(Context mContext, Integer result) {
        int r = (int) result;
        String msg = "";
        switch (r) {
            case HttpStatus.SC_SERVICE_UNAVAILABLE:
                msg = "Cannot connect to server";
                break;
            case HttpStatus.SC_UNAUTHORIZED:
                msg = "Invalid credentials!";
                break;
            default:
                msg = "An unexpected error has occured";
                break;
        }
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        tvInvalidLogin.setVisibility(View.VISIBLE);
    }

    /**
     * Displays login success message on login success
     */
    public static void onLoginSuccess() {
        tvInvalidLogin.setVisibility(View.GONE);
    }

}
