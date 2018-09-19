package com.example.dell.customviewdemo.customview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.example.dell.customviewdemo.R;
import com.example.dell.customviewdemo.utils.NumberUtils;

public class JikeLikeView extends View {

    private static final String TAG = "JikeLikeView";

    private Paint paint;
    private int contentInt = 129;
    private String content;
    private String contentNext;
    private int offsetX = 250;
    private int offsetY = 200;
    private float textSize = 45;
    private Rect rectMove;
    private Rect rectStatic;
    private float contentNextWidth;
    private float lineSpace;
    private float baseSpace;
    private float topSpace;
    private float moveWidth;
    private float staticWidth;

    private float progressY;
    private int alphaInt;
    private float scale = 1f;
    private float scaleMin = 0.75f;
    private long duration = 300;
    private int bitmapSpace = 6;

    private Bitmap bitmapLike;
    private Bitmap bitmapShining;

    private Animator animator;
    private boolean clickFlag = false;
    private AnimatorSet animatorSet;

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
        invalidate();
    }

    public int getAlphaInt() {
        return alphaInt;
    }

    public void setAlphaInt(int alphaInt) {
        this.alphaInt = alphaInt;
    }

    public int getContentInt() {
        return contentInt;
    }

    public void setContentInt(int contentInt) {
        this.contentInt = contentInt;
        invalidate();
    }

    public float getProgressY() {
        return progressY;
    }

    public void setProgressY(float progressY) {
        this.progressY = progressY;
        invalidate();
    }

    public JikeLikeView(Context context) {
        this(context, null);
    }

    public JikeLikeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JikeLikeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public JikeLikeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rectMove = new Rect();
        rectStatic = new Rect();
        setBackgroundColor(Color.GRAY);
        bitmapLike = BitmapFactory.decodeResource(getResources(), R.drawable.ic_messages_like_unselected);
        bitmapShining = BitmapFactory.decodeResource(getResources(), R.drawable.ic_messages_like_selected_shining);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        content = contentInt + "";
        contentNext = contentInt + 1 + "";

        paint.reset();
        paint.setTextSize(textSize);

        /*---------- 以下数据是基于（0，0）点测得的-----------*/
        //内容宽度
        contentNextWidth = paint.measureText(contentNext);
        //行高
        lineSpace = paint.descent() - paint.ascent();
        //顶部间隙
        topSpace = paint.ascent() - paint.getFontMetrics().top;
        //底部间隙，基线为0，故其Y坐标即为底部间隙
        baseSpace = paint.descent();
        //静止的那一块的宽度
        staticWidth = paint.measureText(contentNext, 0, contentNext.length() - 1 - NumberUtils.getNineCount(contentInt));
        //动的那一块的宽度
        moveWidth = paint.measureText(contentNext, contentNext.length() - 1 - NumberUtils.getNineCount(contentInt), contentNext.length());
        /*---------- 以上数据是基于（0，0）点测得的-----------*/

        //动态范围由于比静态范围上下各多一个行间距，故再顶部与底部分别上移和下移即可
        rectMove.top = (int) (offsetY + topSpace - lineSpace * 2);
        rectMove.left = (int) (contentNextWidth - (moveWidth) + offsetX);
        rectMove.right = (int) (contentNextWidth + offsetX);
        rectMove.bottom = (int) (baseSpace + offsetY + lineSpace);
//        paint.setStyle(Paint.Style.STROKE);
//        canvas.drawRect(rectMove, paint);

        rectStatic.left = offsetX;
        //顶部间距+偏移量Y，再往上（即-）一个行间距即为顶部范围
        rectStatic.top = (int) (offsetY + topSpace - lineSpace);
        rectStatic.right = (int) (offsetX + staticWidth);
        //底部间距+偏移量Y为最终显示底部的范围
        rectStatic.bottom = (int) (baseSpace + offsetY);
//        paint.setStyle(Paint.Style.STROKE);
//        canvas.drawRect(rectStatic, paint);

        canvas.save();
        canvas.clipRect(rectStatic);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText(content, offsetX, offsetY, paint);
        canvas.restore();

        canvas.save();
        canvas.clipRect(rectMove);
        canvas.translate(0, -lineSpace * progressY / 100);
        paint.setStyle(Paint.Style.FILL);
        //设置要被顶掉的数字的透明度
        paint.setAlpha(255 - alphaInt);
        canvas.drawText(content, offsetX, offsetY, paint);
        //设置下一个到来的数字的透明度
        paint.setAlpha(alphaInt);
        canvas.drawText(contentNext, offsetX, offsetY + lineSpace, paint);
        canvas.restore();


        //图标的绘制基于静态部分，并且有缩放效果
        paint.setAlpha(255);
        canvas.save();
        //设置缩放，中心为bitmap中心
        canvas.scale(scale, scale, rectStatic.left - bitmapLike.getWidth() / 2, rectStatic.top + (lineSpace / 2));
        canvas.drawBitmap(bitmapLike, rectStatic.left - bitmapLike.getWidth(), rectStatic.top + (lineSpace / 2) - (bitmapLike.getHeight() / 2), paint);
        if (clickFlag) {
            canvas.drawBitmap(bitmapShining, rectStatic.left - bitmapLike.getWidth() + bitmapSpace,
                    rectStatic.top + (lineSpace / 2) - (bitmapLike.getHeight() / 2) - bitmapShining.getHeight() / 2, paint);
        }
        canvas.restore();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //执行缩小动画
            animator = ObjectAnimator.ofFloat(this, "scale", 1, scaleMin);
            animator.setDuration(duration);
            animator.start();
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            setMainAnimate();
            return performClick();
        }
        return super.onTouchEvent(event);
    }

    /**
     * 主要动画的实现
     */
    private void setMainAnimate() {
        ObjectAnimator animatorText;
        ObjectAnimator animatorAlpha;
        ObjectAnimator animatorScale = ObjectAnimator.ofFloat(JikeLikeView.this, "scale", scaleMin, 1);

        if (clickFlag) {
            animatorText = ObjectAnimator.ofFloat(JikeLikeView.this, "progressY", 100, 0);
            animatorAlpha = ObjectAnimator.ofInt(JikeLikeView.this, "alphaInt", 255, 0);
        } else {
            animatorText = ObjectAnimator.ofFloat(JikeLikeView.this, "progressY", 0, 100);
            animatorAlpha = ObjectAnimator.ofInt(JikeLikeView.this, "alphaInt", 0, 255);
        }

        animatorSet = new AnimatorSet();
        animatorSet.setDuration(duration);
        animatorScale.setInterpolator(new OvershootInterpolator());
        animatorText.setInterpolator(new FastOutSlowInInterpolator());
        animatorSet.play(animatorText).with(animatorAlpha).with(animatorScale);
        animatorScale.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                bitmapLike = BitmapFactory.decodeResource(getResources(), clickFlag ?
                        R.drawable.ic_messages_like_unselected : R.drawable.ic_messages_like_selected);
                clickFlag = !clickFlag;
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animatorSet != null) {
            animatorSet.cancel();
        }
    }
}
