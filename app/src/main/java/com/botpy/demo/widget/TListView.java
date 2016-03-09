package com.botpy.demo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.botpy.demo.R;

/**
 * Created by weiTeng on 2016/2/18.
 */
public class TListView extends ListView implements GestureDetector.OnGestureListener, View.OnTouchListener{

    private static final String TAG = "TListView";

    private GestureDetector mGestureDetector;
    private OnDeleteListener mOnDeleteListener;

    private View mDeleteButton;
    private ViewGroup mItemLayout;

    private boolean isDeleteShow;
    private int selectItem;

    public interface OnDeleteListener{
        void onDelete(int index);
    }

    public TListView(Context context) {
        this(context, null);
    }

    public TListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVariable();
    }

    private void initVariable() {
        mGestureDetector = new GestureDetector(getContext(), this);
        setOnTouchListener(this);
    }

    public void setOnDeleteListener(OnDeleteListener onDeleteListener){
        this.mOnDeleteListener = onDeleteListener;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        if(!isDeleteShow){
            selectItem = pointToPosition((int)e.getX(), (int)e.getY());
        }
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if(!isDeleteShow && Math.abs(velocityX) > Math.abs(velocityY)){
            mDeleteButton = LayoutInflater.from(getContext()).inflate(R.layout.delete_button, null);
            mDeleteButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    mItemLayout.removeView(mDeleteButton);
                    mDeleteButton = null;
                    isDeleteShow = false;
                    mOnDeleteListener.onDelete(selectItem);
                }
            });

            mItemLayout = (ViewGroup) getChildAt(selectItem - getFirstVisiblePosition());

            Log.d(TAG, mItemLayout.toString());
            Log.d(TAG, "child count = " + mItemLayout.getChildCount());

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            if(mDeleteButton != null) {
                mItemLayout.addView(mDeleteButton, params);
                isDeleteShow = true;
            }
        }
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(isDeleteShow){
            mItemLayout.removeView(mDeleteButton);
            mDeleteButton = null;
            isDeleteShow = false;
            return false;
        }else{
            return mGestureDetector.onTouchEvent(event);
        }
    }
}
