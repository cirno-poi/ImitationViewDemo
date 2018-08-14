package com.example.dell.customviewdemo.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.dell.customviewdemo.R;
import com.example.dell.customviewdemo.customview.AnimateView;

public class MainActivity extends AppCompatActivity {

    private AnimateView animateView;
    private AnimateView animateView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        animateView = findViewById(R.id.animate_view);
        animateView2 = findViewById(R.id.animate_view2);
        animateView.setDrawPath(true);
        animateView.setDrawBackground(true);
        animateView.setMainDuration(1200);
        animateView2.setMainDuration(1200);
        animateView.startAnimate();
        animateView2.startAnimate();
    }

}
