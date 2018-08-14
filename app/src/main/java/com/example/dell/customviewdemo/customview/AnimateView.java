package com.example.dell.customviewdemo.customview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
import android.util.DisplayMetrics;
import android.view.View;

import com.example.dell.customviewdemo.R;

/**
 * 模仿Flipboard动画效果的view
 *
 * @author cirno-poi
 */
public class AnimateView extends View {
    private Bitmap bitmap;
    private Paint paint;
    private Camera camera;
    /**
     * 动态变化部分的路径
     */
    private Path pathMove;
    /**
     * 静止不变部分的路径
     */
    private Path pathStatic;

    private boolean drawPath = false;
    private boolean drawBackground = false;
    private long mainDuration = 1500;

    /**
     * 平面旋转角度
     */
    private float degreeZ = 0;
    /**
     * 平面旋转结束的角度
     */
    private float endDeg = 270;
    /**
     * 图像动态部分沿Y轴旋转的角度
     */
    private float degreeYPart1 = 0;
    /**
     * 图像静态部分沿Y轴旋转的角度
     */
    private float degreeYPart2 = 0;
    /**
     * 图像抬起的最高角度
     */
    private float highDegree = 45;
    /**
     * 图像抬起的最低角度
     */
    private float lowDegree = 30;

    final int RIGHT_ANGLE = 90;
    final int ROUND_ANGLE = 360;
    final int STRAIGHT_ANGLE = 180;

    {
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.maps);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        camera = new Camera();
        pathMove = new Path();
        pathStatic = new Path();

        //设置“糊脸修正”
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float newZ = -displayMetrics.density * 6;
        camera.setLocation(0, 0, newZ);
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

    public float getDegreeYPart2() {
        return degreeYPart2;
    }

    public void setDegreeYPart2(float degreeYPart2) {
        this.degreeYPart2 = degreeYPart2;
        invalidate();
    }

    public long getMainDuration() {
        return mainDuration;
    }

    public void setMainDuration(long mainDuration) {
        this.mainDuration = mainDuration;
    }

    public float getDegreeYPart1() {
        return degreeYPart1;
    }

    public float getHighDegree() {
        return highDegree;
    }

    public void setHighDegree(float highDegree) {
        this.highDegree = highDegree;
    }

    public float getLowDegree() {
        return lowDegree;
    }

    public void setLowDegree(float lowDegree) {
        this.lowDegree = lowDegree;
    }

    public float getEndDeg() {
        return endDeg;
    }

    public void setEndDeg(float endDeg) {
        this.endDeg = endDeg;
    }

    public void setDegreeYPart1(float degreeYPart1) {
        this.degreeYPart1 = degreeYPart1;
        invalidate();
    }

    public float getDegreeZ() {
        return degreeZ;
    }

    public void setDegreeZ(float degreeZ) {
        this.degreeZ = degreeZ;
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

    public void startAnimate() {
        ObjectAnimator animatorFirst = ObjectAnimator.ofFloat(this, "degreeYPart1", 0, highDegree);
        ObjectAnimator animatorDegreeZ = ObjectAnimator.ofFloat(this, "degreeZ", 0, endDeg);
        //不需要在旋转的过程中改变抬起的角度
        //ObjectAnimator animatorRoatDeg = ObjectAnimator.ofFloat(this, "degreeYPart1", highDegree, lowDegree, highDegree);
        ObjectAnimator animatorLast = ObjectAnimator.ofFloat(this, "degreeYPart2", 0, lowDegree);

        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(mainDuration);
        animatorSet.setInterpolator(new FastOutSlowInInterpolator());
        animatorSet.playSequentially(animatorFirst, animatorDegreeZ, animatorLast);
        animatorSet.start();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setDegreeZ(0);
                setDegreeYPart1(0);
                setDegreeYPart2(0);
                animatorSet.start();
            }
        });
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
        setPathMove((double) degreeZ);

        /* 绘制Bitmap动态部分
         * 按路径裁切
         * 先裁切，再旋转
         * */
        canvas.save();
        //按路径裁切
        canvas.clipPath(pathMove);
        camera.save();
        camera.rotateY(-degreeYPart1);
        canvas.rotate(-degreeZ, centerX, centerY);
        canvas.translate(centerX, centerY);
        camera.applyToCanvas(canvas);
        //位置变化是倒序执行的
        canvas.translate(-centerX, -centerY);
        canvas.rotate(degreeZ, centerX, centerY);
        camera.restore();
        paint.reset();
        if (drawBackground) {
            canvas.drawColor(Color.YELLOW);
        }
        canvas.drawBitmap(bitmap, x, y, paint);
        canvas.restore();

        /* 绘制Bitmap静态部分
         * 按路径裁切
         * 先裁切，再旋转
         * */
        canvas.save();
        //按路径裁切
        canvas.clipPath(pathStatic);
        camera.save();
        //由于与动态部分是中心对称的，故此处需要旋转相反的角度
        camera.rotateY(degreeYPart2);
        canvas.rotate(-degreeZ, centerX, centerY);
        canvas.translate(centerX, centerY);
        camera.applyToCanvas(canvas);
        //位置变化是倒序执行的
        canvas.translate(-centerX, -centerY);
        canvas.rotate(degreeZ, centerX, centerY);
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
     * 需要注意tan函数的值在90-180度以及270-360度的区间上是负的
     * 其余分4个区间进行计算
     *
     * @param angdeg 旋转角度
     */
    private void setPathMove(Double angdeg) {
        pathMove.reset();
        pathStatic.reset();
        //处理大于360的角和负角
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
        //下半部分的2个临界角之间
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
