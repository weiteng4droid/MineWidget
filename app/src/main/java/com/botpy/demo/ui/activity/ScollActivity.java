package com.botpy.demo.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.botpy.demo.R;
import com.botpy.demo.widget.MScrollView;

public class ScollActivity extends AppCompatActivity implements MScrollView.OnScrollChangeListener{

    private static final String TAG = "ScollActivity";

    private MScrollView mScrollView;
    private RelativeLayout mCenterBuyView;
    private RelativeLayout mTopBuyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoll);

        mScrollView = (MScrollView) findViewById(R.id.scrollView);
        mCenterBuyView = (RelativeLayout) findViewById(R.id.buy);
        mTopBuyView = (RelativeLayout) findViewById(R.id.top_buy_layout);

        mScrollView.setScrollChangeListener(this);

        findViewById(R.id.parent_layout).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                Log.d(TAG, "scrollY = " + mScrollView.getScrollY());
                onScroll(mScrollView.getScrollY());
            }
        });
    }


    @Override
    public void onScroll(int scrollY) {
        int mBuyLayoutTopToParent = Math.max(scrollY, mCenterBuyView.getTop());
        mTopBuyView.layout(0, mBuyLayoutTopToParent, mTopBuyView.getWidth(), mBuyLayoutTopToParent + mTopBuyView.getHeight());

        Log.d(TAG, "mCenterBuyView.getTop() = " + mCenterBuyView.getTop());
        Log.d(TAG, "top = " + mBuyLayoutTopToParent);
        Log.d(TAG, "right = " + mTopBuyView.getWidth());
        Log.d(TAG, "bottom = " + (mBuyLayoutTopToParent + mTopBuyView.getHeight()));
    }
}
