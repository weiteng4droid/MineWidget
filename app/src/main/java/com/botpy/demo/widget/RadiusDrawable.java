package com.botpy.demo.widget;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

/**
 * Created by weiTeng on 15/11/28.
 * 备注：画图形的类, 圆形Drawable
 */
public class RadiusDrawable extends Drawable {

    private int topLeftRadius;
    private int topRightRadius;
    private int bottomLeftRadius;
    private int bottomRightRadius;

    private int left;
    private int top;
    private int right;
    private int buttom;

    private int width;
    private int height;

    private Paint mPaint;
    private int color;
    private boolean isStroke;
    private int strokeWidth = 0;
    private int strokeColor;

    private Path path;

    public RadiusDrawable(int topLeftRadius, int topRightRadius, int bottomLeftRadius,
                          int bottomRightRadius, boolean isStroke, int color){
        this.topLeftRadius = topLeftRadius;
        this.topRightRadius = topRightRadius;
        this.bottomLeftRadius = bottomLeftRadius;
        this.bottomRightRadius = bottomRightRadius;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);  // 消除锯齿
        this.isStroke = isStroke;
        this.color = color;
    }

    public RadiusDrawable(int radius, boolean isStroke, int color){
        this.topLeftRadius = this.topRightRadius = this.bottomLeftRadius = this.bottomRightRadius = radius;
        this.isStroke = isStroke;
        this.color = color;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void setStrokeWidth(int width){
        strokeWidth = width;
        setBounds(left, top, right, buttom);
    }


    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
    }

    public void setColor(int color){
        this.color = color;
    }

    public void setRadius(int radius){
        this.topLeftRadius = this.topRightRadius = this.bottomLeftRadius = this.bottomRightRadius = radius;
    }

    public void setRadiuses(int topLeftRadius, int topRightRadius, int bottomLeftRadius, int bottomRightRadius){
        this.topLeftRadius = topLeftRadius;
        this.topRightRadius = topRightRadius;
        this.bottomLeftRadius = bottomLeftRadius;
        this.bottomRightRadius = bottomRightRadius;
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);

        this.left = left;
        this.top = top;
        this.right = right;
        this.buttom = bottom;

        if(isStroke){
            int halfStrokeWidth = strokeWidth / 2;
            left += halfStrokeWidth;
            top += halfStrokeWidth;
            right -= halfStrokeWidth;
            bottom -= halfStrokeWidth;
        }

        path = new Path();
        path.moveTo(left + topLeftRadius, top);
        path.lineTo(right - topRightRadius, top);
        path.arcTo(new RectF(right - topRightRadius * 2, top, right, top + topRightRadius * 2), -90, 90);

        path.lineTo(right, bottom - bottomRightRadius);
        path.arcTo(new RectF(right - bottomRightRadius * 2, bottom - bottomRightRadius * 2, right, bottom), 0, 90);

        path.lineTo(left + bottomLeftRadius, bottom);
        path.arcTo(new RectF(left, bottom - bottomLeftRadius * 2, left + bottomLeftRadius * 2, bottom), 90, 90);

        path.lineTo(left, top + topLeftRadius);
        path.arcTo(new RectF(left, top, left + topLeftRadius * 2, top + topLeftRadius * 2), 180, 90);
        path.close();
    }

    @Override
    public void draw(Canvas canvas){
        if(color != 0){
            mPaint.setColor(color);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawPath(path, mPaint);
        }

        if(strokeWidth >0){
            mPaint.setColor(strokeColor);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeJoin(Paint.Join.MITER);
            mPaint.setStrokeWidth(strokeWidth);
            canvas.drawPath(path, mPaint);
        }
    }

    @Override
    public void setAlpha(int alpha){

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
