package com.example.dell.customviewdemo.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.dell.customviewdemo.R;
import com.example.dell.customviewdemo.Utils;

public class JikeLikeView extends View {

    private Paint paint;
    private int contentInt = 129;
    private String content;
    private String contentNext;
    private int offsetX = 250;
    private int offsetY = 200;
    private float textSize = 80;
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

    private Bitmap bitmap;

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
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_messages_like_unselected);
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
        staticWidth = paint.measureText(contentNext, 0, contentNext.length() - 1 - Utils.getNineCount(contentInt));
        //动的那一块的宽度
        moveWidth = paint.measureText(contentNext, contentNext.length() - 1 - Utils.getNineCount(contentInt), contentNext.length());
        /*---------- 以上数据是基于（0，0）点测得的-----------*/

        //动态范围由于比静态范围上下各多一个行间距，故再顶部与底部分别上移和下移即可
        rectMove.top = (int) (offsetY + topSpace - lineSpace * 2);
        rectMove.left = (int) (contentNextWidth - (moveWidth) + offsetX);
        rectMove.right = (int) (contentNextWidth + offsetX);
        rectMove.bottom = (int) (baseSpace + offsetY + lineSpace);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(rectMove, paint);

        rectStatic.left = offsetX;
        //顶部间距+偏移量Y，再往上（即-）一个行间距即为顶部范围
        rectStatic.top = (int) (offsetY + topSpace - lineSpace);
        rectStatic.right = (int) (offsetX + staticWidth);
        //底部间距+偏移量Y为最终显示底部的范围
        rectStatic.bottom = (int) (baseSpace + offsetY);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(rectStatic, paint);

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

        //图标的绘制基于静态部分
        paint.setAlpha(255);
        canvas.drawBitmap(bitmap, rectStatic.left - bitmap.getWidth(), rectStatic.top + (lineSpace / 2) - (bitmap.getHeight() / 2), paint);
    }
}
