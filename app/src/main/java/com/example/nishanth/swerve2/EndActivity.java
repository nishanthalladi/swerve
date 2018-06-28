package com.example.nishanth.swerve2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EndActivity extends AppCompatActivity {

    SharedPreferences reader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        Intent i = getIntent();

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
