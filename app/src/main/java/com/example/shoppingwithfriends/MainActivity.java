package com.example.shoppingwithfriends;

import android.app.Activity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    TextView tvText1, tvText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle extras = getIntent().getExtras();
        tvText1 = (TextView) findViewById(R.id.MAIN_TEXTVIEW_TEXT1);
        tvText2 = (TextView) findViewById(R.id.MAIN_TEXTVIEW_TEXT2);
        tvText1.setText("Username: " + extras.get("username"));
        tvText2.setText("Password: " + extras.get("password"));
    }
}
