package com.botpy.demo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.botpy.demo.R;

/**
 * Created by weiTeng on 2015/12/29.
 */
public class StarImageView extends ImageView {

    private static final String TAG = "StarImageView";

    private StateListDrawable mDrawable;

    private boolean mLiked;

    public interface OnLikeListener {
        void liked();
        void unLiked();
    }

    private OnLikeListener mOnLikeListener;

    public void setOnLikeListener(OnLikeListener onLikeListener) {
        mOnLikeListener = onLikeListener;
    }

    public StarImageView(Context context) {
        this(context, null);
    }

    public StarImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StarImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.StarImageView);
        mDrawable = (StateListDrawable) ta.getDrawable(R.styleable.StarImageView_state_drawables);
        mLiked = ta.getBoolean(R.styleable.StarImageView_liked, false);
        ta.recycle();
        initStarView();
    }

    private void initStarView() {
        if(mDrawable == null){
            throw new IllegalArgumentException("未设置状态图片选择器");
        }
        setImageDrawable(mDrawable);
        switchState(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mLiked = !mLiked;
        switchState(true);
        return super.onTouchEvent(event);
    }

    public void setCheckState(boolean checked){
        setCheckState(checked, false);
    }

    public void setCheckState(boolean checked, boolean tiger){
        if(mLiked != checked){
            mLiked = checked;
            switchState(tiger);
        }
    }

    private void switchState(boolean tiger){
        if(mLiked){
            mDrawable.setState(new int[]{android.R.attr.state_checked});
            if(tiger && mOnLikeListener != null) mOnLikeListener.liked();
        }else{
            mDrawable.setState(new int[]{-android.R.attr.state_checked});
            if(tiger && mOnLikeListener != null) mOnLikeListener.unLiked();
        }
    }
}
