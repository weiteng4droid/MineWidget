package com.botpy.demo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * 测试绘图的View
 *
 * @author weiTeng on 2016/3/3.
 */
public class TestView extends View {

    private static final String TAG = "TestView";

    private static final float  STROKE_WIDTH        = 1F / 256F, // 描边宽度占比
                                LINE_LENGTH         = 3F / 32F,  // 线段长度占比
                                CRICLE_LARGER_RADIU = 3F / 32F,  // 大圆半径
                                CRICLE_SMALL_RADIU  = 5F / 64F,  // 小圆半径
                                ARC_RADIU           = 1F / 8F,   // 弧半径
                                ARC_TEXT_RADIU      = 5F / 32F;  // 弧围绕文字半径

    private static final float  LINE_GAP            = 1F / 48F;  // 线段之间的间距

    private Paint mPaint;
    private Paint mTextPaint;

    private int mSize;
    private float mStrokeWidth;
    private float mLineLen;
    private float mMaxRadius;
    private float mMinRadius;
    private float mLineGap;
    private float mArcRadius;
    private float mArcTextRadius;
    private float mTextOffsetY;

    private PointF mCenterPoint = new PointF();

    public TestView(Context context) {
        this(context, null);
    }

    public TestView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int realWidth = widthSize - getPaddingLeft() - getPaddingRight();
        int realHeight = heightSize - getPaddingTop() - getPaddingBottom();

        realWidth = Math.min(realWidth, realHeight);

        setMeasuredDimension(realWidth + getPaddingLeft() + getPaddingRight(), realWidth + getPaddingBottom() + getPaddingTop());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mSize = w;
        calculatePosition();
    }

    private void calculatePosition() {

        mStrokeWidth = mSize * STROKE_WIDTH;

        mCenterPoint.x = mSize / 2;
        mCenterPoint.y = mSize / 2 + mSize * CRICLE_LARGER_RADIU;

        mMaxRadius = mSize * CRICLE_LARGER_RADIU;
        mMinRadius = mSize * CRICLE_SMALL_RADIU;

        mLineLen = mSize * LINE_LENGTH;
        mLineGap = mSize * LINE_GAP;
        mArcRadius = mSize * ARC_RADIU;
        mArcTextRadius = mSize * ARC_TEXT_RADIU;

        initPaintParam();
    }

    private void initPaintParam() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG|Paint.SUBPIXEL_TEXT_FLAG);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(30);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mTextOffsetY = (mPaint.descent() + mPaint.ascent()) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 画背景
        canvas.drawColor(0xFFF29B76);

        // 画中心圆
        drawCenterCircle(canvas);

        // 画左边分支
        drawLeftBrunch(canvas);

        // 画左边单圆
        drawLeftSingleCircle(canvas);

        // 画右边单圆
        drawRightSingleCircle(canvas);

        // 画下边单圆
        drawBottomSingleCircle(canvas);

        // 画右上角的圆
        drawRightTopCircle(canvas);
    }

    private void drawRightTopCircle(Canvas canvas) {
        canvas.save();

        canvas.translate(mCenterPoint.x, mCenterPoint.y);
        canvas.rotate(30);

        canvas.drawLine(0, -mMaxRadius, 0, -mLineLen * 2, mPaint);
        canvas.drawCircle(0, -mLineLen * 2 - mMaxRadius, mMaxRadius, mPaint);
        canvas.drawText("skill", 0, -mLineLen * 2 - mMaxRadius - mTextOffsetY, mTextPaint);
        canvas.drawArc(new RectF(0 - mArcRadius, -mLineLen * 2 - mMaxRadius - mArcRadius, mArcRadius, -mLineLen * 2 - mMaxRadius + mArcRadius), -187.5f, 135, false, mPaint);

        canvas.save();
        canvas.translate(0, -mLineLen * 2 - mMaxRadius);
        String[] skills = new String[]{"c", "php", "vb", "python"};
        for (float i = -90F, x = 0; x < 4; i += 33.75F, x++) {
            canvas.save();
            canvas.rotate(i);

            canvas.drawText(skills[(int)x], 0, -mArcTextRadius, mTextPaint);

            canvas.restore();
        }

        canvas.restore();
        canvas.restore();
    }

    private void drawBottomSingleCircle(Canvas canvas) {
        drawSingleCircle(canvas, 180, "爱编程");
    }

    private void drawRightSingleCircle(Canvas canvas) {
        drawSingleCircle(canvas, 100, "爱老婆");
    }

    private void drawSingleCircle(Canvas canvas, float degrees, String text) {
        canvas.save();

        canvas.translate(mCenterPoint.x, mCenterPoint.y);
        canvas.rotate(degrees);

        canvas.drawLine(0, -mMaxRadius - mLineGap, 0, -mMaxRadius - mLineGap - mLineLen, mPaint);
        canvas.drawCircle(0, -mMaxRadius - mLineGap * 2 - mLineLen - mMinRadius, mMinRadius, mPaint);
        canvas.drawText(text, 0, -mMaxRadius - mLineGap * 2 - mLineLen - mMinRadius - mTextOffsetY, mTextPaint);

        canvas.restore();
    }

    private void drawLeftSingleCircle(Canvas canvas) {
        drawSingleCircle(canvas, -100, "爱生活");
    }

    private void drawLeftBrunch(Canvas canvas) {
        canvas.save();

        canvas.translate(mCenterPoint.x, mCenterPoint.y);
        canvas.rotate(-30);

        canvas.drawLine(0, -mMaxRadius, 0, -mLineLen * 2, mPaint);
        canvas.drawCircle(0, -mLineLen * 2 - mMaxRadius, mMaxRadius, mPaint);
        canvas.drawText("Java", 0, -mLineLen * 2 - mMaxRadius - mTextOffsetY, mTextPaint);
        canvas.drawLine(0, -mLineLen * 2 - mMaxRadius * 2, 0, -mLineLen * 3 - mMaxRadius * 2, mPaint);
        canvas.drawCircle(0, -mLineLen * 3 - mMaxRadius * 3, mMaxRadius, mPaint);
        canvas.drawText("Android", 0, -mLineLen * 3 - mMaxRadius * 3 - mTextOffsetY, mTextPaint);

        canvas.restore();
    }

    private void drawCenterCircle(Canvas canvas) {
        canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, mMaxRadius, mPaint);
        canvas.drawText("weiTeng", mCenterPoint.x, mCenterPoint.y - mTextOffsetY, mTextPaint);
    }
}
