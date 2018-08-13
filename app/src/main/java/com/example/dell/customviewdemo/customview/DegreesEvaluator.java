package com.example.dell.customviewdemo.customview;

import android.animation.TypeEvaluator;
import android.util.Log;

public class DegreesEvaluator implements TypeEvaluator<Float> {

    private static final String TAG = "23333";

    @Override
    public Float evaluate(float fraction, Float startValue, Float endValue) {
        Log.d(TAG, "fraction: " + fraction);
        return  (fraction * (endValue - startValue) + startValue);
    }
}
