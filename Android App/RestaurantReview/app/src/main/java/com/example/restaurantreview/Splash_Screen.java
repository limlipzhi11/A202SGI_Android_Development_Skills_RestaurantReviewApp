package com.example.restaurantreview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class Splash_Screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override

            public void run() {

                //Change Activity
                Intent i = new Intent(Splash_Screen.this, Login_Screen.class);
                startActivity(i);

                //End Activity
                finish();

            }

        }, 3*1000); // wait for 5 seconds
    }
}
