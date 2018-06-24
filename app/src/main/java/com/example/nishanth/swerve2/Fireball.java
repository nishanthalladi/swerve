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
//        if (Math.random()<(1.0/3)){
//            setImageResource(R.drawable.box);
//        }
//        else if (Math.random()<(2.0/3)){
//            setImageResource(R.drawable.table);
//        }
//        else{
//            setImageResource(R.drawable.dresser);
//        }
//        setImageResource(R.drawable.box);
        setImageResource(R.drawable.box);
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
    }
}
