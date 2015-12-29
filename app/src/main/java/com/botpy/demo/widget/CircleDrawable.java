package com.botpy.demo.widget;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

/**
 * 圆形图片背景
 * @author weiTeng
 * @since 2015-12-05 14:58:19
 */
public class CircleDrawable extends Drawable {

    private boolean mIsStroke;

    private int mColor;
    private Paint mPaint;
    private int mStrokeWidth;
    private int mStrokeColor;

    private int mLeft;
    private int mTop;
    private int mRight;
    private int mBottom;

    private int mRadius;

    public CircleDrawable(int color){
        this.mColor = color;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mColor);
    }

    public CircleDrawable(boolean isStroke, int color){
        this.mIsStroke = isStroke;
        this.mColor = color;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mColor);
    }

    public void setStrokeColor(int strokeColor){
        this.mStrokeColor = strokeColor;
    }

    public void setStrokeWidth(int strokeWidth){
        this.mStrokeWidth = strokeWidth;
        setBounds(mLeft, mTop, mRight, mBottom);
    }


    @Override
    public void setBounds(int left, int top, int right, int bottom) {

        int tempWidth = right - left;
        int tempHeight = bottom - top;
        if(tempWidth != tempHeight){
            if(tempWidth < tempHeight){
                mRadius = (int) (tempWidth / 2.0f + 0.5f);
            }else{
                mRadius = (int) (tempHeight / 2.0f + 0.5f);
            }
        }else{
            mRadius = (int) (tempWidth / 2.0f + 0.5f);
        }

        mLeft = left;
        mTop = top;
        mRight = left + mRadius * 2;
        mBottom = top + mRadius * 2;

        if(mIsStroke){
            int halfStrokeWidth = (int) (mStrokeWidth / 2.0f + 0.5);
            mLeft += halfStrokeWidth;
            mTop += halfStrokeWidth;
            mRight -= halfStrokeWidth;
            mBottom -= halfStrokeWidth;
        }

        super.setBounds(mLeft, mTop, mRight, mBottom);
    }

    @Override
    public void draw(Canvas canvas) {
        int centerX = (int) ((mRight - mLeft) / 2.0f + 0.5f);
        int centerY = (int) ((mBottom - mTop) / 2.0f + 0.5f);
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(centerX, centerY, mRadius, mPaint);

        if(mStrokeWidth > 0){
            mPaint.setColor(mStrokeColor);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeWidth(mStrokeWidth);
            canvas.drawCircle(centerX, centerY, mRadius, mPaint);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.getAlpha();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.getColorFilter();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
