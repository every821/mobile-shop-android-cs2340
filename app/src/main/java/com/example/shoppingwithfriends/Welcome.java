package com.example.shoppingwithfriends;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * The Welcome screen for the application
 */
@SuppressWarnings("StatementWithEmptyBody")
@SuppressLint("ShowToast")
public class Welcome extends Activity {

    private final static int LOGIN_CODE = 100,REGISTER_CODE=101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Button btLogin = (Button) findViewById(R.id.WELCOME_BUTTON_LOGIN);
        Button btRegister = (Button) findViewById(R.id.WELCOME_BUTTON_REGISTER);
        btLogin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivityForResult(intent, LOGIN_CODE);
            }
        });
        btRegister.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivityForResult(intent, REGISTER_CODE);
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_CODE) {

        } else if (requestCode == REGISTER_CODE) {

        }
    }
}
