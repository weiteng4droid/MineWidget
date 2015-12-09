package com.botpy.demo.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.botpy.demo.R;
import com.botpy.demo.base.BaseFragment;
import com.botpy.demo.widget.ClearEditText;

import butterknife.InjectView;

/**
 * Created by weiTeng on 2015/12/7.
 */
public class SecondFragment extends BaseFragment {

    private static final String TAG = "SecondFragment";

    @InjectView(R.id.clear_et)
    ClearEditText mClearEditText;

    @Override
    protected int getLayoutId() {
        return R.layout.layout_fragment_second;
    }

    @Override
    public void initView(View view) {

    }

    @Override
    public void initData(Bundle saveInstaceState) {

    }

    @Override
    public void reloadData() {
        super.reloadData();
        Log.d(TAG, " SecondFragment : reloadData()");
    }
}
