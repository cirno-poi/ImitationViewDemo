package com.example.dell.customviewdemo.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.example.dell.customviewdemo.R;

public class MyFirstView extends View {

    private DrawCanvas drawCanvas;

    public MyFirstView(Context context) {
        super(context);
    }

    public MyFirstView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyFirstView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyFirstView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    private Paint paint = new Paint();
    Paint paint1 = new Paint();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (drawCanvas != null) {
            drawCanvas.customDraw(canvas);
        } else {
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(20);
            paint.setAntiAlias(true);
            canvas.drawCircle(550, 550, 500, paint);
//        canvas.drawRect(150, 150, 500, 500, paint);

//            paint1.setStrokeWidth(20);
//            paint1.setStrokeCap(Paint.Cap.ROUND);
//            canvas.drawPoint(50, 50, paint1);

        }

    }

    public void setDrawCanvas(DrawCanvas drawCanvas) {
        this.drawCanvas = drawCanvas;
    }

    public interface DrawCanvas {
        /**
         * 自定义绘制view
         *
         * @param canvas 画布
         */
        void customDraw(Canvas canvas);
    }
}
