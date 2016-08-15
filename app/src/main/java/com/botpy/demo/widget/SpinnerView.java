package com.botpy.demo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.botpy.demo.R;

/**
 * Created by weiTeng on 2016/8/3.
 */
public class SpinnerView extends View {

    /** 外圈小点的半径 */
    private static final int POINT_RADIUS  = 8;
    /** 内圈静止的圆的半径 */
    private static final int CIRCLE_RADIUS = 50;
    /** 旋转的默认速度 */
    private static final int VELOCITY      = 50;

    private int mOutRadius = POINT_RADIUS;
    private int mInnerRadius = CIRCLE_RADIUS;

    private int mVelocity;
    private int mSpace = 20;

    private int mAngle = 0;

    private Paint mPaint;
    private Rect mRect;

    public SpinnerView(Context context) {
        this(context, null);
    }

    public SpinnerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpinnerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SpinnerView);
        mInnerRadius = ta.getDimensionPixelSize(R.styleable.SpinnerView_velocity, CIRCLE_RADIUS);
        mOutRadius = ta.getDimensionPixelSize(R.styleable.SpinnerView_outer_radius, POINT_RADIUS);
        mVelocity = ta.getInt(R.styleable.SpinnerView_velocity, VELOCITY);
        ta.recycle();

        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.CYAN);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec)- getPaddingLeft() - getPaddingRight();

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();

        int width, height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            int tempWidth = mInnerRadius + mSpace + mOutRadius;
            width = Math.min(tempWidth, widthSize);
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            int tempHeight = mInnerRadius + mSpace + mOutRadius;
            height = Math.min(tempHeight, heightSize);
        }

        width = Math.min(width, height);
        setMeasuredDimension(width + getPaddingLeft() + getPaddingRight(), width + getPaddingTop() + getPaddingBottom());

        mRect = new Rect();
        mRect.left = getPaddingLeft();
        mRect.top = getPaddingTop();
        mRect.right = getPaddingLeft() + width;
        mRect.bottom = getPaddingBottom() + width;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(mRect.centerX(), mRect.centerY(), mInnerRadius, mPaint);
        /*
        for (float i = mAngle, x) {
            canvas.save();
            canvas.rotate(i);

            canvas.drawText(skills[(int)x], 0, -mArcTextRadius, mTextPaint);

            canvas.restore();
        }
        */
    }
}
