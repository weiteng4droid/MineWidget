package com.botpy.demo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import com.botpy.demo.R;

/**
 * 带有边框的TextView
 * @author  weiTeng on 16/1/3.
 */
public class FrameTextView extends TextView {

    private int mFrameWidth;
    private int mFrameColor;
    private int mRoundRadius;

    private Paint mPaint;

    public FrameTextView(Context context) {
        this(context, null);
    }

    public FrameTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FrameTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FrameTextView);

        mFrameWidth = ta.getDimensionPixelSize(R.styleable.FrameTextView_frame_width, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 2, context.getResources().getDisplayMetrics()));
        mFrameColor = ta.getColor(R.styleable.FrameTextView_frame_color, 0xff0099cc);
        mRoundRadius = ta.getDimensionPixelSize(R.styleable.FrameTextView_frame_round_radius, 0);

        ta.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mFrameWidth);
        mPaint.setColor(mFrameColor);
        mPaint.setStrokeJoin(Paint.Join.MITER);
    }

    public void setFrameWidth(int frameWidth){
        if(mFrameWidth != frameWidth){
            mFrameWidth = frameWidth;
            mPaint.setStrokeWidth(mFrameWidth);
            invalidate();
        }
    }

    public void setFrameColor(@ColorRes int frameColor){
        if(mFrameColor != frameColor){
            mFrameColor = frameColor;
            mPaint.setColor(getResources().getColor(mFrameColor));
            invalidate();
        }
    }

    public void setRoundRadius(int roundRadius){
        if(mRoundRadius != roundRadius){
            mRoundRadius = roundRadius;
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        if(mRoundRadius == 0) {
            canvas.drawRect(0, 0, width, height, mPaint);
        }else{
            Path mPath = new Path();
            mPath.moveTo(mRoundRadius, 0);
            mPath.lineTo(width - mRoundRadius, 0);

            mPath.arcTo(new RectF(width - mRoundRadius, 0, width, mRoundRadius), 270, 90);
            mPath.lineTo(width, height - mRoundRadius);

            mPath.arcTo(new RectF(width - mRoundRadius, height - mRoundRadius, width, height), 0, 90);
            mPath.lineTo(mRoundRadius, height);

            mPath.arcTo(new RectF(0, height - mRoundRadius, mRoundRadius, height), 90, 90);
            mPath.lineTo(0, mRoundRadius);

            mPath.arcTo(new RectF(0, 0, mRoundRadius, mRoundRadius), 180, 90);
            mPath.close();

            canvas.drawPath(mPath, mPaint);
        }
        canvas.save();
        canvas.translate(mRoundRadius, mRoundRadius);
        super.onDraw(canvas);
        canvas.restore();
    }
}
