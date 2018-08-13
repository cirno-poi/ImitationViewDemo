package com.example.dell.customviewdemo.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.dell.customviewdemo.R;
import com.example.dell.customviewdemo.customview.AnimateView;
import com.example.dell.customviewdemo.customview.DegreesEvaluator;
import com.example.dell.customviewdemo.customview.MyFirstView;

public class MainActivity extends AppCompatActivity {

    private MyFirstView myFirstView;
    private AnimateView animateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myFirstView = findViewById(R.id.my_firstview);
        animateView = findViewById(R.id.animate_view);

        final Path path = new Path(); // 初始化 Path 对象

        final Paint paint = new Paint();
//        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(20);
        paint.setColor(Color.parseColor("#66CCFF"));
        paint.setAntiAlias(true);


        myFirstView.setDrawCanvas(new MyFirstView.DrawCanvas() {
            @Override
            public void customDraw(Canvas canvas) {
//                canvas.drawPoint(50, 50, paint);
//                canvas.drawOval(100, 100, 1000, 618, paint);
//                canvas.drawLine(100, 100, 950, 1345, paint);
//                float[] points = {20, 20, 120, 20, 70, 20, 70, 120, 20, 120, 120, 120, 150, 20, 250, 20, 150, 20, 150, 120, 250, 20, 250, 120, 150, 120, 250, 120};
//                canvas.drawLines(points, paint);
//                canvas.drawRoundRect(100, 100, 950, 1600, 50, 50, paint);
//                canvas.drawColor(Color.parseColor("#88880000")); // 半透明红色

//                paint.setStyle(Paint.Style.FILL);
//                canvas.drawArc(200, 100, 800, 500, -110, 100, true, paint); // 绘制扇形
//                canvas.drawArc(200, 100, 800, 500, 20, 140, false, paint); // 绘制弧形
//                paint.setStyle(Paint.Style.STROKE); // 画线模式
//                canvas.drawArc(200, 100, 800, 500, 180, 60, true, paint); // 绘制不封口的弧形
                path.lineTo(300, 200);
//                path.rLineTo(300, 0);
                path.arcTo(300, 200, 600, 500, -90, 90, false);
                canvas.drawPath(path, paint);

            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator animator = ObjectAnimator.ofFloat(animateView, "angDeg", 0, 360);
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(animateView, "roatDeg", 0, 50, 0);
        animator.setEvaluator(new DegreesEvaluator());
        animator1.setEvaluator(new DegreesEvaluator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator1.setRepeatCount(ValueAnimator.INFINITE);
        animatorSet.setDuration(4000);
        animatorSet.playTogether(animator, animator1);
        animatorSet.start();
    }


}
