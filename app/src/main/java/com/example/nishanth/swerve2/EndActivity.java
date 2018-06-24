package com.example.nishanth.swerve2;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EndActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        Intent i = getIntent();
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        }
        decorView.setSystemUiVisibility(uiOptions);

        double score = i.getDoubleExtra("score", 0);

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
