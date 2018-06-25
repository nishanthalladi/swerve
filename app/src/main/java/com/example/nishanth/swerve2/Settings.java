package com.example.nishanth.swerve2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
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

import java.util.HashMap;
import java.util.LinkedList;

public class Settings extends AppCompatActivity implements SensorEventListener {

    SharedPreferences.Editor editor;
    SeekBar bar;
    ImageView kenny;
    ConstraintLayout layout;
    SensorManager mSensorManager;
    LinkedList<Integer> characters;
    HashMap<String,Integer> map;
    int index=0;
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

        characters = new LinkedList<>();
        characters.add(R.drawable.banana);
        characters.add(R.drawable.glasses);
        characters.add(R.drawable.box);
        characters.add(R.drawable.coin);
        characters.add(R.drawable.dresser);
        characters.add(R.drawable.ghost);
        characters.add(R.drawable.gem);
        characters.add(R.drawable.heart_pixel);
        characters.add(R.drawable.wizard);

        map = new HashMap<>();
        map.put("banana",R.drawable.banana);
        map.put("glasses",R.drawable.glasses);
        map.put("box",R.drawable.box);
        map.put("coin",R.drawable.coin);
        map.put("dresser",R.drawable.dresser);
        map.put("ghost",R.drawable.ghost);
        map.put("gem",R.drawable.gem);
        map.put("heart_pixel",R.drawable.heart_pixel);
        map.put("wizard",R.drawable.wizard);


        TextView sensitivity = findViewById(R.id.textView7), home = findViewById(R.id.textView8),
                characterselect= findViewById(R.id.textView10), left = findViewById(R.id.textView12), right = findViewById(R.id.textView13);
        Typeface fipps = Typeface.createFromAsset(getAssets(),"fipps.otf");
        sensitivity.setTypeface(fipps);home.setTypeface(fipps);characterselect.setTypeface(fipps);left.setTypeface(fipps);right.setTypeface(fipps);
        sensitivity.setWidth(Resources.getSystem().getDisplayMetrics().widthPixels );
        characterselect.setWidth(Resources.getSystem().getDisplayMetrics().widthPixels );




        SharedPreferences preferences = getApplicationContext().getSharedPreferences("Preferences", MODE_PRIVATE);

        kenny.setImageResource(preferences.getInt("character",R.drawable.banana));

        bar = findViewById(R.id.seekBar);
        bar.setMax(9);
        bar.setProgress(preferences.getInt("sensitivity", 0));

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

    public void right(View view) {
        index = (index+1)%characters.size();
        kenny.setImageResource(characters.get(index));
        kenny.invalidate();
        for (Integer s : map.values()) {
            if (s.equals(characters.get(index))) {
                editor.putInt("character", s);
                break;
            }
        }
    }

    public void left(View view) {
        index--;
        if (index<0)
            index=characters.size()-1;
        kenny.setImageResource(characters.get(index));
        kenny.invalidate();
        for (Integer s : map.values()) {
            if (s.equals(characters.get(index))) {
                editor.putInt("character", s);
                break;
            }
        }
    }
}
