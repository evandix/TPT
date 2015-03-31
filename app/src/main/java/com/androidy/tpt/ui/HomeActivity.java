package com.androidy.tpt.ui;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.androidy.tpt.R;
import com.androidy.tpt.adapter.TipAdapter;
import com.androidy.tpt.info.Tips;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;




public class HomeActivity extends ListActivity {

    ImageButton twitterButton;
    ImageButton faceBookButton;
    ImageButton emailButton;

    private TextView todaysDate;
    private TextView todaysTipText;
    String todaysTipMessage = "";




    ArrayList<Tips> mTipsArrayList;
    ArrayList<Tips> tipsForLastSixDays;
    private int mMonth;
    private int mDay;
    private GregorianCalendar gc;
    private static final String TAG = HomeActivity.class.getSimpleName();
    private Tips[] tipsArray;
    private String tipFromToday;
    private String date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        twitterButton = (ImageButton) findViewById(R.id.twitterButton);
        faceBookButton = (ImageButton) findViewById(R.id.facebookButton);
        emailButton = (ImageButton) findViewById(R.id.emailButton);

        todaysDate = (TextView) findViewById(R.id.dateTextView);
        todaysTipText = (TextView) findViewById(R.id.todaysTipTextView);


        twitterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

            }
        });

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent shareIntent = new Intent(Intent.ACTION_SEND);



                shareIntent.setType("plain/text");
                shareIntent.putExtra(Intent.EXTRA_TEXT, todaysTipMessage);
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

        if (networkIsAvailable()) {               // check to see is netwok is availibe before we get online

            OkHttpClient client = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url(urlString)
                    .build();

            Call call = client.newCall(request); // request wraped up in a call
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
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

                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught", e);
                    }
                    catch (JSONException e) {
                        Log.e(TAG, "Exception caught", e);
                    }
                }
            });
        } else {
            Toast.makeText(HomeActivity.this , "No connection" , Toast.LENGTH_SHORT).show();

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

            Log.v(TAG, keyForSearching);

            for (int ii = 0; ii < tipsArrayList.size(); ii++) {
                Tips tip = tipsArrayList.get(ii);
                String monthNameFromTip = tip.getMonth();
                int dayIntFromTip = tip.getDay();

                String keyFromTip = monthNameFromTip + dayIntFromTip;

                if (keyForSearching.equalsIgnoreCase(keyFromTip)) {

                    refinedSixTips.add(tip);
                    Log.v(TAG, "success");



                } else {
                    Log.v(TAG, "...");
                }
            }
        }
        Log.v(TAG, refinedSixTips.size() + "");
        return refinedSixTips;
    }

    private void updateList() {

        Tips todaysTip = tipsForLastSixDays.get(0);
        todaysTipMessage = todaysTip.getMessage();
        Log.v(TAG, todaysTipMessage);
        todaysTipText.setText(todaysTipMessage);

          tipsForLastSixDays.remove(0);
        Log.v(TAG, tipsForLastSixDays.size() + "");

     tipsArray = new Tips[tipsForLastSixDays
             .size()];
        tipsArray = tipsForLastSixDays.toArray(tipsArray);

        String todaysDatee = getDate(new GregorianCalendar());
        todaysDate.setText(todaysDatee);




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
                Log.v(TAG , monthName);
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
        int monthInt = gc.get(Calendar.MONTH) + 2;
        int dayInt =  gc.get(Calendar.DATE) - 1;


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
        int monthInt = gc.get(Calendar.MONTH) + 2;
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
        int monthInt = gc.get(Calendar.MONTH) + 2;
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

    /*
            GregorianCalendar gc = new GregorianCalendar();

        //gc.get(Calendar.MONTH);

        ArrayList<String> m = new ArrayList<String>();
do {
    int month = gc.get(Calendar.MONTH) + 1; //

    String monthName = getMonthName(month);
    int day = gc.get(Calendar.DATE);


    Log.v(TAG, monthName + "" + day);
    m.add("a");

    gc.add(Calendar.DATE, -1);

} while (m.size() < 30);

    }
     */

    /**
     * Share to Twitter
     *
     * @param tip
     */
    private void sendEmail(String emailAddress, String tip) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
                Uri.fromParts("mailto", emailAddress, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Teaching Professor Tips");
        emailIntent.putExtra(Intent.EXTRA_TEXT, tipFromToday);
        startActivity(Intent.createChooser(emailIntent, "Send email:"));
    }


    /*


    private void publishToFacebook(String tip){
        OnPublishListener onPublishListener = new OnPublishListener() {
            @Override
            public void onComplete(String postId) {
                Log.i("", "Published successfully. The new post id = " + postId);
                TtfpManager.getInstance().trackShareSuccessful(TtfpManager.SHARE_VIA.FACEBOOK);
            }

            @Override
            public void onFail(String reason) {
                super.onFail(reason);

                TtfpManager.getInstance().trackShareIncomplete(TtfpManager.SHARE_VIA.FACEBOOK);
            }


              You can override other methods here:
             onThinking(), onFail(String reason), onException(Throwable throwable)

        };

        Feed feed = new Feed.Builder()
                .setMessage(tip)
                .setName(getString(R.string.ttfp_fullname))
                .setCaption(tip)
                .setDescription(tip)
                .setPicture(getString(R.string.logo_url))
                .setLink(getString(R.string.ttfp_website))
                .build();

        SimpleFacebook.getInstance().publish(feed, true, onPublishListener);
    }


                */


    /**
     * Share to Twitter
     *
     * @para

    private void publishToTwitter(String tip) {
    Intent intent = new Intent(getApplicationContext(), Webview4TwitterActivity.class);
    intent.putExtra("tip", tip);
    startActivity(intent);
    }

     */

    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            Log.wtf(TAG, "UTF-8 should always be supported", e);
            throw new RuntimeException("URLEncoder.encode() failed for " + s);
        }
    }


}
