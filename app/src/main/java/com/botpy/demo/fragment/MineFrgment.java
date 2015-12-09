package com.botpy.demo.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.botpy.demo.R;
import com.botpy.demo.base.BaseFragment;
import com.botpy.demo.model.ChartModel;
import com.botpy.demo.widget.AchievementView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by weiTeng on 2015/12/8.
 */
public class MineFrgment extends BaseFragment {

    private static final String TAG = "MineFrgment";
    @InjectView(R.id.achievement)
    AchievementView mAchievement;


    @Override
    public void initView(View view) {

        ArrayList<ChartModel> models = new ArrayList<>();
        models.add(new ChartModel("100", "#f76450"));
        models.add(new ChartModel("34", "#f78750"));
        models.add(new ChartModel("65", "#ffae22"));
        models.add(new ChartModel("51", "#ffcc00"));
        mAchievement.setChartModels(models);
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
