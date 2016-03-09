package com.botpy.demo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Scroller;

import com.botpy.demo.R;

/**
 * 可滑动的布局
 *
 * @author weiTeng on 2016/2/26.
 */
public class SlideLayout extends ViewGroup {

    private static final String TAG = "SlideLayout";

    private static final int DISPLAY_MENU = 1;
    private static final int DISPLAY_CONTENT = 2;

    private int mCurrentState = DISPLAY_CONTENT;

    private int mSlideWidth;

    private View mSlideLayout;
    private View mContentLayout;

    private int mStartX;
    private int mMovedX;

    private int mInterceptX;
    private int mInterceptY;
    private int mTouchSlop;

    private Scroller mScroller;

    private OnSlideChangedListener mOnSlideChangedListener;

    public SlideLayout(Context context) {
        this(context, null);
    }

    public SlideLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SlideLayout);
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        int defaultWidth = (int) (metrics.widthPixels * 0.7);

        mSlideWidth = ta.getDimensionPixelSize(R.styleable.SlideLayout_slide_width,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, defaultWidth, context.getResources().getDisplayMetrics()));
        ta.recycle();
        mScroller = new Scroller(context, new AccelerateDecelerateInterpolator());

        int touchSlop;
        if(context == null){
            touchSlop = ViewConfiguration.getTouchSlop();
        } else {
            final ViewConfiguration config = ViewConfiguration.get(context);
            touchSlop = config.getScaledTouchSlop();
        }
        mTouchSlop = touchSlop;
    }

    public void setOnSlideChangedListener(OnSlideChangedListener onSlideChangedListener) {
        mOnSlideChangedListener = onSlideChangedListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int slideWidthMeasureSpec = MeasureSpec.makeMeasureSpec(mSlideWidth, MeasureSpec.EXACTLY);
        mSlideLayout = getChildAt(0);
        mContentLayout = getChildAt(1);
        measureChild(mSlideLayout, slideWidthMeasureSpec, heightMeasureSpec);
        measureChild(mContentLayout, widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mSlideLayout.layout(-mSlideWidth, t, 0, b);
        mContentLayout.layout(0, 0, r, b);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mInterceptX = (int) ev.getX();
                mInterceptY = (int) ev.getY();
                mStartX = mInterceptX;
                mMovedX = mInterceptX;
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) (ev.getX() - mInterceptX);
                int moveY = (int) (ev.getY() - mInterceptY);

                if(Math.abs(moveX) > mTouchSlop){
                    return mCurrentState == DISPLAY_MENU;
                } else if (Math.abs(moveY) > mTouchSlop){
                    return false;
                }
                break;
        }
        return false;
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
                Log.d(TAG, "mStartX = " + mStartX);
                Log.d(TAG, "moveX = " + move);
                Log.d(TAG, "dx = " + dx);
                Log.d(TAG, "mScrollX = " + getScrollX());

                if(getScrollX() + dx < -mSlideWidth){
                    scrollTo(-mSlideWidth, 0);
                } else if(getScrollX() + dx > 0){
                    scrollTo(0, 0);
                } else {
                    scrollBy(dx, 0);
                }
                mMovedX = move;
                break;
            case MotionEvent.ACTION_UP:
                int distance = (int) (event.getX() - mStartX);
                if(distance > (mSlideWidth / 2)){
                    mCurrentState = DISPLAY_MENU;
                }
                else if(distance < -(mSlideWidth / 2)){
                    mCurrentState = DISPLAY_CONTENT;
                }
                scrollToLocation();
                break;
        }
        return true;
    }

    private void scrollToLocation(){
        int dx = 0;
        int start = getScrollX();
        if(mCurrentState == DISPLAY_MENU){
            dx = -mSlideWidth - start;
        } else if(mCurrentState == DISPLAY_CONTENT){
            dx = 0 - start;
        }

        if(!mScroller.isFinished()) {
            mScroller.forceFinished(true);
        }
        mScroller.startScroll(start, 0, dx, 0, 280);
        invalidate();
    }

    @Override
    public void computeScroll(){
        if(mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
            invalidate();
        }
    }

    public boolean isClosed(){
        return mCurrentState == DISPLAY_CONTENT;
    }

    public void openMenu(){
        mCurrentState = DISPLAY_MENU;
        scrollToLocation();
    }

    public void closeMenu(){
        mCurrentState = DISPLAY_CONTENT;
        scrollToLocation();
    }

    public interface OnSlideChangedListener{
        void opened();
        void closed();
    }
}
