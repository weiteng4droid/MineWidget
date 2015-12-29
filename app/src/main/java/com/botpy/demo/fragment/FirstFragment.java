package com.botpy.demo.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.botpy.demo.R;
import com.botpy.demo.base.BaseFragment;
import com.botpy.demo.widget.ItemTableView;
import com.botpy.demo.widget.StarImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by weiTeng on 2015/12/7.
 */
public class FirstFragment extends BaseFragment {

    private static final String TAG = "FirstFragment";

    @InjectView(R.id.item_table_view)
    ItemTableView mItemTableView;
    @InjectView(R.id.edittext)
    EditText mEditText;
    @InjectView(R.id.star_siv)
    StarImageView mStarImageView;

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
        mStarImageView.setOnLikeListener(new StarImageView.OnLikeListener() {

            @Override
            public void liked() {
                Toast.makeText(mActivity, "点赞", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void unLiked() {
                Toast.makeText(mActivity, "不点赞", Toast.LENGTH_SHORT).show();
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
