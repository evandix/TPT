package com.androidy.tpt.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.androidy.tpt.R;
import com.androidy.tpt.alarm.SetingsActivity;

public class SplashActivity extends Activity {

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
                    SharedPreferences.Editor editor = preferences.edit();
                    int i = preferences.getInt("numberoflaunches", 1);

                    if (i < 2){
                        Intent mainIntent = new Intent(SplashActivity.this, SetingsActivity.class);
                        SplashActivity.this.startActivity(mainIntent);
                        SplashActivity.this.finish();
                        SetingsActivity.isFromSplashActivity = true;

                        i++;
                        editor.putInt("numberoflaunches", i);
                        editor.commit();
                    } else {



                /* Create an Intent that will start the Menu-Activity. */
                        Intent mainIntent = new Intent(SplashActivity.this, HomeActivity.class);
                        SplashActivity.this.startActivity(mainIntent);
                        SplashActivity.this.finish();
                    }
                }
            }, SPLASH_DISPLAY_LENGTH);
        }
    }