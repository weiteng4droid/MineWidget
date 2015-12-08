package com.botpy.demo.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.botpy.demo.R;
import com.botpy.demo.base.BaseFragment;

/**
 * Created by weiTeng on 2015/12/8.
 */
public class MineFrgment extends BaseFragment {

    private static final String TAG = "MineFrgment";

    @Override
    public void initView(View view) {

    }

    @Override
    public void initData(Bundle saveInstaceState) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_fragment_mine;
    }

    @Override
    public void reloadData() {
        super.reloadData();
        Log.d(TAG, " MineFrgment : reloadData()");
    }
}
