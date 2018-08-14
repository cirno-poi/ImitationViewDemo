package com.example.dell.customviewdemo.customview;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;

import com.example.dell.customviewdemo.R;

public class AnimateView extends View {
    Bitmap bitmap;
    Paint paint;
    Camera camera;
    /**
     * 动态变化部分的路径
     */
    Path pathMove;
    /**
     * 静止不变部分的路径
     */
    Path pathStatic;
    private boolean drawPath = false;
    private boolean drawBackground = false;
    private long mainDuration = 1500;

    float angDeg;
    float roatDeg;
    final int RIGHT_ANGLE = 90;
    final int ROUND_ANGLE = 360;
    final int STRAIGHT_ANGLE = 180;


    public AnimateView(Context context) {
        super(context);
    }

    public AnimateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public long getMainDuration() {
        return mainDuration;
    }

    public void setMainDuration(long mainDuration) {
        this.mainDuration = mainDuration;
    }

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

    public boolean isDrawPath() {
        return drawPath;
    }

    public void setDrawPath(boolean drawPath) {
        this.drawPath = drawPath;
    }

    public boolean isDrawBackground() {
        return drawBackground;
    }

    public void setDrawBackground(boolean drawBackground) {
        this.drawBackground = drawBackground;
    }

    public AnimateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    {
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.maps);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        camera = new Camera();
        pathMove = new Path();
        pathStatic = new Path();
    }

    public void setAnimate() {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "angDeg", 0, 270);
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(this, "roatDeg", 50, 20, 50);
        animator.setEvaluator(new DegreesEvaluator());
        animator1.setEvaluator(new DegreesEvaluator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator1.setRepeatCount(ValueAnimator.INFINITE);
        animatorSet.setDuration(mainDuration);
        animatorSet.setInterpolator(new FastOutSlowInInterpolator());
        animatorSet.playTogether(animator, animator1);
        animatorSet.start();
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
        setPathMove((double) angDeg);

        /* 绘制Bitmap动态部分
         * 按路径裁切
         * 先裁切，再旋转
         * */
        canvas.save();
        //按路径裁切
        canvas.clipPath(pathMove);
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
        if (drawBackground) {
            canvas.drawColor(Color.YELLOW);
        }
        canvas.drawBitmap(bitmap, x, y, paint);
        canvas.restore();

        /* 绘制Bitmap静态部分
         * 比起动态部分只少了camera.rotateY(-roatDeg);这一句
         * 按路径裁切
         * 先裁切，再旋转
         * */
        canvas.save();
        //按路径裁切
        canvas.clipPath(pathStatic);
        camera.save();
        canvas.rotate(-angDeg, centerX, centerY);
        canvas.translate(centerX, centerY);
        camera.applyToCanvas(canvas);
        //位置变化是倒序执行的
        canvas.translate(-centerX, -centerY);
        canvas.rotate(angDeg, centerX, centerY);
        camera.restore();
        paint.reset();
        if (drawBackground) {
            canvas.drawColor(ContextCompat.getColor(getContext(), R.color.lightPink));
        }
        canvas.drawBitmap(bitmap, x, y, paint);
        canvas.restore();

        /* 按路径裁切
         * 绘制路径
         * 先裁切，再旋转
         * */
        if (drawPath) {
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5);
            canvas.drawPath(pathMove, paint);
            paint.setColor(ContextCompat.getColor(getContext(), R.color.doderBlue));
            canvas.drawPath(pathStatic, paint);
        }
    }

    /**
     * 计算裁切路径，分不同情况考虑
     * 由于需要用到旋转角的tan函数，故需要特殊处理90度和270度
     * 其余分4个区间进行计算
     *
     * @param angdeg 旋转角度
     */
    private void setPathMove(Double angdeg) {
        pathMove.reset();
        pathStatic.reset();
        angdeg = Math.abs(angdeg) >= ROUND_ANGLE ? angdeg % ROUND_ANGLE : angdeg;
        angdeg = angdeg < 0 ? angdeg + ROUND_ANGLE : angdeg;
        if (angdeg == RIGHT_ANGLE) {
            pathMove.moveTo(0, getHeight() / 2);
            pathMove.lineTo(0, 0);
            pathMove.rLineTo(getWidth(), 0);
            pathMove.rLineTo(0, getHeight() / 2);
            pathMove.close();

            pathStatic.moveTo(0, getHeight() / 2);
            pathStatic.lineTo(0, getHeight());
            pathStatic.rLineTo(getWidth(), 0);
            pathStatic.rLineTo(0, -getHeight() / 2);
            return;
        }
        //270度
        if (angdeg == RIGHT_ANGLE * 3) {
            pathMove.moveTo(getWidth(), getHeight() / 2);
            pathMove.lineTo(getWidth(), getHeight());
            pathMove.rLineTo(-getWidth(), 0);
            pathMove.rLineTo(0, -getHeight() / 2);
            pathMove.close();

            pathStatic.moveTo(getWidth(), getHeight() / 2);
            pathStatic.lineTo(getWidth(), 0);
            pathStatic.rLineTo(-getWidth(), 0);
            pathStatic.rLineTo(0, getHeight() / 2);
            return;
        }
        //临界角
        double criticalRad = Math.atan((double) getWidth() / getHeight());
        double angrad = Math.toRadians(angdeg);
        //上半部分的2个临界角之间
        if (angrad <= criticalRad || angdeg > (ROUND_ANGLE - Math.toDegrees(criticalRad))) {
            float dX = (float) (getWidth() / 2 - (getHeight() / 2) * Math.tan(angrad));
            pathMove.moveTo(dX, 0);
            pathMove.lineTo(getWidth(), 0);
            pathMove.rLineTo(0, getHeight());
            pathMove.rLineTo(-dX, 0);
            pathMove.close();

            pathStatic.moveTo(dX, 0);
            pathStatic.lineTo(0, 0);
            pathStatic.rLineTo(0, getHeight());
            pathStatic.rLineTo(getWidth() - dX, 0);
            return;
        }
        //左半部分的2个临界角之间
        else if (angdeg < (STRAIGHT_ANGLE - Math.toDegrees(criticalRad))) {
            float dY = (float) (getHeight() / 2 - (getWidth() / 2) / Math.tan(angrad));
            pathMove.moveTo(0, dY);
            pathMove.lineTo(0, 0);
            pathMove.rLineTo(getWidth(), 0);
            pathMove.rLineTo(0, getHeight() - dY);
            pathMove.close();

            pathStatic.moveTo(0, dY);
            pathStatic.lineTo(0, getHeight());
            pathStatic.rLineTo(getWidth(), 0);
            pathStatic.rLineTo(0, -dY);
            return;
        }
        //下半部分的2个临界角之间，此时tan值为负
        if (angdeg < (STRAIGHT_ANGLE + Math.toDegrees(criticalRad)) && angdeg >= (STRAIGHT_ANGLE - Math.toDegrees(criticalRad))) {
            float dX = (float) (getWidth() / 2 + (getHeight() / 2) * Math.tan(angrad));
            pathMove.moveTo(dX, getHeight());
            pathMove.lineTo(0, getHeight());
            pathMove.rLineTo(0, -getHeight());
            pathMove.rLineTo(getWidth() - dX, 0);
            pathMove.close();

            pathStatic.moveTo(dX, getHeight());
            pathStatic.lineTo(getWidth(), getHeight());
            pathStatic.rLineTo(0, -getHeight());
            pathStatic.rLineTo(-dX, 0);
            return;
        }
        //右半部分的2个临界角之间
        if (angdeg < (ROUND_ANGLE - Math.toDegrees(criticalRad)) && angdeg >= (STRAIGHT_ANGLE + Math.toDegrees(criticalRad))) {
            float dY = (float) (getHeight() / 2 - (getWidth() / 2) / Math.tan(angrad));
            pathMove.moveTo(getWidth(), getHeight() - dY);
            pathMove.lineTo(getWidth(), getHeight());
            pathMove.rLineTo(-getWidth(), 0);
            pathMove.rLineTo(0, -getHeight() + dY);
            pathMove.close();

            pathStatic.moveTo(getWidth(), getHeight() - dY);
            pathStatic.lineTo(getWidth(), 0);
            pathStatic.rLineTo(-getWidth(), 0);
            pathStatic.rLineTo(0, dY);
        }
    }
}
