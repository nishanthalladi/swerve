package com.fishbrain.nishanth.swerve2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
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
    private float speed=3.5f;
    final int NUM_FIREBALLS = 5, FLASH=50;
    private Coin coin;
    private boolean playing = true, counting = false, soundplaying = false;
    private SharedPreferences reader;
    private SharedPreferences.Editor editor;
    private TextView count;
    private MediaPlayer coinsound, hitsound;
    private Queue<MediaPlayer> sounds;
    private float MAX_VOLUME = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coinsound = MediaPlayer.create(getApplicationContext(), R.raw.coinsound);
        hitsound = MediaPlayer.create(getApplicationContext(), R.raw.hitsound);

//        coinsound.setAudioStreamType(AudioManager.FX_KEY_CLICK);
//        hitsound.setAudioStreamType(AudioManager.FX_KEY_CLICK);

        sounds = new LinkedList<>();

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

        layout = findViewById(R.id.layout);
        layout.setMinHeight(-10);
        layout.setMaxHeight(Resources.getSystem().getDisplayMetrics().heightPixels );
        layout.setMaxWidth(Resources.getSystem().getDisplayMetrics().widthPixels);

        reader = getApplicationContext().getSharedPreferences("Preferences", MODE_PRIVATE);
        editor = reader.edit();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);

        kenny = findViewById(R.id.imageView);
        kenny.setImageResource(reader.getInt("character",R.drawable.banana));
        kenny.setX(layout.getMaxWidth()/2);
        kenny.setY(layout.getMaxHeight()/2);




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

        play = findViewById(R.id.imageView4);
        play.setVisibility(View.GONE);
        play.bringToFront();

        count = findViewById(R.id.textView14);
        count.setTypeface(Typeface.createFromAsset(getAssets(), "fipps.otf"));
        count.bringToFront();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

//        if (!reader.getBoolean("playing",false) && reader.getInt("health",health.getMax())!=0 ){
//            for (int i =0; i<fireballs.size(); i++) {
//                fireballs.get(i).setY(Float.parseFloat(reader.getStringSet("fireball"+i,fireballs.get(i).getCoords()).toArray(new String[0])[0]));
//                fireballs.get(i).setX(Float.parseFloat(reader.getStringSet("fireball"+i,fireballs.get(i).getCoords()).toArray(new String[0])[1]));
//            }
//            coin.setY(Float.parseFloat(reader.getStringSet("coin",coin.getCoords()).toArray(new String[0])[0]));
//            coin.setX(Float.parseFloat(reader.getStringSet("coin",coin.getCoords()).toArray(new String[0])[1]));
//
//            health.setProgress(reader.getInt("health",health.getMax()));
//            speed = reader.getFloat("speed",speed);
//        }

        countdown();
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
            if (reader.getFloat("volume",MAX_VOLUME) >0) {
                MediaPlayer hitsound = MediaPlayer.create(getApplicationContext(), R.raw.hitsound);
                float log1 = (float) (Math.log(MAX_VOLUME - reader.getFloat("volume", MAX_VOLUME)) / Math.log(MAX_VOLUME));
                hitsound.setVolume((1 - log1) / 2, (1 - log1) / 2);
                hitsound.start();
                hitsound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                        mp = null;
                    }
                });
            }
            health.setProgress(health.getProgress()-10);
            kenny.setColorFilter(Color.RED);
            flashTime=FLASH;
        }
        if (coin()) {
            if (reader.getFloat("volume",MAX_VOLUME) >0) {
                MediaPlayer coinsound = MediaPlayer.create(getApplicationContext(), R.raw.coinsound);
                float log1=(float)(Math.log(MAX_VOLUME-reader.getFloat("volume",MAX_VOLUME))/Math.log(MAX_VOLUME));
                coinsound.setVolume((1-log1)/2, (1-log1)/2);
                coinsound.start();
                coinsound.setOnCompletionListener(mp -> {
                    mp.release();
                    mp = null;
                });
            }
            health.setProgress(health.getProgress()+50);
            kenny.setColorFilter(Color.GREEN);
            flashTime=FLASH;
        }
        flashTime--;

    }

    private void playsound(MediaPlayer mediaPlayer) {
//        sounds.add(mediaPlayer);
//        while (!sounds.isEmpty()) {
//            sounds.remove().start();
//        }

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
        speed+=1.5E-5;
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

    public void pause() {
        playing = false;
        play.setVisibility(View.VISIBLE);
        //savegamestate();

    }

    public void unpause() {
        play.setVisibility(View.GONE);
        countdown();
        //savegamestate();
    }
    private void savegamestate() {
        editor.putBoolean("playing", playing);
        for (int i=0; i<fireballs.size(); i++) {
            editor.putStringSet("fireball"+i , fireballs.get(i).getCoords());
        }
        editor.putStringSet("coin",coin.getCoords());
        editor.putInt("health",health.getProgress());
        editor.putFloat("speed",speed);
        editor.commit();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (counting)
            return false;
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (playing){
                    pause();
                }
                else {
                    unpause();
                }
                return true;
            case MotionEvent.ACTION_UP:
                return true;
            case MotionEvent.ACTION_MOVE:
                return true;
            default:
                return super.onTouchEvent(event);

            //Toast.makeText(getApplicationContext(), "pos:"+coin.getY(),Toast.LENGTH_SHORT).show();
//        }
            //return true;
        }
        //return true;
    }

    private void countdown() {
        if (counting)
            return;
        count.setVisibility(View.VISIBLE);
        playing = false;
        counting = true;
        new CountDownTimer(3000, 1) {
            @Override
            public void onTick(long millisUntilFinished) {
                count.setText(millisUntilFinished/1000 + 1 +"");
            }

            @Override
            public void onFinish() {
                count.setVisibility(View.GONE);
                playing = true;
                counting = false;
            }
        }.start();
    }


    @Override
    public void onBackPressed() {
        //pause();
        timer.cancel();
        startActivity(new Intent(this, HomePage.class));
    }

    @Override
    public void onPause() {
        if(health.getProgress()>0)
            pause();
        super.onPause();
    }
    @Override
    public void onResume() {
        unpause();
        super.onResume();
    }

}
