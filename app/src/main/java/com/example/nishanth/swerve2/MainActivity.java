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
    private float speed=20;
    final int NUM_FIREBALLS = 4;
    private Coin coin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
        kenny = findViewById(R.id.imageView);
        kenny.setX(500);
        kenny.setY(1000);

        layout = findViewById(R.id.layout);
        layout.setMinHeight(-10);
        layout.setMaxHeight(2500);
        layout.setMaxWidth(1500);

        for (int i =0 ;i<NUM_FIREBALLS ;i++) {
            fireballs.add(new Fireball(getApplicationContext()));
            layout.addView(fireballs.get(i));
            fireballs.get(i).setX((float) (Math.random()*layout.getMaxWidth()-50));
            fireballs.get(i).setY((float) (Math.random()*layout.getMaxHeight()));
            fireballs.get(i).setY(fireballs.get(i).getY()-9000);
        }

        coin = new Coin(getApplicationContext());
        layout.addView(coin);
        coin.setX((float) (Math.random()*layout.getMaxWidth()-50));
        coin.setY(-15000);


        health = findViewById(R.id.progressBar);
        health.bringToFront();
        health.setMax(250);
        health.setProgress(250);

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
            die();
        }
        if (touching()) {
            health.setProgress(health.getProgress()-10);
            kenny.setColorFilter(Color.RED);
            return;
        }
        if (coin()) {
            health.setProgress(health.getProgress()+20);
            kenny.setColorFilter(Color.GREEN);
            return;
        }
        kenny.clearColorFilter();
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

    private void die() {
        timer.cancel();
        Intent i = new Intent(this, EndActivity.class);
        i.putExtra("score",score/10.0);
        startActivity(i);
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
            if (Rect.intersects(kenR,r)) {
                init(fireballs.get(rects.indexOf(r)));
                return true;
            }
        }
        return false;
    }

    private void init(ImageView f) {
        f.setX((float) (Math.random()*layout.getMaxWidth()-50));
        f.setY(-100);
        if (f.toString().equals("coin"))
            f.setY(-1000*speed);
    }

    private void drop() {
        if (kenny.getX() < 0 || kenny.getX()+kenny.getWidth() > layout.getMaxWidth() || kenny.getY()>layout.getMaxHeight())
            health.setProgress(health.getProgress()-1);
        for (Fireball f : fireballs) {
            f.setY(f.getY()+speed);
            if (f.getY()>layout.getMaxHeight()) {
                init(f);
                if (f.equals(fireballs.get(0))) {
                    f.setX(kenny.getX());
                }
            }
        }
        coin.setY(coin.getY()+speed+5);
        if (coin.getY()>layout.getMaxHeight()) {
            init(coin);
        }
        score++;
        speed+=1E-3;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        kenny.setY(kenny.getY()+sensorEvent.values[1]*15);
        kenny.setX(kenny.getX()+sensorEvent.values[0]*-20);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}
    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        int action = event.getActionMasked();
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                kenny.setY(event.getRawY() - 200);
//                kenny.setX(event.getRawX() - 200);
//                return true;
//            case MotionEvent.ACTION_UP:
//                return true;
//            case MotionEvent.ACTION_MOVE:
//                return true;
//            default:
//                return super.onTouchEvent(event);
//
        //Toast.makeText(getApplicationContext(), "pos:"+coin.getY(),Toast.LENGTH_SHORT).show();
        return false;
    }
}
