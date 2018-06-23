package com.example.nishanth.swerve2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private ImageView kenny, heart;
    private Handler handler = new Handler();
    private Timer timer = new Timer();
    private ArrayList<Fireball> fireballs = new ArrayList<>();
    private ConstraintLayout layout;
    private ProgressBar health;
    private int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        kenny = findViewById(R.id.imageView);
        kenny.setX(500);
        kenny.setY(1000);

        layout = findViewById(R.id.layout);
        layout.setMinHeight(-10);
        layout.setMaxHeight(2500);
        layout.setMaxWidth(1500);

        for (int i =0 ;i<5 ;i++) {
            fireballs.add(new Fireball(getApplicationContext()));
            layout.addView(fireballs.get(i));
            fireballs.get(i).setX((float) (Math.random()*layout.getMaxWidth()-50));
            fireballs.get(i).setY((float) (Math.random()*layout.getMaxHeight()-200));
        }

        health = findViewById(R.id.progressBar);
        health.bringToFront();
        health.setMax(500);
        health.setProgress(500);

        heart = findViewById(R.id.imageView2);
        heart.bringToFront();


        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        check();
                        drop();
                    }
                });
            }
        },0,10);
    }

    private void check() {
        if (health.getProgress()<=0){
            Intent i = new Intent(this, EndActivity.class);
            i.putExtra("score",score/10000.0);
            startActivity(i);
        }
        if (touching()) {
            health.setProgress(health.getProgress()-1);
            kenny.setColorFilter(Color.RED);
            return;
        }
        kenny.clearColorFilter();
    }

    public boolean touching() {
        ArrayList<Rect> rects = new ArrayList<>();
        for (Fireball f: fireballs) {
            Rect r = new Rect();
            f.getHitRect(r);
            rects.add(r);
        }
        Rect kenR = new Rect();
        kenny.getHitRect(kenR);
        kenR.set(kenR.left+2,kenR.top+2,kenR.right-2,kenR.bottom-2);
        for (Rect r:rects) {
            if (Rect.intersects(kenR,r))
                return true;
        }
        return false;
    }

    private void init(Fireball f) {
        f.setX((float) (Math.random()*layout.getMaxWidth()));
        f.setY(-100);
    }

    private void drop() {
        if (kenny.getX() < 0 || kenny.getX()+kenny.getWidth() > layout.getMaxWidth() || kenny.getY()>layout.getMaxHeight())
            health.setProgress(health.getProgress()-1);
        for (Fireball f : fireballs) {
            f.setY(f.getY()+20);
            if (f.getY()>layout.getMaxHeight()) {
                init(f);
                if (f.equals(fireballs.get(0))) {
                    f.setX(kenny.getX());
                }
            }
        }
        score++;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        kenny.setY(kenny.getY()+sensorEvent.values[1]*3);
        kenny.setX(kenny.getX()+sensorEvent.values[0]*-20);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}
    @Override
    public boolean onTouchEvent(MotionEvent event){
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                kenny.setY(event.getRawY()-200);
                kenny.setX(event.getRawX()-200);
                return true;
            case MotionEvent.ACTION_UP:
                return true;
            case MotionEvent.ACTION_MOVE:
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }
}
