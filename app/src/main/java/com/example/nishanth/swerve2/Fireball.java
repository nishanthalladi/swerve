package com.example.nishanth.swerve2;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by nishanth on 6/22/18.
 */

public class Fireball extends android.support.v7.widget.AppCompatImageView {
    public Fireball(Context context) {
        super(context);
        setImageResource(R.drawable.fireball);
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
    }
}
