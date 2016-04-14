package com.botpy.demo.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.botpy.demo.R;
import com.botpy.demo.base.BaseFragment;

/**
 * @author weiTeng on 2015/12/8.
 */
public class MineFrgment extends BaseFragment {

    private static final String TAG = "MineFrgment";
//    @InjectView(R.id.achievement)
//    AchievementView mAchievement;
//
//    @InjectView(R.id.icon_tv)
//    IconTextView mIconTextView;

    @Override
    public void initView(View view) {
/*
        ArrayList<ChartModel> models = new ArrayList<>();
        models.add(new ChartModel("100", Color.parseColor("#f76450")));
        models.add(new ChartModel("34", Color.parseColor("#f78750")));
        models.add(new ChartModel("65", Color.parseColor("#ffae22")));
        models.add(new ChartModel("51", Color.parseColor("#ffcc00")));
        mAchievement.setChartModels(models);

        mIconTextView.setOnIconTextClickListener(new IconTextView.OnIconTextClickListener() {
            @Override
            public void onIconTextClick(IconTextView iview) {
                Toast.makeText(mActivity, "点击控件", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(mActivity, HomeActivity.class));
            }
        });*/
    }

    @Override
    public void initData(Bundle saveInstaceState) {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_fragment_mine_1;
    }

    @Override
    public void reloadData() {
        super.reloadData();
        Log.d(TAG, " MineFrgment : reloadData()");
    }

}
