package com.botpy.demo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.botpy.demo.R;

/**
 * Created by weiTeng on 2015/12/29.
 */
public class StarImageView extends ImageView {

    private static final String TAG = "StarImageView";

    private Drawable mNormalDrawable;
    private Drawable mSelectedDrawable;

    private boolean mLinked;

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
        mNormalDrawable = ta.getDrawable(R.styleable.StarImageView_icon_normal);
        mSelectedDrawable = ta.getDrawable(R.styleable.StarImageView_icon_selected);
        mLinked = ta.getBoolean(R.styleable.StarImageView_icon_lined, false);
        ta.recycle();

        initStarView();
    }

    private void initStarView() {
        if(mSelectedDrawable == null || mNormalDrawable == null){
            throw new IllegalArgumentException("未设置状态图片");
        }
        switchState(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mLinked = !mLinked;
        switchState(true);
        return super.onTouchEvent(event);
    }

    public void setCheckState(boolean checked){
        setCheckState(checked, false);
    }

    public void setCheckState(boolean checked, boolean tiger){
        if(mLinked != checked){
            mLinked = checked;
            switchState(tiger);
        }
    }

    private void switchState(boolean tiger){
        if(mLinked){
            setImageDrawable(mSelectedDrawable);
            if(tiger && mOnLikeListener != null) mOnLikeListener.liked();
        }else{
            setImageDrawable(mNormalDrawable);
            if(tiger && mOnLikeListener != null) mOnLikeListener.unLiked();
        }
    }
}
