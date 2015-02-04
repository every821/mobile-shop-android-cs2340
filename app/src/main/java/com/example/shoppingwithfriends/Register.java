package com.example.shoppingwithfriends;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.NinePatchDrawable;
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

import java.util.HashMap;

public class Register extends Activity {

    private EditText etName, etPassword;
    private static EditText etUsername;
    private TextView tvRegister;
    private Button btRegister, btCancel;
    private NinePatchDrawable etError;
    private HashMap<String, String> hm;
    private MyDB mydb;
    public static String username = "", password = "", name = "";
    public static int color = Color.BLACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mydb =  new MyDB(this);
        mydb.open();
        Log.e("initial call", "initial call");
        etError = (NinePatchDrawable) getResources().getDrawable(R.drawable.apptheme_textfield_activated_holo_light);
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
        btRegister = (Button) findViewById(R.id.REGISTER_BUTTON_REGISTER);
        btCancel = (Button) findViewById(R.id.REGISTER_BUTTON_CANCEL);
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

    public static void onRegisterSucceed() {

    }

    public static void onRegisterFail(Context mContext, Integer result) {
        int r = (int) result;
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

}
