package com.example.nishanth.swerve2;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private ImageView kenny;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        kenny = findViewById(R.id.imageView);
        kenny.setX(500);
        kenny.setY(1000);

        ConstraintLayout layout = findViewById(R.id.layout);
        layout.setMaxHeight(2500);
        layout.setMaxWidth(1500);

        ArrayList<Fireball> fireballs = new ArrayList<>();
        for (int i =0 ;i<5 ;i++) {
            fireballs.add(new Fireball(getApplicationContext()));
            layout.addView(fireballs.get(i));
            fireballs.get(i).setX((float) (Math.random()*layout.getMaxWidth()));
            fireballs.get(i).setY((float) (Math.random()*layout.getMaxHeight()));
        }



    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        kenny.setY(kenny.getY()+sensorEvent.values[1]*3);
        kenny.setX(kenny.getX()+sensorEvent.values[0]*-3);
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
