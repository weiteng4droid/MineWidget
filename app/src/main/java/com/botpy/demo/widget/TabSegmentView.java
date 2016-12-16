package com.botpy.demo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.botpy.demo.R;

/**
 * TabSegmentView
 *
 * Created by weiTeng on 16/12/1
 */
public class TabSegmentView extends View {

    private static final int DEFAULT_GAP          = 10;
    private static final int DEFAULT_TEXT_SIZE    = 14;
    private static final int DEFAULT_RADIUS       = 5;
    private static final int DEFAULT_DIVIDE_WIDTH = 2;

    private String[] mTexts;

    private Rect[] mCacheBounds;
    private Rect[] mTextBounds;

    private int mTextLeftRightGap;
    private int mTextTopBottomGap;

    private int mTextNormalColor;
    private int mTextSelectColor;
    private int mNormalColor;
    private int mSelectColor;
    private int mRadius;

    private Paint mTextPaint;
    private Paint mPaint;
    private Paint mDividePaint;

    private int mTouchSlop;
    private boolean inTapRegion;

    private float mStartX;
    private float mStartY;

    private int mCurrentIndex = -1;

    private int mTextSize;
    private Path mBackgroundPath;
    private Path mItemPath;

    private float mTextOffsetY;

    private double mDegree = Math.PI / 18;

    private OnSegmentItemClickListener mOnSegmentItemClickListener;

    public interface OnSegmentItemClickListener {
        void onSegmentItemClick(int index);
    }

    public void setOnSegmentItemClickListener(OnSegmentItemClickListener onSegmentItemClickListener) {
        mOnSegmentItemClickListener = onSegmentItemClickListener;
    }

    public TabSegmentView(Context context) {
        this(context, null);
    }

    public TabSegmentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabSegmentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TabSegmentView);

        String texts = ta.getString(R.styleable.TabSegmentView_ts_texts);
        if (texts != null) {
            mTexts = texts.split("\\|");
        }

        mTextSize = ta.getDimensionPixelSize(R.styleable.TabSegmentView_ts_textSize,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                        DEFAULT_TEXT_SIZE, context.getResources().getDisplayMetrics()));

        mTextNormalColor = ta.getColor(R.styleable.TabSegmentView_ts_textNormalColor, 0xff9a9a9a);
        mTextSelectColor = ta.getColor(R.styleable.TabSegmentView_ts_textSelectColor, 0xff3b4245);
        mNormalColor = ta.getColor(R.styleable.TabSegmentView_ts_normalColor, 0xfff5f5f5);
        mSelectColor = ta.getColor(R.styleable.TabSegmentView_ts_selectColor, 0xffffffff);

        mRadius = ta.getDimensionPixelSize(R.styleable.TabSegmentView_ts_radius,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        DEFAULT_RADIUS, context.getResources().getDisplayMetrics()));

        mTextLeftRightGap = ta.getDimensionPixelSize(R.styleable.TabSegmentView_ts_textLeftRightGap, DEFAULT_GAP);
        mTextTopBottomGap = ta.getDimensionPixelSize(R.styleable.TabSegmentView_ts_textTopBottomGap, DEFAULT_GAP);

        mCurrentIndex = ta.getInt(R.styleable.TabSegmentView_ts_defaultIndex, 0);
        int divideColor = ta.getColor(R.styleable.TabSegmentView_ts_divideColor, 0xffdfe1e3);
        int divideWidth = ta.getDimensionPixelSize(R.styleable.TabSegmentView_ts_divideWidth,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        DEFAULT_DIVIDE_WIDTH, context.getResources().getDisplayMetrics()));

        ta.recycle();

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG | Paint.DITHER_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextNormalColor);
        mTextOffsetY = (mTextPaint.ascent() + mTextPaint.descent()) / 2;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mNormalColor);

        mDividePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDividePaint.setStyle(Paint.Style.FILL);
        mDividePaint.setColor(divideColor);
        mDividePaint.setStrokeWidth(divideWidth);

        // 对触摸的点进行定义，提高精度
        int touchSlop = 0;
        if (context == null) {
            touchSlop = ViewConfiguration.getTouchSlop();
        } else {
            final ViewConfiguration config = ViewConfiguration.get(context);
            touchSlop = config.getScaledTouchSlop();
        }

        mTouchSlop = touchSlop * touchSlop;
        inTapRegion = false;
    }

    public void setTextSize(int textSize) {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }

    public void setTextSize(int unit, int textSize) {
        mTextPaint.setTextSize((int) (TypedValue.applyDimension(unit, textSize, getContext().getResources().getDisplayMetrics())));

        if (textSize != mTextSize) {
            mTextSize = textSize;
            requestLayout();
            invalidate();
        }
    }

    public void setTexts(String... texts) {
        mTexts = texts;
        if (mTexts != null) {
            requestLayout();
            invalidate();
        }
    }

    public void setCurrentIndex(int index) {
        mCurrentIndex = index;
        createItemIndexPath(mCurrentIndex);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if(mCacheBounds == null){
            mCacheBounds = new Rect[mTexts.length];
        }
        if(mTextBounds == null){
            mTextBounds = new Rect[mTexts.length];
        }
        calculateTextBound();

        int width;
        int height;
        int realWidth = widthSize - getPaddingLeft() - getPaddingRight();
        int realHeight = heightSize - getPaddingTop() - getPaddingBottom();

        // measure width
        if (widthMode == MeasureSpec.EXACTLY) {
            width = realWidth;
        } else {
            int maxWidth = 0;
            for (Rect rect : mTextBounds) {
                int minWidth = rect.width() + 2 * mTextLeftRightGap;
                if (maxWidth < minWidth) {
                    maxWidth = minWidth;
                }
            }
            if (realWidth <= maxWidth) {
                width = maxWidth * mTexts.length;
            } else {
                width = realWidth;
            }
        }

        // measure height
        if (heightMode == MeasureSpec.EXACTLY) {
            height = realHeight;
        } else {
            int minHeight = mTextBounds[0].height() + 2 * mTextTopBottomGap;
            height = Math.min(minHeight, realHeight);
        }

        setMeasuredDimension(width, height);
        calculateCacheBounds(width, height);

        createBackgroundPath();
        createItemIndexPath(mCurrentIndex);
    }

    /**
     * 计算缓存的的矩形的大小
     */
    private void calculateCacheBounds(int width, int height) {
        int len = mCacheBounds.length;
        int singleWidth = width / len;
        for (int i = 0; i < len; i++) {
            if (mCacheBounds[i] == null) {
                mCacheBounds[i] = new Rect();
            }
            Rect rect = mCacheBounds[i];
            if (i > 0) {
                rect.left = mCacheBounds[i -1].right;
            } else {
                rect.left = getPaddingLeft();
            }
            rect.top = getPaddingTop();
            rect.right = rect.left + singleWidth;
            rect.bottom = rect.top + height;

            Log.d("TabSegmentView", rect.toShortString());
        }
    }

    /**
     * 计算文字矩形大小
     */
    private void calculateTextBound() {
        for(int i = 0; i < mTexts.length; i++){
            String itemText = mTexts[i];
            if(mTextBounds[i] == null ){
                mTextBounds[i] = new Rect();
            }
            mPaint.getTextBounds(itemText, 0, itemText.length(), mTextBounds[i]);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                inTapRegion = true;

                mStartX = event.getX();
                mStartY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:

                int dx = (int) (event.getX() - mStartX);
                int dy = (int) (event.getY() - mStartY);

                int distance = dx * dx + dy * dy;
                if (distance > mTouchSlop) {
                    inTapRegion = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (inTapRegion) {
                    setSelectState();
                }
                break;
        }
        return true;
    }

    private void setSelectState() {
        int index = -1;
        for (int i = 0; i < mCacheBounds.length; i++) {
            Rect rect = mCacheBounds[i];
            if ((mStartX > rect.left && mStartX < rect.right) &&
                    (mStartY > rect.top && mStartY < rect.bottom)) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            if (mCurrentIndex != index) {
                createItemIndexPath(index);
                mCurrentIndex = index;
                if (mOnSegmentItemClickListener != null) {
                    mOnSegmentItemClickListener.onSegmentItemClick(index);
                }
            }
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);

        drawItem(canvas);

        drawText(canvas);

        drawDivideLine(canvas);
    }

    private void createBackgroundPath() {
        Rect rect = mCacheBounds[0];
        Rect end = mCacheBounds[mTexts.length - 1];
        int left = rect.left;
        int top = rect.top;
        int right = end.right;
        int bottom = end.bottom;
        mBackgroundPath = new Path();
        mBackgroundPath.moveTo(left + mRadius, top);
        mBackgroundPath.lineTo(right - mRadius, top);
        mBackgroundPath.arcTo(new RectF(right - mRadius * 2, top, right, top + mRadius * 2), -90, 90);

        mBackgroundPath.lineTo(right, bottom);
        mBackgroundPath.lineTo(left, bottom);

        mBackgroundPath.lineTo(left, top + mRadius);
        mBackgroundPath.arcTo(new RectF(left, top, left + mRadius * 2, top + mRadius * 2), 180, 90);
        mBackgroundPath.close();
    }

    private void createItemIndexPath(int index) {
        if (index == 0) {
            createLeftItemPath();
        } else if (index == mTexts.length - 1) {
            createRightItemPath();
        } else {
            createCenterItemPath(index);
        }
    }

    /**
     * 创建最左边 item 的路径背景
     */
    private void createLeftItemPath() {
        Rect rect = mCacheBounds[0];
        mItemPath = new Path();
        mItemPath.moveTo(rect.left, rect.bottom);
        mItemPath.lineTo(rect.left, rect.top + mRadius);
        mItemPath.arcTo(new RectF(rect.left, rect.top, rect.left + 2 * mRadius, rect.top + 2 * mRadius), 180, 90);

        mItemPath.lineTo((float) (rect.right - mRadius * Math.tan(Math.PI / 4 + mDegree / 2)), rect.top);
        mItemPath.arcTo(createRightAcrRectF(rect), -90, sweepAngle());

        mItemPath.lineTo(rect.right + calculateDiff(rect), rect.bottom);
        mItemPath.lineTo(rect.left, rect.bottom);
        mItemPath.close();
    }

    /**
     * 计算梯形边角扫过的圆角
     */
    private float sweepAngle() {
        return (float) ((Math.PI / 2 - mDegree) * (180 / Math.PI));
    }

    /**
     * 计算右边梯形圆角圆弧的矩形区域
     */
    private RectF createRightAcrRectF(Rect rect) {
        float left = (float) (rect.right - Math.tan(Math.PI / 4 + mDegree / 2) * mRadius - mRadius);
        float right = (float) (rect.right - Math.tan(Math.PI / 4 + mDegree / 2) * mRadius + mRadius);
        return new RectF(left, rect.top, right, rect.top + 2 * mRadius);
    }


    /**
     * 计算左边梯形圆角圆弧的矩形区域
     */
    private RectF createLeftAcrRectF(Rect rect) {
        float left = (float) (rect.left -(mRadius - Math.tan(Math.PI / 4 - mDegree / 2) * mRadius));
        float right = (float) (rect.left + Math.tan(Math.PI / 4 - mDegree / 2) * mRadius + mRadius);
        return new RectF(left, rect.top, right, rect.top + 2 * mRadius);
    }

    /**
     * 计算腰部连接圆弧的点的X坐标
     */
    private float createWaistX(Rect rect) {
        return (float) (rect.left - (Math.cos(mDegree) * mRadius - Math.tan(Math.PI / 4 - mDegree / 2) * mRadius));
    }

    /**
     * 计算腰部连接圆弧的点的Y坐标
     */
    private float createWaistY(Rect rect) {
        return (float) (rect.top + (mRadius - Math.sin(mDegree) * mRadius));
    }

    /**
     * 创建最右边 item 的路径背景
     */
    private void createRightItemPath() {
        Rect rect = mCacheBounds[mTexts.length - 1];
        mItemPath = new Path();

        mItemPath.moveTo(createWaistX(rect), createWaistY(rect));
        mItemPath.arcTo(createLeftAcrRectF(rect), 180 + (90 - sweepAngle()), sweepAngle());
        mItemPath.lineTo(rect.right - mRadius, rect.top);
        mItemPath.arcTo(new RectF(rect.right - 2 * mRadius, rect.top, rect.right, rect.top + 2 * mRadius), -90, 90);

        mItemPath.lineTo(rect.right, rect.bottom);
        mItemPath.lineTo(rect.left - calculateDiff(rect), rect.bottom);
        mItemPath.lineTo(createWaistX(rect), createWaistY(rect));
        mItemPath.close();
    }

    /**
     * 创建中间 item 的路径背景
     */
    private void createCenterItemPath(int index) {
        Rect rect = mCacheBounds[index];
        mItemPath = new Path();

        mItemPath.moveTo(createWaistX(rect), createWaistY(rect));
        mItemPath.arcTo(createLeftAcrRectF(rect), 180 + (90 - sweepAngle()), sweepAngle());
        mItemPath.lineTo((float) (rect.right - mRadius * Math.tan(Math.PI / 4 + mDegree / 2)), rect.top);
        mItemPath.arcTo(createRightAcrRectF(rect), -90, sweepAngle());

        mItemPath.lineTo(rect.right + calculateDiff(rect), rect.bottom);
        mItemPath.lineTo(rect.left - calculateDiff(rect), rect.bottom);

        mItemPath.lineTo(createWaistX(rect), createWaistY(rect));
        mItemPath.close();
    }

    private float calculateDiff(Rect rect) {
        return (float) (rect.height() * Math.tan(mDegree));
    }

    /**
     * 画圆角背景
     */
    private void drawBackground(Canvas canvas) {
        mPaint.setColor(mNormalColor);
        canvas.drawPath(mBackgroundPath, mPaint);
    }

    /**
     * 画选中的背景
     */
    private void drawItem(Canvas canvas) {
        mPaint.setColor(mSelectColor);
        canvas.drawPath(mItemPath, mPaint);
    }

    /**
     * 画文字
     */
    private void drawText(Canvas canvas) {
        for (int i = 0; i < mCacheBounds.length; i++) {
            Rect rect = mCacheBounds[i];
            if (rect != null) {
                if (i == mCurrentIndex) {
                    mTextPaint.setColor(mTextSelectColor);
                } else {
                    mTextPaint.setColor(mTextNormalColor);
                }
                canvas.drawText(mTexts[i], rect.centerX(), rect.centerY() - mTextOffsetY, mTextPaint);
            }
        }
    }

    /**
     * 画分割线
     */
    private void drawDivideLine(Canvas canvas) {
        int len = mTexts.length;
        if (mCurrentIndex < len / 2) {
            for (int i = mCurrentIndex + 1; i < len - 1; i++) {
                Rect rect = mCacheBounds[i];
                int height = rect.height() / 3;
                canvas.drawLine(rect.right, rect.top + height, rect.right, rect.top + 2 * height, mDividePaint);
            }
        } else {
            for (int i = 1; i < mCurrentIndex; i++) {
                Rect rect = mCacheBounds[i];
                int height = rect.height() / 3;
                canvas.drawLine(rect.left, rect.top + height, rect.left, rect.top + 2 * height, mDividePaint);
            }
        }
    }
}
