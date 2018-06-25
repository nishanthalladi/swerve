package com.example.nishanth.swerve2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager mSensorManager;
    private ImageView kenny, heart, play;
    private Handler handler = new Handler();
    private Timer timer = new Timer();
    private ArrayList<Fireball> fireballs = new ArrayList<>();
    private ConstraintLayout layout;
    private ProgressBar health;
    private int score, flashTime;
    private float speed=2;
    final int NUM_FIREBALLS = 5, FLASH=50;
    private Coin coin;
    private boolean playing = true;
    private SharedPreferences reader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        }
        decorView.setSystemUiVisibility(uiOptions);
        reader = getApplicationContext().getSharedPreferences("Preferences", MODE_PRIVATE);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
        kenny = findViewById(R.id.imageView);
        kenny.setImageResource(reader.getInt("character",R.drawable.banana));
        kenny.setX(500);
        kenny.setY(1000);

        layout = findViewById(R.id.layout);
        layout.setMinHeight(-10);
        layout.setMaxHeight(2555);
        layout.setMaxWidth(1450);

        for (int i =0 ;i<NUM_FIREBALLS ;i++) {
            fireballs.add(new Fireball(getApplicationContext()));
            layout.addView(fireballs.get(i));
            fireballs.get(i).setX((float) (Math.random()*layout.getMaxWidth()-50));
            fireballs.get(i).setY((float) (Math.random()*layout.getMaxHeight()));
            fireballs.get(i).setY(fireballs.get(i).getY()-3000);
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

        play = findViewById(R.id.imageView5);
        play.setVisibility(View.GONE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    check();
                    drop();
                });
            }
        },0, 1);
    }

    private void check() {
        if (!playing)
            return;
        if (health.getProgress()<=0){
            die();
        }
        if (flashTime<=0)
            kenny.clearColorFilter();
        if (touching()) {
            health.setProgress(health.getProgress()-10);
            kenny.setColorFilter(Color.RED);
            flashTime=FLASH;
        }
        if (coin()) {
            health.setProgress(health.getProgress()+50);
            kenny.setColorFilter(Color.GREEN);
            flashTime=FLASH;
        }
        flashTime--;

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
        i.putExtra("score",score/100.0);
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
        f.setY((float) (-1000 - Math.random()*500));
        if (f.toString().equals("coin"))
            f.setY(-16000*speed);
    }

    private void drop() {
        if (!playing)
            return;

        for (Fireball f : fireballs) {
            f.setY(f.getY()+speed);
            if (f.getY()>layout.getMaxHeight()) {
                init(f);
                if (f.equals(fireballs.get(0))) {
                    f.setX(kenny.getX());
                }
            }
        }
        coin.setY(coin.getY()+speed*1.3f);
        if (coin.getY()>layout.getMaxHeight()) {
            init(coin);
        }
        score++;
        speed+=8E-6;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (!playing)
            return;
        if (kenny.getX() < 0)
            kenny.setX(0);
        if (kenny.getX()+kenny.getWidth() > layout.getMaxWidth())
            kenny.setX(layout.getMaxWidth()-kenny.getWidth());
        if (kenny.getY()+kenny.getHeight()>layout.getMaxHeight())
            kenny.setY(layout.getMaxHeight()-kenny.getHeight());
        if (kenny.getY()<0)
            kenny.setY(0);
        kenny.setY(kenny.getY()+sensorEvent.values[1]*2*(reader.getInt("sensitivity", 10)));
        kenny.setX(kenny.getX()+sensorEvent.values[0]*-2*(reader.getInt("sensitivity", 10)));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}
    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        int action = event.getActionMasked();
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                if (playing){
//                    play.setVisibility(View.VISIBLE);
//                }
//                else {
//                    play.setVisibility(View.GONE);
//                }
//                playing = !playing;
//                return true;
//            case MotionEvent.ACTION_UP:
//                return true;
//            case MotionEvent.ACTION_MOVE:
//                return true;
//            default:
//                return super.onTouchEvent(event);
//
//            //Toast.makeText(getApplicationContext(), "pos:"+coin.getY(),Toast.LENGTH_SHORT).show();
////        }
//            //return true;
//        }
        return true;
    }


    @Override
    public void onBackPressed() {
        timer.cancel();
        startActivity(new Intent(this, HomePage.class));
    }
}
