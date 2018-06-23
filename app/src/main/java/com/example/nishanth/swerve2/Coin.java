package com.example.nishanth.swerve2;

import android.content.Context;

/**
 * Created by nishanth on 6/23/18.
 */

public class Coin extends android.support.v7.widget.AppCompatImageView {
    public Coin(Context context) {
        super(context);
        if (Math.random()<(0.5)){
            setImageResource(R.drawable.coin);
        }
        else{
            setImageResource(R.drawable.gem);
        }
        setImageResource(R.drawable.coin);
    }

    @Override
    public String toString(){
        return "coin";
    }
    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
    }
}
