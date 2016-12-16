package com.botpy.demo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * 可以滑动删除的layout(用于滑动删除的ListView中)
 *
 * @author weiTeng on 2016/3/3.
 */
public class HorizontalSlideLayout extends ViewGroup {

    private static final String TAG = "HorizontalSlideLayout";

    private static final int CONTENT_MAIN = 1;
    private static final int CONTENT_HIDDEN = 2;

    private int mState = CONTENT_MAIN;

    private int mStartX;
    private int mMovedX;

    private View mContentLayout;
    private View mHiddenLayout;

    private int mHiddenWidth;

    private int mTouchSlop;
    private Scroller mScroller;

    public HorizontalSlideLayout(Context context) {
        this(context, null);
    }

    public HorizontalSlideLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalSlideLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mScroller = new Scroller(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();
        if(count < 2){
            throw new IllegalArgumentException("必须有两个子View");
        }
        mContentLayout = getChildAt(0);
        mHiddenLayout = getChildAt(1);
        MineLayoutParams layoutParams = (MineLayoutParams) mHiddenLayout.getLayoutParams();

        int hiddenMeasureSpec = MeasureSpec.makeMeasureSpec(layoutParams.width, MeasureSpec.EXACTLY);

        measureChild(mContentLayout, widthMeasureSpec, heightMeasureSpec);
        measureChild(mHiddenLayout, hiddenMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mHiddenWidth = mHiddenLayout.getMeasuredWidth();

        mContentLayout.layout(l, t, r, b);
        mHiddenLayout.layout(r, t, r + mHiddenWidth, b);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mStartX = (int) event.getX();
                mMovedX = mStartX;
                break;

            case MotionEvent.ACTION_MOVE:
                int move = (int) event.getX();
                int dx = mMovedX - move;

                Log.d(TAG, "getScrollX() = " + getScrollX());

                if (getScrollX() + dx < 0) {
                    scrollTo(0, 0);
                } else if (getScrollX() + dx > mHiddenWidth){
                    scrollTo(mHiddenWidth, 0);
                } else {
                    scrollBy(dx, 0);
                }
                mMovedX = move;
                break;

            case MotionEvent.ACTION_UP:
                int distance = (int) (mStartX - event.getX());

                Log.d(TAG, "distance = " + distance);

                if(Math.abs(distance) > mTouchSlop){
                    if (distance > mHiddenWidth / 2){
                        mState = CONTENT_HIDDEN;
                    } else if (distance < -mHiddenWidth / 2) {
                        mState = CONTENT_MAIN;
                    }
                    scrollPosition();
                }
                break;
        }
        return true;
    }

    private void scrollPosition() {
        int scrollX = getScrollX();
        int dx = 0;
        if (mState == CONTENT_HIDDEN) {
            dx = mHiddenWidth - scrollX;
        } else if (mState == CONTENT_MAIN) {
            dx = 0 - scrollX;
        }

        if(!mScroller.isFinished()){
            mScroller.forceFinished(true);
        }
        mScroller.startScroll(scrollX, 0, dx, 0, 260);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
            invalidate();
        }
    }

    @Override
    protected MineLayoutParams generateDefaultLayoutParams() {
        return new MineLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    protected MineLayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new MineLayoutParams(p);
    }

    @Override
    public MineLayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MineLayoutParams(getContext(), attrs);
    }

    /**
     * 布局参数
     */
    public class MineLayoutParams extends ViewGroup.LayoutParams{

        public MineLayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public MineLayoutParams(int width, int height) {
            super(width, height);
        }

        public MineLayoutParams(LayoutParams source) {
            super(source);
        }
    }
}
