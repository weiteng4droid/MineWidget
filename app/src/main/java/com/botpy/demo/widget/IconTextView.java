package com.botpy.demo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.botpy.demo.R;

/**
 * 图标TextView
 *
 * @author weiTeng on 2016/1/5.
 */
public class IconTextView extends View {

    private static final String TAG = "IconTextView";

    private Rect mCacheBound;
    private Rect mIconBound;
    private Rect mTextBound;

    private int mTextSize;
    private int mTextColor;

    private String mText;

    private int mGap;
    private int mIconWidth;
    private int mIconHeight;

    private Drawable mDrawable;
    private Direction mDirection;

    private Paint mPaint;

    private int mTouchSlop;
    private boolean inTapRegion;
    private int mStartX, mStartY;

    enum Direction {
        TOP(0), BOTTOM(1), LEFT(2), RIGHT(3);

        int mValue;

        Direction(int value) {
            this.mValue = value;
        }
    }

    public interface OnIconTextClickListener {
        void onIconTextClick(IconTextView iview);
    }

    private OnIconTextClickListener mOnIconTextClickListener;

    public IconTextView(Context context) {
        this(context, null);
    }

    public IconTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.IconTextView);

        mTextSize = ta.getDimensionPixelSize(R.styleable.IconTextView_itv_textSize,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, context.getResources().getDisplayMetrics()));
        mTextColor = ta.getColor(R.styleable.IconTextView_itv_textColor, 0xff0099cc);
        mGap = ta.getDimensionPixelSize(R.styleable.IconTextView_itv_gap,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources().getDisplayMetrics()));
        mIconWidth = ta.getDimensionPixelSize(R.styleable.IconTextView_itv_iconWidth,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 50, context.getResources().getDisplayMetrics()));
        mIconHeight = ta.getDimensionPixelSize(R.styleable.IconTextView_itv_iconHeight,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 50, context.getResources().getDisplayMetrics()));

        mDrawable = ta.getDrawable(R.styleable.IconTextView_itv_icon);
        mDirection = Direction.values()[ta.getInt(R.styleable.IconTextView_itv_direction, 0)];
        mText = ta.getString(R.styleable.IconTextView_itv_text);

        ta.recycle();

        if (mTextBound == null) {
            mTextBound = new Rect();
        }
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mTextSize);
        mPaint.getTextBounds(mText, 0, mText.length(), mTextBound);

        // 计算出最小的滑动距离
        int touchSlop;
        if (context == null) {
            touchSlop = ViewConfiguration.getTouchSlop();
        } else {
            final ViewConfiguration config = ViewConfiguration.get(context);
            touchSlop = config.getScaledTouchSlop();
        }
        mTouchSlop = touchSlop * touchSlop;
        inTapRegion = false;

        // 修正给定图标的宽度高度值
        int drawableWidth = mDrawable.getIntrinsicWidth();
        int drawableHeight = mDrawable.getIntrinsicHeight();
        if(mIconWidth == 0 || drawableWidth < mIconWidth){
            mIconWidth = drawableWidth;
        }
        if(mIconHeight == 0 || drawableHeight < mIconHeight){
            mIconHeight = drawableHeight;
        }
    }

    public void setOnIconTextClickListener(OnIconTextClickListener onIconTextClickListener) {
        mOnIconTextClickListener = onIconTextClickListener;
    }

    public void setTextSize(int textSize) {
        if (mTextSize != textSize) {
            mTextSize = textSize;
            setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            requestLayout();
            invalidate();
        }
    }

    private void setTextSize(int unit, int size) {
        mPaint.setTextSize(TypedValue.applyDimension(unit, size, getResources().getDisplayMetrics()));
    }

    public void setTextColor(int textColor) {
        if (mTextColor != textColor) {
            mTextColor = textColor;
            invalidate();
        }
    }

    public void setText(String text) {
        if (!mText.equals(text)) {
            mText = text;
            requestLayout();
            invalidate();
        }
    }

    public void setGap(int gap) {
        if (mGap != gap) {
            mGap = gap;
            requestLayout();
            invalidate();
        }
    }

    public void setIconWidth(int iconWidth) {
        if (mIconWidth != iconWidth) {
            mIconWidth = iconWidth;
            requestLayout();
            invalidate();
        }
    }

    public void setIconHight(int iconHeight) {
        if (mIconHeight != iconHeight) {
            mIconWidth = iconHeight;
            requestLayout();
            invalidate();
        }
    }

    public void setDrawable(Drawable drawable) {
        if (mDrawable != drawable) {
            mDrawable = drawable;
            requestLayout();
            invalidate();
        }
    }

    public void setDirection(Direction direction) {
        if (mDirection != direction) {
            mDirection = direction;
            requestLayout();
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width, height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize - getPaddingLeft() - getPaddingRight();
        } else {
            width = measureAdjustSize(widthSize, widthMode);
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize - getPaddingTop() - getPaddingBottom();
        } else {
            height = measureAdjustHeight(heightSize, heightMode);
        }

        if (mCacheBound == null) {
            mCacheBound = new Rect();
        }
        mCacheBound.left = getPaddingLeft();
        mCacheBound.top = getPaddingTop();
        mCacheBound.right = getPaddingLeft() + width;
        mCacheBound.bottom = getPaddingTop() + height;

        composeDrawableBound();
        setMeasuredDimension(width + getPaddingLeft() + getPaddingRight(), height + getPaddingBottom() + getPaddingTop());
    }

    private int measureAdjustHeight(int heightSize, int heightMode) {
        int height;
        int needHeight;
        int realHeight = heightSize - getPaddingTop() - getPaddingBottom();

        if(mDirection == Direction.TOP || mDirection == Direction.BOTTOM){
            needHeight = mIconHeight + mGap + mTextBound.height();
        }else{
            needHeight = Math.max(mIconHeight, mTextBound.height());
        }

        height  = needHeight;

        if(heightMode == MeasureSpec.AT_MOST){
            height = Math.min(needHeight, realHeight);
        }
        return height;
    }

    private int measureAdjustSize(int widthSize, int widthMode) {
        int width;
        int needWidth;
        int realWidth = widthSize - getPaddingLeft() - getPaddingRight();

        if (mDirection == Direction.TOP || mDirection == Direction.BOTTOM) {
            needWidth = Math.max(mTextBound.width(), mIconWidth);
        } else {
            needWidth = mIconWidth + mTextBound.width() + mGap;
        }
        width = needWidth;

        if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(needWidth, realWidth);
        }
        return width;
    }

    /**
     * 确定Icon的位置
     */
    private void composeDrawableBound() {
        if (mCacheBound == null) {
            return;
        }

        int tempGap;
        if (mIconBound == null) {
            mIconBound = new Rect();
        }

        switch (mDirection) {
            case TOP:
                tempGap = (int) ((mCacheBound.width() - mIconWidth) / 2.0f + 0.5f);
                mIconBound.left = mCacheBound.left + tempGap;
                tempGap = (int) ((mCacheBound.height() - (mIconHeight + mGap + mTextBound.height())) / 2 + 0.5f);
                mIconBound.top = mCacheBound.top + tempGap;
                mIconBound.right = mIconBound.left + mIconWidth;
                mIconBound.bottom = mIconBound.top + mIconHeight;
                break;
            case LEFT:
                tempGap = (int) ((mCacheBound.width() - (mIconWidth + mGap + mTextBound.width())) / 2 + 0.5f);
                mIconBound.left = mCacheBound.left + tempGap;
                tempGap = (int) ((mCacheBound.height() - mIconHeight) / 2.0f + 0.5f);
                mIconBound.top = mCacheBound.top + tempGap;
                mIconBound.right = mIconBound.left + mIconWidth;
                mIconBound.bottom = mIconBound.top + mIconHeight;
                break;
            case RIGHT:
                tempGap = (int) ((mCacheBound.width() - (mIconWidth + mGap + mTextBound.width())) / 2 + 0.5f);
                mIconBound.left = mCacheBound.right - tempGap - mIconWidth;
                tempGap = (int) ((mCacheBound.height() - mIconHeight) / 2.0f + 0.5f);
                mIconBound.top = mCacheBound.top + tempGap;
                mIconBound.right = mCacheBound.right - tempGap;
                mIconBound.bottom = mIconBound.top + mIconHeight;
                break;
            case BOTTOM:
                tempGap = (int) ((mCacheBound.width() - mIconWidth) / 2.0f + 0.5f);
                mIconBound.left = mCacheBound.left + tempGap;
                tempGap = (int) ((mCacheBound.height() - (mIconHeight + mGap + mTextBound.height())) / 2 + 0.5f);
                mIconBound.top = mCacheBound.bottom - tempGap - mIconHeight;
                mIconBound.right = mIconBound.left + mIconWidth;
                mIconBound.bottom = mCacheBound.bottom - tempGap;
                break;
        }
        mDrawable.setBounds(mIconBound);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = (int) event.getX();
                mStartY = (int) event.getY();
                inTapRegion = true;
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) (event.getX() - mStartX);
                int dy = (int) (event.getY() - mStartY);

                int touchSlop = dx * dx + dy * dy;
                if (touchSlop > mTouchSlop) {
                    inTapRegion = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (inTapRegion) {
                    if ((mStartX > mCacheBound.left && mStartX < mCacheBound.right)
                            && (mStartY > mCacheBound.top && mStartY < mCacheBound.bottom)) {
                        if (mOnIconTextClickListener != null) {
                            mOnIconTextClickListener.onIconTextClick(this);
                        }
                    }
                }
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (mDirection) {
            case TOP:
                mDrawable.draw(canvas);
                canvas.drawText(mText, 0, mText.length(), mCacheBound.left + (mCacheBound.width() - mTextBound.width()) / 2,
                        mIconBound.bottom + mGap + mTextBound.height(), mPaint);
                break;
            case LEFT:
                mDrawable.draw(canvas);
                canvas.drawText(mText, 0, mText.length(), mIconBound.right + mGap,
                        mCacheBound.top + mTextBound.height() + (mCacheBound.height() - mTextBound.height()) / 2, mPaint);
                break;
            case RIGHT:
                mDrawable.draw(canvas);
                canvas.drawText(mText, 0, mText.length(), mCacheBound.left,
                        mCacheBound.top + mTextBound.height() + (mCacheBound.height() - mTextBound.height()) / 2, mPaint);
                break;
            case BOTTOM:
                mDrawable.draw(canvas);
                canvas.drawText(mText, 0, mText.length(),
                        mCacheBound.left + (mCacheBound.width() - mTextBound.width()) / 2, mIconBound.top - mGap, mPaint);  // 文字的起点是第一个文字的左下角位置开始
                break;
        }
    }
}
