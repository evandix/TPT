package com.androidy.tpt.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidy.tpt.R;
import com.androidy.tpt.adapter.TipAdapter;
import com.androidy.tpt.alarm.SetingsActivity;
import com.androidy.tpt.info.Tips;
import com.parse.ParseAnalytics;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Feed;
import com.sromku.simple.fb.listeners.OnPublishListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class HomeActivity extends ListActivity {

    ImageButton twitterButton;
    ImageButton faceBookButton;
    ImageButton emailButton;

    private TextView todaysDate;
    private TextView todaysTipText;
    String todaysTipMessage = "";
    private Tips todaysTip;




    ArrayList<Tips> mTipsArrayList;
    ArrayList<Tips> tipsForLastSixDays;

    private int mMonth;
    private int mDay;
    private GregorianCalendar gc;
    private static final String TAG = HomeActivity.class.getSimpleName();
    private Tips[] tipsArray;
    private String tipFromToday;
    private String date;
    private ImageButton infoButton;
    private ImageButton settingsButton;
    private ImageButton notifyButton;
    private ProgressBar mProgressBar;
    private ImageButton socailLink;
    private String linkFromTodaysTip = "";

    private SimpleFacebook mSimpleFacebook;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ParseAnalytics.trackAppOpened(getIntent());

        Map<String, String> dimensions = new HashMap<String, String>();

        dimensions.put("Shared", "twitter");

        ParseAnalytics.trackEvent("Shared", dimensions);

        mSimpleFacebook = SimpleFacebook.getInstance(this);

        socailLink = (ImageButton) findViewById(R.id.linkImageButton);







        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);


        infoButton = (ImageButton) findViewById(R.id.infoButton);
        settingsButton = (ImageButton) findViewById(R.id.settingsButton);
        notifyButton = (ImageButton) findViewById(R.id.notifyButton);

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToInfo = new Intent(HomeActivity.this , InfoActivity.class);
                HomeActivity.this.startActivity(goToInfo);


            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToSettings = new Intent(HomeActivity.this , SetingsActivity.class);
                HomeActivity.this.startActivity(goToSettings);
            }
        });

        notifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToNotify = new Intent(HomeActivity.this , NotifyActivity.class);
                HomeActivity.this.startActivity(goToNotify);
            }
        });



        twitterButton = (ImageButton) findViewById(R.id.twitterButton);
        faceBookButton = (ImageButton) findViewById(R.id.facebookButton);
        emailButton = (ImageButton) findViewById(R.id.emailButton);

        todaysDate = (TextView) findViewById(R.id.dateTextView);
        todaysTipText = (TextView) findViewById(R.id.todaysTipTextView);

        todaysTipText.setMovementMethod(new ScrollingMovementMethod());

        twitterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, String> dimensions = new HashMap<String, String>();
// What type of news is this?
                dimensions.put("Shared", "twitter");
// Is it a weekday or the weekend?

// Send the dimensions to Parse along with the 'read' event

                ParseAnalytics.trackEvent("Shared", dimensions);

                // Create intent using ACTION_VIEW and a normal Twitter url:
                String tweetUrl =
                        String.format("https://twitter.com/intent/tweet?text=%s&url=",
                                urlEncode(todaysTipMessage));
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));

// Narrow down to official Twitter app, if available:
                List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent, 0);
                for (ResolveInfo info : matches) {
                    if (info.activityInfo.packageName.toLowerCase().startsWith("com.twitter")) {
                        intent.setPackage(info.activityInfo.packageName);
                    }
                }

                startActivity(intent);


            }
        });

        faceBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, String> dimensions = new HashMap<String, String>();
// What type of news is this?
                dimensions.put("Shared", "Facebook");
// Is it a weekday or the weekend?

// Send the dimensions to Parse along with the 'read' event

                ParseAnalytics.trackEvent("Shared", dimensions);

                publishToFacebook(todaysTipMessage);

            }
        });

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, String> dimensions = new HashMap<String, String>();
// What type of news is this?
                dimensions.put("Shared", "Email");
// Is it a weekday or the weekend?

// Send the dimensions to Parse along with the 'read' event

                ParseAnalytics.trackEvent("Shared", dimensions);

                Intent shareIntent = new Intent(Intent.ACTION_SEND);



                shareIntent.setType("plain/text");
                shareIntent.putExtra(Intent.EXTRA_TEXT, todaysTipMessage);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Teaching Professor Tips");
                startActivity(Intent.createChooser(shareIntent, "Share using"));

            }
        });


        setUpList();


      

    }

    private String getMonthName(int month) {
        int monthInt = month;

        String monthName = "April";


        if (monthInt == 1 ) {
            monthName = "January";
        } else if (monthInt == 2) {
            monthName = "February";
        }  else if (monthInt == 3) {
            monthName = "March";
        }  else if (monthInt == 4) {
            monthName = "April";
        }  else if (monthInt == 5) {
            monthName = "May";
        }  else if (monthInt == 6) {
            monthName = "June";
        }  else if (monthInt == 7) {
            monthName = "July";
        }  else if (monthInt == 8) {
            monthName = "August";
        }  else if (monthInt == 9) {
            monthName = "September";
        }  else if (monthInt == 10) {
            monthName = "October";
        }  else if (monthInt == 11) {
            monthName = "November";
        }  else if (monthInt == 12) {
            monthName = "December";
        }

        return monthName;

    }

    private void setUpList() {
        String urlString = "http://intense-mountain-5585.herokuapp.com/tip_lists/1/categories/1/tips.json";

        if (networkIsAvailable()) {
            mProgressBar.setVisibility(View.VISIBLE);
                      // check to see is netwok is availibe before we get online

            OkHttpClient client = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url(urlString)
                    .build();

            Call call = client.newCall(request); // request wraped up in a call
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Looper.prepare();

                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                    builder.setMessage("Something went wrong. Please try again");
                    builder.setTitle("Sorry");
                    builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.show();
                    dialog.show();

                }

                @Override
                public void onResponse(Response response) throws IOException {

                    try {

                        String jsonData = response.body().string();
                       // Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                           mTipsArrayList = getTipArrayList(jsonData);
                        tipsForLastSixDays = searchThroughTips(mTipsArrayList);





                                    runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateList();




                                }
                            });




                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                            builder.setMessage("Something went wrong. Please try again");
                            builder.setTitle("Sorry");
                            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog dialog = builder.show();
                            dialog.show();

                        }
                    } catch (IOException e) {
                       // Log.e(TAG, "Exception caught", e);
                    }
                    catch (JSONException e) {
                       // Log.e(TAG, "Exception caught", e);
                    }
                }
            });
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setMessage("No internet connection could be established. Please try again.");
            builder.setTitle("Sorry");
            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.show();
            dialog.show();

        }
    }


    private String getTodaysTip(ArrayList<Tips> tipsArrayList) {

        String todaysTip = "";

        String key = getTodaysKey();

        for (int i = 0 ; i < tipsArrayList.size() ; i ++ ) {

            Tips tip = tipsArrayList.get(i);
            String monthNameFromTip = tip.getMonth();
            int dayIntFromTip = tip.getDay();

            String keyFromTip = monthNameFromTip + dayIntFromTip;

            if (key.equalsIgnoreCase(keyFromTip)) {

                String theTip = tip.getMessage();

                todaysTip = theTip;
            }

        }

        return todaysTip;


    }


    private ArrayList<Tips> searchThroughTips(ArrayList<Tips> tipsArrayList) {

        ArrayList<Tips> refinedSixTips = new ArrayList<Tips>();

       ArrayList<String> keys = getSixKeys();

        for (int i = 0 ; i < keys.size() ; i ++ ) {
            String keyForSearching = keys.get(i);

           // Log.v(TAG, keyForSearching);

            for (int ii = 0; ii < tipsArrayList.size(); ii++) {
                Tips tip = tipsArrayList.get(ii);
                String monthNameFromTip = tip.getMonth();
                int dayIntFromTip = tip.getDay();

                String keyFromTip = monthNameFromTip + dayIntFromTip;

                if (keyForSearching.equalsIgnoreCase(keyFromTip)) {

                    refinedSixTips.add(tip);
                  //  Log.v(TAG, "success");



                } else {
                  //  Log.v(TAG, "...");
                }
            }
        }
     //   Log.v(TAG, refinedSixTips.size() + "");
        return refinedSixTips;
    }

    private void updateList() {

        todaysTip = tipsForLastSixDays.get(0);


        linkFromTodaysTip = todaysTip.getLink();

        if (linkFromTodaysTip.length() > 5 ) {
            socailLink.setImageResource(R.drawable.whitelinkforsocial);
            socailLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(linkFromTodaysTip));
                    HomeActivity.this.startActivity(intent);
                }
            });
        }

      //  Log.v(TAG , linkFromTodaysTip);


        todaysTipMessage = todaysTip.getMessage();
       // Log.v(TAG, todaysTipMessage);
        todaysTipText.setText(todaysTipMessage);

          tipsForLastSixDays.remove(0);
       // Log.v(TAG, tipsForLastSixDays.size() + "");

     tipsArray = new Tips[tipsForLastSixDays
             .size()];
        tipsArray = tipsForLastSixDays.toArray(tipsArray);





        String todaysDatee = getDate(new GregorianCalendar());
        todaysDate.setText(todaysDatee);

        mProgressBar.setVisibility(View.INVISIBLE);



        TipAdapter adapter = new TipAdapter(HomeActivity.this , tipsArray);
        setListAdapter(adapter);

    }

    private ArrayList<Tips> getTipArrayList(String jsonResponse) throws JSONException {

        ArrayList<Tips> searchingArrayList = new ArrayList<Tips>();
        JSONArray jsonArray = new JSONArray(jsonResponse);
        GregorianCalendar gc = new GregorianCalendar();




            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Tips tipsForSorting = new Tips();


                String monthName = jsonObject.getString("month");
                int dayInt = jsonObject.getInt("day");
                String message = jsonObject.getString("content");
                String link = jsonObject.getString("link");


                tipsForSorting.setLink(link);
                tipsForSorting.setMessage(message);

                tipsForSorting.setMonth(monthName);
               // Log.v(TAG , monthName);
                tipsForSorting.setDay(dayInt);

                searchingArrayList.add(tipsForSorting);
            }
        return searchingArrayList;
        }

    private ArrayList<String> getSixKeys() {
        ArrayList<String> keyArrayListSix = new ArrayList<String>();
        GregorianCalendar gc = new GregorianCalendar();


        for (int i = 0 ; i < 6 ; i ++ ) {

            String key = getBaseKeyForTips(gc);
            keyArrayListSix.add(key);

            gc.add(Calendar.DATE, -1);


        }

        return  keyArrayListSix;

    }

    private String getTodaysKey() {
        GregorianCalendar gc = new GregorianCalendar();
        String key = getBaseKeyForTodaysTip(gc);
        return key;


    }


    private String getBaseKeyForTips(GregorianCalendar gc) {
        String monthName = "April";
        int monthInt = gc.get(Calendar.MONTH) + 1;
        int dayInt =  gc.get(Calendar.DATE);


        if (monthInt == 1 ) {
            monthName = "January";
        } else if (monthInt == 2) {
            monthName = "February";
        }  else if (monthInt == 3) {
            monthName = "March";
        }  else if (monthInt == 4) {
            monthName = "April";
        }  else if (monthInt == 5) {
            monthName = "May";
        }  else if (monthInt == 6) {
            monthName = "June";
        }  else if (monthInt == 7) {
            monthName = "July";
        }  else if (monthInt == 8) {
            monthName = "August";
        }  else if (monthInt == 9) {
            monthName = "September";
        }  else if (monthInt == 10) {
            monthName = "October";
        }  else if (monthInt == 11) {
            monthName = "November";
        }  else if (monthInt == 12) {
            monthName = "December";
        }

        return monthName + dayInt;    //ex April2

    }

    private String getBaseKeyForTodaysTip(GregorianCalendar gc) {
        String monthName = "April";
        int monthInt = gc.get(Calendar.MONTH) + 1;
        int dayInt =  gc.get(Calendar.DATE);


        if (monthInt == 1 ) {
            monthName = "January";
        } else if (monthInt == 2) {
            monthName = "February";
        }  else if (monthInt == 3) {
            monthName = "March";
        }  else if (monthInt == 4) {
            monthName = "April";
        }  else if (monthInt == 5) {
            monthName = "May";
        }  else if (monthInt == 6) {
            monthName = "June";
        }  else if (monthInt == 7) {
            monthName = "July";
        }  else if (monthInt == 8) {
            monthName = "August";
        }  else if (monthInt == 9) {
            monthName = "September";
        }  else if (monthInt == 10) {
            monthName = "October";
        }  else if (monthInt == 11) {
            monthName = "November";
        }  else if (monthInt == 12) {
            monthName = "December";
        }

        return monthName + dayInt;    //ex April2

    }

    private String getDate(GregorianCalendar gc) {
        String monthName = "April";
        int monthInt = gc.get(Calendar.MONTH) + 1;
        int dayInt =  gc.get(Calendar.DATE);
        int year = gc.get(Calendar.YEAR);


        if (monthInt == 1 ) {
            monthName = "January";
        } else if (monthInt == 2) {
            monthName = "February";
        }  else if (monthInt == 3) {
            monthName = "March";
        }  else if (monthInt == 4) {
            monthName = "April";
        }  else if (monthInt == 5) {
            monthName = "May";
        }  else if (monthInt == 6) {
            monthName = "June";
        }  else if (monthInt == 7) {
            monthName = "July";
        }  else if (monthInt == 8) {
            monthName = "August";
        }  else if (monthInt == 9) {
            monthName = "September";
        }  else if (monthInt == 10) {
            monthName = "October";
        }  else if (monthInt == 11) {
            monthName = "November";
        }  else if (monthInt == 12) {
            monthName = "December";
        }

        return monthName + " " + dayInt + ", " + year ;    //ex April2

    }


    private boolean networkIsAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();   // had to add permission to manifest
        boolean isAvailible = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailible = true;
        }
        return  isAvailible;
    }









    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
          //  Log.wtf(TAG, "UTF-8 should always be supported", e);
            throw new RuntimeException("URLEncoder.encode() failed for " + s);
        }
    }

    private void publishToFacebook(String tip){
        OnPublishListener onPublishListener = new OnPublishListener() {
            @Override
            public void onComplete(String postId) {
               // Log.i("", "Published successfully. The new post id = " + postId);
            }

            @Override
            public void onFail(String reason) {
                super.onFail(reason);


            }

        };

        Feed feed = new Feed.Builder()
                .setMessage(tip)
                .setName("TPT")
                .setCaption(tip)
                .setDescription(tip)

                .setPicture("http://mostbeastlystudios.com/wp-content/uploads/2015/03/iTunesArtwork@2x1-e1427410350476.png")

                .setLink("http://www.magnapubs.com/teaching-professor-conference/")
                .build();

        SimpleFacebook.getInstance().publish(feed, true, onPublishListener);
    }




}
