package com.botpy.demo.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.botpy.demo.R;
import com.botpy.demo.base.BaseFragment;
import com.botpy.demo.widget.ItemTableView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by weiTeng on 2015/12/7.
 */
public class FirstFragment extends BaseFragment {

    private static final String TAG = "FirstFragment";

    @InjectView(R.id.item_table_view)
    ItemTableView mItemTableView;

    @Override
    protected int getLayoutId() {
        return R.layout.layout_fragment_first;
    }

    @Override
    public void initView(View view) {

        mItemTableView.setCurrentIndex(0);
        mItemTableView.setOnSegmentItemClickListener(new ItemTableView.OnSegmentItemClickListener() {
            @Override
            public void onSegmentIitemClick(int index) {
                Log.d(TAG, " FirstFragment : index = " + index);
            }
        });
    }

    @Override
    public void initData(Bundle saveInstaceState) {

    }

    @Override
    public void reloadData() {
        super.reloadData();
        Log.d(TAG, " FirstFragment : reloadData()");
    }
}
