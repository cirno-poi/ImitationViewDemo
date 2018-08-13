package com.example.dell.customviewdemo.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.dell.customviewdemo.R;

public class AnimateView extends View {
    Bitmap bitmap;
    Paint paint;
    Camera camera;
    Path path;
    float angDeg;
    float roatDeg;
    final int RIGHT_ANGLE = 90;
    final int ROUND_ANGLE = 360;
    final int STRAIGHT_ANGLE = 180;

    public float getRoatDeg() {
        return roatDeg;
    }

    public void setRoatDeg(float roatDeg) {
        this.roatDeg = roatDeg;
        invalidate();
    }

    public float getAngDeg() {
        return angDeg;
    }

    public void setAngDeg(float angDeg) {
        this.angDeg = angDeg;
        invalidate();
    }

    public AnimateView(Context context) {
        super(context);
    }

    public AnimateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AnimateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    {
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.maps);
        Log.d("233333", "bitmap.getWidth()-------" + bitmap.getWidth());
        Log.d("233333", "bitmap.getHeight()--------" + bitmap.getHeight());
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        camera = new Camera();
        path = new Path();
    }

    /**
     * 按路径裁切，canvas不动，让过canvas中心与Y轴平行的线沿中心进行旋转
     * 裁切被此直线分为2部分的路径
     * 再将裁切后的canvas平移加旋转，使得上述直线与y轴重合，且中点在坐标原点上
     * 这样镜头做rotateY变换时就是以此直线为旋转轴了
     * 最后恢复canvas即可
     *
     * @param canvas 画布
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //临界角
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int x = centerX - bitmapWidth / 2;
        int y = centerY - bitmapHeight / 2;
        setPath((double) angDeg);

        /* 绘制Bitmap
         * 按路径裁切
         * 先裁切，再旋转
         * */
        canvas.save();
        //按路径裁切
        canvas.clipPath(path);
        camera.save();
        camera.rotateY(-roatDeg);
        canvas.rotate(-angDeg, centerX, centerY);
        canvas.translate(centerX, centerY);
        camera.applyToCanvas(canvas);
        //位置变化是倒序执行的
        canvas.translate(-centerX, -centerY);
        canvas.rotate(angDeg, centerX, centerY);
        camera.restore();
        paint.reset();
        canvas.drawColor(Color.YELLOW);
        canvas.drawBitmap(bitmap, x, y, paint);
        canvas.restore();


        /* 按路径裁切
         * 绘制路径
         * 先裁切，再旋转
         * */
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        canvas.drawPath(path, paint);
    }

    /**
     * 计算裁切路径，分不同情况考虑
     * 由于需要用到旋转角的tan函数，故需要特殊处理90度和270度
     * 其余分4个区间进行计算
     *
     * @param angdeg 旋转角度
     */
    private void setPath(Double angdeg) {
        path.reset();
        angdeg = angdeg < 0 ? angdeg + ROUND_ANGLE : angdeg;
        angdeg = angdeg >= ROUND_ANGLE ? angdeg - ROUND_ANGLE : angdeg;
        if (angdeg == RIGHT_ANGLE) {
            path.moveTo(0, getHeight() / 2);
            path.lineTo(0, 0);
            path.rLineTo(getWidth(), 0);
            path.rLineTo(0, getHeight() / 2);
            path.close();
            return;
        }
        //270度
        if (angdeg == RIGHT_ANGLE * 3) {
            path.moveTo(getWidth(), getHeight() / 2);
            path.lineTo(getWidth(), getHeight());
            path.rLineTo(-getWidth(), 0);
            path.rLineTo(0, -getHeight() / 2);
            path.close();
            return;
        }
        //临界角
        double criticalRad = Math.atan((double) getWidth() / getHeight());
        double angrad = Math.toRadians(angdeg);
        //上半部分的2个临界角之间
        if (angrad <= criticalRad || angdeg > (ROUND_ANGLE - Math.toDegrees(criticalRad))) {
            float dX = (float) (getWidth() / 2 - (getHeight() / 2) * Math.tan(angrad));
            path.moveTo(dX, 0);
            path.lineTo(getWidth(), 0);
            path.rLineTo(0, getHeight());
            path.rLineTo(-dX, 0);
            path.close();
            return;
        }
        //左半部分的2个临界角之间
        else if (angdeg < (STRAIGHT_ANGLE - Math.toDegrees(criticalRad))) {
            float dY = (float) (getHeight() / 2 - (getWidth() / 2) / Math.tan(angrad));
            path.moveTo(0, dY);
            path.lineTo(0, 0);
            path.rLineTo(getWidth(), 0);
            path.rLineTo(0, getHeight() - dY);
            path.close();
            return;
        }
        //下半部分的2个临界角之间
        if (angdeg < (STRAIGHT_ANGLE + Math.toDegrees(criticalRad)) && angdeg >= (STRAIGHT_ANGLE - Math.toDegrees(criticalRad))) {
            float dX = (float) (getWidth() / 2 + (getHeight() / 2) * Math.tan(angrad));
            path.moveTo(dX, getHeight());
            path.lineTo(0, getHeight());
            path.rLineTo(0, -getHeight());
            path.rLineTo(getWidth() - dX, 0);
            path.close();
            return;
        }
        //右半部分的2个临界角之间
        if (angdeg < (ROUND_ANGLE - Math.toDegrees(criticalRad)) && angdeg >= (STRAIGHT_ANGLE + Math.toDegrees(criticalRad))) {
            float dY = (float) (getHeight() / 2 - (getWidth() / 2) / Math.tan(angrad));
            path.moveTo(getWidth(), getHeight() - dY);
            path.lineTo(getWidth(), getHeight());
            path.rLineTo(-getWidth(), 0);
            path.rLineTo(0, -getHeight() + dY);
            path.close();
        }
    }
}
