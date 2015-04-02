package com.androidy.tpt.alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.SettingInjectorService;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.androidy.tpt.R;
import com.androidy.tpt.ui.HomeActivity;

import java.util.Calendar;

public class SetingsActivity extends Activity {

    public static boolean isFromSplashActivity = false;

    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private TimePicker alarmTimePicker;
    private ToggleButton toggle1;
    private static SetingsActivity inst;
    private Button saveButton;


    private Button backButton;
    public static SetingsActivity instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setings);
        alarmTimePicker = (TimePicker) findViewById(R.id.alarmTimePicker);

        toggle1 = (ToggleButton) findViewById(R.id.alarmToggle);

        ToggleButton alarmToggle = (ToggleButton) findViewById(R.id.alarmToggle);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        backButton = (Button) findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetingsActivity.this , HomeActivity.class);
                SetingsActivity.this.startActivity(intent);
            }
        });

        toggle1.setChecked(getDefaults("etatToggle",this));

        toggle1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggle1.isChecked()) {

                    Log.d("MyActivity", "Alarm On");
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
                    calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
                    Intent myIntent = new Intent(SetingsActivity.this, AlarmReceiver.class);
                    pendingIntent = PendingIntent.getBroadcast(SetingsActivity.this, 0, myIntent, 0);
                    alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);




                } else {

                    alarmManager.cancel(pendingIntent);

                    Log.d("MyActivity", "Alarm Off");

                }

            }
        });

        saveButton = (Button) findViewById(R.id.save);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetingsActivity.this , HomeActivity.class);
                SetingsActivity.this.startActivity(intent);
            }
        });


    }

    @Override
    public void onStop(){
        super.onStop();

        setDefaults("etatToggle", toggle1.isChecked(), this);
    }



        public static void setDefaults(String key, Boolean value, Context context)
        {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(key, value);
            editor.commit();
        }

    public static Boolean getDefaults(String key, Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(key, true);
    }


}