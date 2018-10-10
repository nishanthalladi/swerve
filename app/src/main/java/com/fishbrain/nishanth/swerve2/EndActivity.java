package com.fishbrain.nishanth.swerve2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class EndActivity extends AppCompatActivity {

    SharedPreferences reader;
    private AdView mAdView, mAdView2;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

        MobileAds.initialize(this, "ca-app-pub-3775150933679718~3942611068"); //actual
        // MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713"); //test

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3775150933679718/4043092537"); // actual
//        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/8691691433"); // test
//        mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("359105065031951").build()); // test
         mInterstitialAd.loadAd(new AdRequest.Builder().build()); // actual

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                mInterstitialAd.show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.

            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the interstitial ad is closed.
            }
        });

        mInterstitialAd.show();

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView2 = findViewById(R.id.adView2);
        AdRequest adRequest2 = new AdRequest.Builder().build();
        mAdView2.loadAd(adRequest2);

        Intent i = getIntent();

        reader = getApplicationContext().getSharedPreferences("Preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = reader.edit();
        double score = i.getDoubleExtra("score", 0);
        if (score>reader.getFloat("highscore", 0)){
            editor.putFloat("highscore", (float) score);
            editor.apply();
        }


        Typeface fipps = Typeface.createFromAsset(getAssets(), "fipps.otf");
        TextView t = findViewById(R.id.textView);
        t.setTypeface(fipps);

        TextView sc = findViewById(R.id.textView2);
        sc.setTypeface(fipps);
        sc.setText("Your Score: " + score);

        TextView b = findViewById(R.id.textView3);
        b.setTypeface(fipps);
        b.setText("Play Again");

        TextView h= findViewById(R.id.textView6);
        h.setTypeface(fipps);

        TextView hs = findViewById(R.id.textView11);
        hs.setTypeface(fipps);
        hs.setText("High Score: " + reader.getFloat("highscore", (float)score));

    }

    @Override
    public void onBackPressed(){}

    public void goBack(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void home(View view) {
        startActivity(new Intent(this, HomePage.class));
    }
}
