package com.example.nishanth.swerve2;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

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

        Typeface fipps = Typeface.createFromAsset(getAssets(), "fipps.otf");
        TextView b = findViewById(R.id.textView4), p = findViewById(R.id.textView5), s = findViewById(R.id.textView9);
        b.setTypeface(fipps);
        p.setTypeface(fipps);
        s.setTypeface(fipps);

    }

    public void play(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void settings(View view) {
        startActivity(new Intent(this, Settings.class));
    }
}
