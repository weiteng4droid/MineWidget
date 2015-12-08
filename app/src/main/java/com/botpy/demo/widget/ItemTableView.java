package com.botpy.demo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.botpy.demo.R;

/**
 * 切换item条目的控件
 * @author weiTeng
 * @since 2015-12-1 10:37:11
 */
public class ItemTableView extends View{

    private String[] mTexts;

    private Rect[] mCacheBounds;
    private Rect[] mTextBounds;

    private int mLineBorder;

    private int mSingleChildWidth;
    private int mSingleChildHeight;

    private int mHorizonGap;
    private int mVerticalGap;

    private int mColors;
    private int mBorderColor;

    private Paint mPaint;
    private Paint mLinePaint;

    private int mTouchSlop;
    private boolean inTapRegion;

    private float mStartX;
    private float mStartY;

    private int mCurrentIndex = -1;

    private int mTextSize;

    private OnSegmentItemClickListener mOnSegmentItemClickListener;

    public interface OnSegmentItemClickListener{
        void onSegmentIitemClick(int index);
    }

    public void setOnSegmentItemClickListener(OnSegmentItemClickListener onSegmentItemClickListener) {
        mOnSegmentItemClickListener = onSegmentItemClickListener;
    }

    public ItemTableView(Context context) {
        this(context, null);
    }

    public ItemTableView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemTableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ItemTableView);
        String texts = ta.getString(R.styleable.ItemTableView_it_texts);
        if(texts != null){
            mTexts = texts.split("\\|");
        }
        mHorizonGap = ta.getDimensionPixelSize(R.styleable.ItemTableView_it_horizonGap, 0);
        mVerticalGap = ta.getDimensionPixelSize(R.styleable.ItemTableView_it_verticalGap, 0);
        mLineBorder = ta.getDimensionPixelSize(R.styleable.ItemTableView_it_lineBorder, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.5f, context.getResources().getDisplayMetrics()));
        mTextSize = ta.getDimensionPixelSize(R.styleable.ItemTableView_it_textSize, (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, context.getResources().getDisplayMetrics()));
        mColors = ta.getColor(R.styleable.ItemTableView_it_textColor, 0xff0099cc);
        mBorderColor = ta.getColor(R.styleable.ItemTableView_it_lineBorderColor, 0xffa0a9b0);
        ta.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(mTextSize);
        mPaint.setColor(mColors);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStrokeWidth(mLineBorder);
        mLinePaint.setColor(mBorderColor);

        // 对触摸的点进行定义，提交精度
        int touchSlop = 0;
        if(context == null){
            touchSlop = ViewConfiguration.getTouchSlop();
        }else{
            final ViewConfiguration config = ViewConfiguration.get(context);
            touchSlop = config.getScaledTouchSlop();
        }

        mTouchSlop = touchSlop * touchSlop;
        inTapRegion = false;
    }

    public void setTextSize(int textSize){
        setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }

    public void setTextSize(int unit, int textSize){
        mPaint.setTextSize((int) (TypedValue.applyDimension(unit, textSize, getContext().getResources().getDisplayMetrics())));

        if(textSize != mTextSize){
            mTextSize = textSize;
            requestLayout();
        }
    }

    public void setTexts(String... texts){
        mTexts = texts;
        if(mTexts != null){
            requestLayout();
        }
    }

    public void setCurrentIndex(int index){
        mCurrentIndex = index;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = 0;
        int height = 0;

        int singleTextWidth = 0;
        int singleTextHeight = 0;

        if(mTexts != null && mTexts.length >0) {

            if (mCacheBounds == null || mCacheBounds.length != mTexts.length) {
                mCacheBounds = new Rect[mTexts.length];
            }

            if (mTextBounds == null || mTextBounds.length != mTexts.length) {
                mTextBounds = new Rect[mTexts.length];
            }

            for (int i = 0; i < mTexts.length; i++) {
                String text = mTexts[i];
                if (text != null) {
                    if (mTextBounds[i] == null) {
                        mTextBounds[i] = new Rect();
                    }
                    mPaint.getTextBounds(text, 0, text.length(), mTextBounds[i]);

                    if (singleTextWidth < mTextBounds[i].width()) {
                        singleTextWidth = mTextBounds[i].width();
                    }
                    if (singleTextHeight < mTextBounds[i].height()) {
                        singleTextHeight = mTextBounds[i].height();
                    }
                }
            }
            switch (widthMode) {
                case MeasureSpec.AT_MOST:
                    if (widthSize <= (singleTextWidth + mHorizonGap * 2) * mTexts.length) {
                        width = (singleTextWidth + mHorizonGap * 2) * mTexts.length;
                    } else {
                        width = widthSize;
                    }
                    break;
                case MeasureSpec.EXACTLY:
                    width = widthSize;
                    break;
                case MeasureSpec.UNSPECIFIED:
                    width = (singleTextWidth + mHorizonGap * 2) * mTexts.length;
                    break;
            }

            switch (heightMode) {
                case MeasureSpec.EXACTLY:
                    height = heightSize;
                    break;
                case MeasureSpec.AT_MOST:
                case MeasureSpec.UNSPECIFIED:
                    height = singleTextHeight + mVerticalGap * 2;
                    break;
            }

            mSingleChildWidth = width / mTexts.length;
            mSingleChildHeight = height;

            for (int i = 0; i < mTexts.length; i++) {
                if (mCacheBounds[i] == null) {
                    mCacheBounds[i] = new Rect();
                }

                mCacheBounds[i].left = i * mSingleChildWidth;
                mCacheBounds[i].top = 0;
                mCacheBounds[i].right =  mCacheBounds[i].left + mSingleChildWidth;
                mCacheBounds[i].bottom = mSingleChildHeight;
            }
        }else{
            width = widthMode == MeasureSpec.UNSPECIFIED ? 0 : widthSize;
            height = heightMode == MeasureSpec.UNSPECIFIED ? 0 : heightSize;
        }

        setMeasuredDimension(width, height);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                inTapRegion = true;

                mStartX = event.getX();
                mStartY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:

                int dx = (int) (event.getX() - mStartX);
                int dy = (int) (event.getY() - mStartY);

                int distance = dx * dx + dy * dy;
                if(distance > mTouchSlop){
                    inTapRegion = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if(inTapRegion){
                    int index = (int) (mStartX / mSingleChildWidth);
                    if(mCurrentIndex != index) {
                        mCurrentIndex = index;
                        if (mOnSegmentItemClickListener != null) {
                            mOnSegmentItemClickListener.onSegmentIitemClick(index);
                        }
                    }
                    invalidate();
                }
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Rect rectFirst = mCacheBounds[0];
        Rect rectLast = mCacheBounds[mTexts.length - 1];

        canvas.drawLine(rectFirst.left, rectFirst.top, rectLast.right, rectLast.top, mLinePaint);
        canvas.drawLine(rectFirst.left, rectFirst.bottom - mLineBorder, rectLast.right, rectLast.bottom - mLineBorder, mLinePaint);
        canvas.drawLine(rectFirst.left, rectFirst.top, rectFirst.left, rectFirst.bottom, mLinePaint);

        for (int x = 0; x < mCacheBounds.length; x++){
            Rect rect = mCacheBounds[x];
            if(rect != null) {
                if (x != mCacheBounds.length - 1) {
                    canvas.drawLine(rect.right, rect.top, rect.right, rect.bottom, mLinePaint);
                }else{
                    canvas.drawLine(rect.right, rect.top, rect.right, rect.bottom, mLinePaint);
                }
                if (x == mCurrentIndex) {
                    mPaint.setColor(mColors);
                    canvas.drawLine(rect.left, rect.bottom - 1, rect.right, rect.bottom - 1, mPaint);
                }else{
                    mPaint.setColor(mBorderColor);
                }
                canvas.drawText(mTexts[x], rect.left + (mSingleChildWidth - mTextBounds[x].width()) / 2, rect.top + ((mSingleChildHeight + mTextBounds[x].height()) / 2), mPaint);
            }
        }
    }
}
