package com.example.dell.customviewdemo.customview;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.dell.customviewdemo.Utils;

public class JikeLikeView extends View {

    private Paint paint;
    private int contentInt = 129;
    private String content;
    private String contentNext;
    private int offsetX = 250;
    private int offsetY = 500;
    private float textSize = 80;
    private Rect rectMove;
    private Rect rectStatic;
    private float contentNextWidth;
    private float lineSpace;
    private float moveWidth;
    private float staticWidth;

    private float progressY;
    private int alphaInt;

    public int getAlphaInt() {
        return alphaInt;
    }

    public void setAlphaInt(int alphaInt) {
        this.alphaInt = alphaInt;
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
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        content = contentInt + "";
        contentNext = contentInt + 1 + "";

        paint.reset();
        paint.setTextSize(textSize);

        //内容宽度
        contentNextWidth = paint.measureText(contentNext);
        //行高
        lineSpace = paint.getFontSpacing();
        //静止的那一块的宽度
        staticWidth = paint.measureText(contentNext, 0, contentNext.length() - 1 - Utils.getNineCount(contentInt));
        //动的那一块的宽度
        moveWidth = paint.measureText(contentNext, contentNext.length() - 1 - Utils.getNineCount(contentInt), contentNext.length());

        rectMove.left = (int) (contentNextWidth - (moveWidth) + offsetX);
        rectMove.top = (int) (offsetY - lineSpace * 2);
        rectMove.right = (int) (contentNextWidth + offsetX);
        rectMove.bottom = (int) (offsetY + lineSpace);
//        paint.setStyle(Paint.Style.STROKE);
//        canvas.drawRect(rectMove, paint);

        rectStatic.left = offsetX;
        rectStatic.top = (int) (offsetY - lineSpace * 2);
        rectStatic.right = (int) (offsetX + staticWidth);
        rectStatic.bottom = (int) (offsetY + lineSpace);

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


    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
