package com.androidy.tpt.alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.androidy.tpt.R;
import com.androidy.tpt.ui.HomeActivity;

import java.lang.reflect.Field;
import java.util.Calendar;

public class SetingsActivity extends Activity {

    public static boolean isFromSplashActivity = false;

    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private TimePicker timePicker;
    private ToggleButton toggle1;
    private static SetingsActivity inst;
    private Button saveButton;
    private boolean isSet = false;


    private Button cancelButton;
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
       timePicker = (TimePicker) findViewById(R.id.alarmTimePicker);

        setTimePickerTextColor(timePicker);

        timePicker.setIs24HourView(false);

        if (!isSet) {
            int min = getMinFromSharedPrefs(SetingsActivity.this);
            int hour = getHourFromSharedPrefs(SetingsActivity.this);

            timePicker.setCurrentHour(hour);
            timePicker.setCurrentMinute(min);
        }

        toggle1 = (ToggleButton) findViewById(R.id.alarmToggle);

        ToggleButton alarmToggle = (ToggleButton) findViewById(R.id.alarmToggle);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        cancelButton = (Button) findViewById(R.id.cancelButton);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetingsActivity.this , HomeActivity.class);
                SetingsActivity.this.startActivity(intent);

                setTheCurrentSetTime(SetingsActivity.this , timePicker);
            }
        });

        toggle1.setChecked(getDefaults("etatToggle",this));

        toggle1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggle1.isChecked()) {




                } else {



                  //  Log.d("MyActivity", "Alarm Off");

                }

            }
        });

        saveButton = (Button) findViewById(R.id.save);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (toggle1.isChecked()) {
                  //  Log.d("MyActivity", "Alarm On");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                    calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                    Intent myIntent = new Intent(SetingsActivity.this, AlarmReceiver.class);
                    pendingIntent = PendingIntent.getBroadcast(SetingsActivity.this, 0, myIntent, 0);
                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY , pendingIntent);

                } else {
                    alarmManager.cancel(pendingIntent);
                }

                setDefaults("etatToggle", toggle1.isChecked(), SetingsActivity.this);

                Intent intent = new Intent(SetingsActivity.this , HomeActivity.class);
                SetingsActivity.this.startActivity(intent);

                setTheCurrentSetTime(SetingsActivity.this , timePicker);

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

    private void setTimePickerTextColor(TimePicker timePicker){
        Resources system = Resources.getSystem();
        int hour_numberpicker_id = system.getIdentifier("hour", "id", "android");
        int minute_numberpicker_id = system.getIdentifier("minute", "id", "android");
        int ampm_numberpicker_id = system.getIdentifier("amPm", "id", "android");

        NumberPicker hour_numberpicker = (NumberPicker) timePicker.findViewById(hour_numberpicker_id);
        NumberPicker minute_numberpicker = (NumberPicker) timePicker.findViewById(minute_numberpicker_id);
        NumberPicker ampm_numberpicker = (NumberPicker) timePicker.findViewById(ampm_numberpicker_id);

        setNumberPickerTextColor(hour_numberpicker);
        setNumberPickerTextColor(minute_numberpicker);
        setNumberPickerTextColor(ampm_numberpicker);
    }

    private void setNumberPickerTextColor(NumberPicker number_picker){
        final int count = number_picker.getChildCount();
        final int color = getResources().getColor(android.R.color.white);

        for(int i = 0; i < count; i++){
            View child = number_picker.getChildAt(i);

            try{
                Field wheelpaint_field = number_picker.getClass().getDeclaredField("mSelectorWheelPaint");
                wheelpaint_field.setAccessible(true);

                ((Paint)wheelpaint_field.get(number_picker)).setColor(color);
                ((EditText)child).setTextColor(color);
                number_picker.invalidate();
            }
            catch(NoSuchFieldException e){
              //  Log.w("setNumberPickerTextColor", e);
            }
            catch(IllegalAccessException e){
                // Log.w("setNumberPickerTextColor", e);
            }
            catch(IllegalArgumentException e){
              //  Log.w("setNumberPickerTextColor", e);
            }
        }
    }

    private void setTheCurrentSetTime(Context context , TimePicker timePicker) {
        int hour  = timePicker.getCurrentHour();
        int min = timePicker.getCurrentMinute();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("Hour" , hour );
        editor.putInt("Min" , min );
        editor.commit();
        isSet = true;


    }

    private int getHourFromSharedPrefs(Context context) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt("Hour", 12);

    }
    private int getMinFromSharedPrefs(Context context) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt("Min", 12);

    }






}