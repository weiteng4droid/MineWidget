package com.botpy.demo.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.botpy.demo.R;
import com.botpy.demo.base.BaseFragment;
import com.botpy.demo.dialog.TFragmentDialog;
import com.botpy.demo.ui.activity.ScollActivity;
import com.botpy.demo.ui.activity.TestViewGroupActivity;
import com.botpy.demo.widget.FrameTextView;
import com.botpy.demo.widget.GradientTextView;
import com.botpy.demo.widget.ItemTableView;
import com.botpy.demo.widget.StarImageView;

import butterknife.InjectView;
import butterknife.OnClick;

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

    @InjectView(R.id.frame_text_view)
    FrameTextView mFrameTextView;

    @InjectView(R.id.gradient_tv)
    GradientTextView mGradientTextView;

    @InjectView(R.id.show_fragment_dialog)
    Button mButton;

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

        mGradientTextView.setText("中华人共和国万岁");
        mGradientTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startNewActivity();
            }
        });
        mFrameTextView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, TestViewGroupActivity.class);
                startActivity(intent);
            }
        });
    }

    private void startNewActivity(){
        startActivity(new Intent(getActivity(), ScollActivity.class));
    }

    @Override
    public void initData(Bundle saveInstaceState) {

    }

    @Override
    public void reloadData() {
        super.reloadData();
        Log.d(TAG, " FirstFragment : reloadData()");
    }

    @OnClick(R.id.show_fragment_dialog)
    public void showDialog() {
        new TFragmentDialog()
                .show(mActivity.getFragmentManager(), "dialog");
    }
}
