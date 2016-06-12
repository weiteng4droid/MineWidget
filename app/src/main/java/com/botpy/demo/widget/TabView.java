package com.botpy.demo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.botpy.demo.R;


/**
 * 底部导航view
 * @author weiTeng
 * @since 2015-12-4 17:29:09
 * @version v1.0.0
 */
public class TabView extends View {

    private static final String TAG = "TabView";
    private static final boolean DEBUG = true;

    private Rect[] mCacheBounds;
    private Rect[] mTextsBounds;
    private Rect[] mDrawableBounds;
    private Rect[] mTouchBounds;

    private String[] mTexts;
    private StateListDrawable[] mStateListDrawables;

    // Gap between image and text
    private int mVerticalGap;
    private int mTextSize;
    private int mSingleWidth;

    private int mBorderWidth;
    private int mBorderColor;
    private int mSelectColor;
    private int mNoticeColor = 0xffff0000;

    private int mPointRadius = 10;
    private int mDrawableWidth = 56;
    private Paint mBorderPaint;
    private Paint mPaint;
    private Paint mNoticePaint;

    private int mTouchSlop;

    private boolean mShowNotice;        // is show notice point
    private boolean mTouchClear;
    private boolean inTapRegion;
    private boolean[] mPosNotices;

    private int mStartX;
    private int mStartY;
    private int mCurrentIndex;

    private OnTabClickListener mOnTabClickListener;

    public interface OnTabClickListener {
        void onTabClick(int index);
    }

    public TabView(Context context) {
        this(context, null);
    }

    public TabView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TabView);
        String texts = ta.getString(R.styleable.TabView_tab_texts);
        if (texts != null) {
            mTexts = texts.split("\\|");
        }
        mTextSize = ta.getDimensionPixelSize(R.styleable.TabView_tab_textSize,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, context.getResources().getDisplayMetrics()));
        mVerticalGap = ta.getDimensionPixelSize(R.styleable.TabView_tab_verticalGap,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, context.getResources().getDisplayMetrics()));
        mBorderWidth = ta.getDimensionPixelSize(R.styleable.TabView_tab_borderWidth,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, context.getResources().getDisplayMetrics()));
        mBorderColor = ta.getColor(R.styleable.TabView_tab_borderColor, getResources().getColor(android.R.color.darker_gray));
        mSelectColor = ta.getColor(R.styleable.TabView_tab_textSelectColor, 0xff0099cc);
        mShowNotice = ta.getBoolean(R.styleable.TabView_tab_showNotice, false);

        ta.recycle();

        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mSelectColor);
        mPaint.setTextSize(mTextSize);

        if(mShowNotice){
            mNoticePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mNoticePaint.setColor(mNoticeColor);
            mPosNotices = new boolean[mTexts.length];
        }

        // bounds of drawables
        if(mDrawableBounds == null || mDrawableBounds.length != mTexts.length){
            mDrawableBounds = new Rect[mTexts.length];
        }

        // ensure touch bounds
        if(mTouchBounds == null || mTouchBounds.length != mTexts.length){
            mTouchBounds = new Rect[mTexts.length];
        }

        // get the real touchSlop
        int touchSlop;
        if(context == null){
            touchSlop = ViewConfiguration.getTouchSlop();
        }else{
            final ViewConfiguration config = ViewConfiguration.get(context);
            touchSlop = config.getScaledTouchSlop();
        }

        mTouchSlop = touchSlop * touchSlop;
        inTapRegion = false;
    }

    public void setCurrentIndex(int index){
        setCurrentIndex(index, false);
        invalidate();
    }

    public void setCurrentIndex(int index, boolean tiger){
        mCurrentIndex = index;
        if(mOnTabClickListener != null && tiger){
            mOnTabClickListener.onTabClick(index);
        }
    }

    public void setNoticePointRadius(int pointRadius){
        if (mPointRadius != pointRadius) {
            mPointRadius = pointRadius;
            requestLayout();
            invalidate();
        }
    }

    public void setDrawableWidth(int drawableWidth){
        if(mDrawableWidth != drawableWidth){
            mDrawableWidth = drawableWidth;
            requestLayout();
            invalidate();
        }
    }

    public boolean isShowNotice() {
        return mShowNotice;
    }

    public void showNoticePoint(boolean showNotice) {
        if(mShowNotice != showNotice) {
            mShowNotice = showNotice;
            invalidate();
        }
    }

    public void showNoticePointAtPosition(int position, boolean toggle){
        if(!mShowNotice){
            return;
        }
        if(position >= mTexts.length){
            throw new IndexOutOfBoundsException("超出标签的长度");
        }
        mPosNotices[position] = toggle;
        invalidate();
    }

    public void clearNoticePointOnTouch(boolean tiger){
        mTouchClear = tiger;
    }

    public void setStateListDrawables(int... resDrawable){
        if(resDrawable.length != mTexts.length){
            throw new IllegalArgumentException("图片个数和文本组数不符");
        }

        if (mStateListDrawables == null) {
            mStateListDrawables = new StateListDrawable[mTexts.length];
        }

        for (int i = 0; i < resDrawable.length; i++) {
            mStateListDrawables[i] = (StateListDrawable) getResources().getDrawable(resDrawable[i]);
        }
    }

    public void setTextSize(int textSize){
        setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }

    private void setTextSize(int unit, int textSize){
        mPaint.setTextSize(TypedValue.applyDimension(unit, textSize, getResources().getDisplayMetrics()));

        if(mTextSize != textSize){
            mTextSize = textSize;
            requestLayout();
            invalidate();
        }
    }

    public void setBorderColor(int borderColor){
        if(mBorderColor != borderColor){
            mBorderColor = borderColor;
            invalidate();
        }
    }

    public void setSelectColor(int selectColor){
        if(mSelectColor != selectColor){
            mSelectColor = selectColor;
            invalidate();
        }
    }

    public void setBorderWidth(int borderWidth){
        mBorderPaint.setStrokeWidth(mBorderWidth);
        if(mBorderWidth != borderWidth){
            mBorderWidth = borderWidth;
            requestLayout();
            invalidate();
        }
    }

    private void getDrawableBounds(Rect borderRect, Rect drawableRect, Drawable drawable){

        int horizonGap = (int) ((mSingleWidth - mDrawableWidth) / 2.0f + 0.5);
        drawableRect.left = borderRect.left + horizonGap;
        drawableRect.top = borderRect.top + getPaddingTop();
        drawableRect.right = drawableRect.left + mDrawableWidth;
        drawableRect.bottom = drawableRect.top + mDrawableWidth;

        drawable.setBounds(drawableRect);
    }

    public void setOnTabClickListener(OnTabClickListener onTabClickListener) {
        mOnTabClickListener = onTabClickListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if(mCacheBounds == null || mCacheBounds.length != mTexts.length){
            mCacheBounds = new Rect[mTexts.length];
        }
        if(mTextsBounds == null || mTextsBounds.length != mTexts.length){
            mTextsBounds = new Rect[mTexts.length];
        }
        calculateTextBound();

        int width;
        int height;
        int realWidth = widthSize - getPaddingLeft() - getPaddingRight();
        int realHeight = heightSize - getPaddingTop() - getPaddingBottom();

        // def temp width and height
        int tempSingleWidth = 0;
        int tempSingleHeight = 0;

        if(mStateListDrawables != null && mStateListDrawables.length == mTexts.length) {
            tempSingleWidth = Math.max(mDrawableWidth, mTextsBounds[0].width());
            tempSingleHeight = mDrawableWidth + mVerticalGap + mTextsBounds[0].height();
        }

        // measure width
        if (widthMode == MeasureSpec.EXACTLY) {
            width = realWidth;
            tempSingleWidth = realWidth / mTexts.length;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            if (realWidth <= tempSingleWidth * mTexts.length) {
                width = tempSingleWidth * mTexts.length;
            } else {
                width = realWidth;
                tempSingleWidth = realWidth / mTexts.length;
            }
        } else {
            width = Math.min(tempSingleWidth * mTexts.length, realWidth);
        }

        // measure height
        if (heightMode == MeasureSpec.EXACTLY) {
            tempSingleHeight = height = realHeight;

        } else if (heightMode == MeasureSpec.AT_MOST) {
            if (tempSingleHeight < realHeight) {
                height = tempSingleHeight;
            } else {
                tempSingleHeight = height = realHeight;
            }
        } else {
            height = Math.min(tempSingleHeight, realHeight);
        }

        mSingleWidth = tempSingleWidth;

        if (DEBUG) {
            Log.d(TAG, "height = " + height);
            Log.d(TAG, "width = " + width);
        }
        calculateBounds(tempSingleHeight);
        setMeasuredDimension(width + getPaddingRight() + getPaddingLeft(), height + getPaddingTop() + getPaddingBottom());
    }

    /**
     * 计算图标的位置和单个item的位置
     */
    private void calculateBounds(int tempSingleHeight) {
        for(int i = 0; i < mCacheBounds.length; i++){
            if(mCacheBounds[i] == null){
                mCacheBounds[i] = new Rect();
            }
            if(mDrawableBounds[i] == null){
                mDrawableBounds[i] = new Rect();
            }

            Rect rect = mCacheBounds[i];
            rect.left = i * mSingleWidth;
            rect.top = 0;
            rect.right = rect.left + mSingleWidth;
            rect.bottom = getPaddingTop() + tempSingleHeight + getPaddingBottom();

            getDrawableBounds(rect, mDrawableBounds[i], mStateListDrawables[i]);

            if(mTouchBounds[i] == null){
                mTouchBounds[i] = new Rect();
            }

            int touchGap = (mSingleWidth - mDrawableBounds[i].width()) / 3;
            if (touchGap < mVerticalGap) {
                touchGap = mVerticalGap;
            }
            mTouchBounds[i].left = mDrawableBounds[i].left - touchGap;          // Adjust the range of the area to be clicked
            mTouchBounds[i].top = rect.top + mVerticalGap;
            mTouchBounds[i].right = mDrawableBounds[i].right + touchGap;
            mTouchBounds[i].bottom = mDrawableBounds[i].bottom + mVerticalGap + mTextsBounds[i].height() + getPaddingBottom();
        }
    }

    /**
     * 计算文字的位置
     */
    private void calculateTextBound() {
        for(int i = 0; i < mTexts.length; i++){
            String itemText = mTexts[i];
            if(mTextsBounds[i] == null ){
                mTextsBounds[i] = new Rect();
            }
            mPaint.getTextBounds(itemText, 0, itemText.length(), mTextsBounds[i]);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:

                inTapRegion = true;

                mStartX = (int) event.getX();
                mStartY = (int) event.getY();
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
                    int index = mCurrentIndex;
                    for (int i = 0; i < mTouchBounds.length; i++) {
                        Rect touchRect = mTouchBounds[i];

                        // If you need more accurate when you need to add the Y direction of the judgment, this View only to determine the level of direction
                        if(mStartX > touchRect.left && mStartX < touchRect.right){
                            index = i;
                            break;
                        }
                    }
                    if(mCurrentIndex != index) {
                        mCurrentIndex = index;
                        if (mOnTabClickListener != null) {
                            mOnTabClickListener.onTabClick(index);
                        }
                        if(mTouchClear){
                            mPosNotices[index] = false;
                        }
                        invalidate();
                    }
                }
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Rect rectFirst = mCacheBounds[0];
        Rect rectLast = mCacheBounds[mTexts.length - 1];
        canvas.drawLine(rectFirst.left, rectFirst.top, rectLast.right, rectLast.top, mBorderPaint);                  // draw top line
        canvas.drawLine(rectFirst.left, rectFirst.bottom, rectLast.right, rectLast.bottom, mBorderPaint);            // draw bottom line

        // draw drawable of item
        StateListDrawable drawable;
        for(int i = 0; i < mTexts.length; i++) {
            drawable = mStateListDrawables[i];
            if (i == mCurrentIndex) {
                drawable.setState(new int[]{android.R.attr.state_checked});
                mPaint.setColor(mSelectColor);
            } else {
                drawable.setState(new int[]{-android.R.attr.state_checked});
                mPaint.setColor(mBorderColor);
            }
            drawable.draw(canvas);

            // draw text under the drawable
            float textOffset = (mCacheBounds[i].height() - getPaddingBottom() - mDrawableBounds[i].height() - mVerticalGap) /2 - (mPaint.descent() + mPaint.ascent()) / 2;
            canvas.drawText(mTexts[i], mCacheBounds[i].left + (mSingleWidth - mTextsBounds[i].width()) / 2, mDrawableBounds[i].bottom + mVerticalGap + textOffset, mPaint);

            // daw red notice point
            if(mShowNotice && mPosNotices[i]) {
                canvas.drawCircle(mDrawableBounds[i].right, mDrawableBounds[i].top + mPointRadius, mPointRadius, mNoticePaint);
            }
        }
    }
}