package com.botpy.demo.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.botpy.demo.R;
import com.botpy.demo.base.BaseFragment;

/**
 * Created by weiTeng on 2015/12/7.
 */
public class ThirdFragment extends BaseFragment {

    private static final String TAG = "ThirdFragment";

    @Override
    protected int getLayoutId() {
        return R.layout.layout_fragment_third;
    }

    @Override
    public void initView(View view) {
        /*
        RelativeLayout container = (RelativeLayout) view.findViewById(R.id.container);

        View mDeleteButton = LayoutInflater.from(mActivity).inflate(R.layout.delete_button, null);

        Log.d(TAG, container.toString());
        Log.d(TAG, "child count = " + container.getChildCount());

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        if (mDeleteButton != null) {
            container.addView(mDeleteButton, params);
        }
        Log.d(TAG, "child count = " + container.getChildCount());
        */
    }

    @Override
    public void initData(Bundle saveInstaceState) {

    }

    @Override
    public void reloadData() {
        super.reloadData();
        Log.d(TAG, " ThirdFragment : reloadData()");
    }
}
