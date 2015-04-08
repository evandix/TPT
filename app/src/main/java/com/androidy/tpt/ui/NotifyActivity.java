package com.androidy.tpt.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.androidy.tpt.R;

public class NotifyActivity extends Activity {

    private Button backButton;

    private Button goToWebSite;
    private Button emailTheDev;
    private Button postATip;
    private Button rateUsOnApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);

        backButton = (Button) findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotifyActivity.this, HomeActivity.class);
                NotifyActivity.this.startActivity(intent);
            }
        });

        goToWebSite = (Button) findViewById(R.id.vistSiteButton);
        emailTheDev = (Button) findViewById(R.id.emailTheDeveloperButton);
        postATip = (Button) findViewById(R.id.submitATipButton);
        rateUsOnApp = (Button) findViewById(R.id.rateUsButton);

        goToWebSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.magnapubs.com/"));
                NotifyActivity.this.startActivity(intent);
            }
        });
        emailTheDev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("plain/text");
                sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {
                        "evandix7@gmail.com"
                });
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "TPT app");

                startActivity(Intent.createChooser(sendIntent, ""));

            }
        });
        postATip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("plain/text");
                sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {
                        "mary.bart@facultyfocus.com"
                });
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "TPT Tip Submission");

                startActivity(Intent.createChooser(sendIntent, ""));

            }
        });
        rateUsOnApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("market://details?id=" + NotifyActivity.this.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + NotifyActivity.this.getPackageName())));
                }

            }
        });
    }


}
