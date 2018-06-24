package com.example.nishanth.swerve2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class Settings extends AppCompatActivity implements SensorEventListener {

    SharedPreferences.Editor editor;
    SeekBar bar;
    ImageView kenny;
    ConstraintLayout layout;
    SensorManager mSensorManager;
    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        }
        decorView.setSystemUiVisibility(uiOptions);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);

        kenny = findViewById(R.id.imageView3);
        kenny.setY(1000);
        kenny.setX(500);


        TextView a = findViewById(R.id.textView7), b = findViewById(R.id.textView8);
        Typeface fipps = Typeface.createFromAsset(getAssets(),"fipps.otf");
        a.setTypeface(fipps);
        b.setTypeface(fipps);

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("Preferences", MODE_PRIVATE);

        bar = findViewById(R.id.seekBar);
        bar.setMax(9);
        bar.setProgress(preferences.getInt("sensitivity", 4));

        editor = preferences.edit();

    }

    @Override
    public void onBackPressed() {
        home(findViewById(R.id.textView8));
    }

    public void home(View view) {
        editor.putInt("sensitivity", (bar.getProgress()));
        editor.commit();
        startActivity(new Intent(this,HomePage.class));
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (kenny.getX() < 0)
            kenny.setX(0);
        if (kenny.getX()+kenny.getWidth() > 1450)
            kenny.setX(1450-kenny.getWidth());
        if (kenny.getY()+kenny.getHeight()>2500)
            kenny.setY(2500-kenny.getHeight());
        if (kenny.getY()<0)
            kenny.setY(0);

        kenny.setY(kenny.getY()+sensorEvent.values[1]*2*(bar.getProgress()));
        kenny.setX(kenny.getX()+sensorEvent.values[0]*-2*(bar.getProgress()));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
