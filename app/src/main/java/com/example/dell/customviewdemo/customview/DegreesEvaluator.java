package com.example.dell.customviewdemo.customview;

import android.animation.TypeEvaluator;

public class DegreesEvaluator implements TypeEvaluator<Float> {


    @Override
    public Float evaluate(float fraction, Float startValue, Float endValue) {
        return  (fraction * (endValue - startValue) + startValue);
    }
}
