package com.example.nishanth.swerve2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Settings extends AppCompatActivity implements SensorEventListener {

    private SharedPreferences.Editor editor;
    private SeekBar bar, volume;
    private ImageView kenny,coin;
    private ConstraintLayout layout;
    private SensorManager mSensorManager;
    private LinkedList<Integer> characters;
    private HashMap<String,Integer> map;
    private int index=0, width, height;
    final int MAX_VOLUME = 10;
    private SharedPreferences preferences;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);

        kenny = findViewById(R.id.imageView3);

        kenny.setY(1000);
        kenny.setX(500);

        characters = new LinkedList<>();
        characters.add(R.drawable.banana);
        characters.add(R.drawable.glasses);
        //characters.add(R.drawable.box);
        //characters.add(R.drawable.coin);
        //characters.add(R.drawable.dresser);
        characters.add(R.drawable.ghost);
        characters.add(R.drawable.gem);
        //characters.add(R.drawable.heart_pixel);
        //characters.add(R.drawable.wizard);

        map = new HashMap<>();
        map.put("banana",R.drawable.banana);
        map.put("glasses",R.drawable.glasses);
        //map.put("box",R.drawable.box);
        //map.put("coin",R.drawable.coin);
        //map.put("dresser",R.drawable.dresser);
        map.put("ghost",R.drawable.ghost);
        map.put("gem",R.drawable.gem);
        // map.put("heart_pixel",R.drawable.heart_pixel);
        //map.put("wizard",R.drawable.wizard);

        width = Resources.getSystem().getDisplayMetrics().widthPixels;
        height = Resources.getSystem().getDisplayMetrics().heightPixels;

        TextView sensitivity = findViewById(R.id.textView7), home = findViewById(R.id.textView8),
                characterselect= findViewById(R.id.textView10), left = findViewById(R.id.textView12), right = findViewById(R.id.textView13),
                feedback = findViewById(R.id.textView16), v = findViewById(R.id.textView19);
        Typeface fipps = Typeface.createFromAsset(getAssets(),"fipps.otf");
        sensitivity.setTypeface(fipps);home.setTypeface(fipps);characterselect.setTypeface(fipps);
        left.setTypeface(fipps);right.setTypeface(fipps);feedback.setTypeface(fipps);v.setTypeface(fipps);
        sensitivity.setWidth(width);
        characterselect.setWidth(width);
        v.setWidth(width);


        preferences = getApplicationContext().getSharedPreferences("Preferences", MODE_PRIVATE);

        kenny.setImageResource(preferences.getInt("character",R.drawable.banana));

        coin = findViewById(R.id.imageView5);
        coin.setX((float) (Math.random()*width));
        coin.setY((float) (Math.random()*height));

        bar = findViewById(R.id.seekBar);
        bar.setMax(9);
        bar.setProgress(preferences.getInt("sensitivity", 5));

        volume = findViewById(R.id.seekBar2);
        volume.setMax(20);

        editor = preferences.edit();

    }

    private boolean coin() {
        Rect kenR = new Rect();
        kenny.getHitRect(kenR);
        kenR.set(kenR.left+2,kenR.top+2,kenR.right-2,kenR.bottom-2);
        Rect coinR = new Rect();
        coin.getHitRect(coinR);
        if (Rect.intersects(kenR,coinR)) {
            init(coin);
            return true;
        }
        return false;
    }

    private void init(ImageView f) {
        coin.setX((float) (Math.random()*width));
        coin.setY((float) (Math.random()*height));
    }

    @Override
    public void onBackPressed() {
        home(findViewById(R.id.textView8));
    }

    public void home(View view) {
        editor.putInt("sensitivity", (bar.getProgress()));
        editor.putFloat("volume", volume.getProgress());
        editor.commit();
        startActivity(new Intent(this,HomePage.class));
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (coin()) {
            if (volume.getProgress() >0) {
                MediaPlayer coinsound = MediaPlayer.create(getApplicationContext(), R.raw.coinsound);
                float log1=(float)(Math.log(MAX_VOLUME-volume.getProgress())/Math.log(MAX_VOLUME));
                coinsound.setVolume((1-log1)/2, (1-log1)/2);
                coinsound.start();
                coinsound.setOnCompletionListener(mp -> {
                    mp.release();
                    mp = null;
                });
            }
            init(coin);
        }
        if (kenny.getX() < 0)
            kenny.setX(0);
        if (kenny.getX()+kenny.getWidth() > width)
            kenny.setX(width-kenny.getWidth());
        if (kenny.getY()+kenny.getHeight()>height)
            kenny.setY(height-kenny.getHeight());
        if (kenny.getY()<0)
            kenny.setY(0);
        if (preferences.getInt("sensitivity", -1)==-1){
            kenny.setY(kenny.getY()+sensorEvent.values[1]*4*bar.getProgress());
            kenny.setX(kenny.getX()+sensorEvent.values[0]*-4*bar.getProgress());
        }
        else {
            kenny.setY(kenny.getY()+sensorEvent.values[1]*2*bar.getProgress());
            kenny.setX(kenny.getX()+sensorEvent.values[0]*-2*bar.getProgress());
        }

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

    public void feedback(View view) throws IOException {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://goo.gl/forms/w7z5pQCoKAfkeldy2"));
        startActivity(intent);
    }

    @Override
    public void onPause() {
        mSensorManager.unregisterListener(this);
        super.onPause();
    }
    @Override
    public void onResume() {
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
        super.onResume();
    }

    public int map(int x, int in_min, int in_max, int out_min, int out_max)
    {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }
}
