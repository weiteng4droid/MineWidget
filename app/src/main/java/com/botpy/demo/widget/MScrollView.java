package com.botpy.demo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ScrollView;

/**
 * Created by weiTeng on 2016/2/25.
 */
public class MScrollView extends ScrollView {

    private static final String TAG  = "MScrollView";

    private OnScrollChangeListener mScrollChangeListener;

    public MScrollView(Context context) {
        this(context, null);
    }

    public MScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public interface OnScrollChangeListener{
        void onScroll(int scroll);
    }

    public OnScrollChangeListener getScrollChangeListener() {
        return mScrollChangeListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightModel = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        Log.d(TAG, "heightModel = " + heightModel);
        Log.d(TAG, "heightSize = " + heightSize);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setScrollChangeListener(OnScrollChangeListener scrollChangeListener) {
        mScrollChangeListener = scrollChangeListener;
    }

    @Override
    protected int computeVerticalScrollRange() {
        return super.computeVerticalScrollRange();
    }

    @Override
    protected void onScrollChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(mScrollChangeListener != null){
            mScrollChangeListener.onScroll(h);
        }
    }
}
