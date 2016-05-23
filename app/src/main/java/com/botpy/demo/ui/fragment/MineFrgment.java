package com.botpy.demo.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.botpy.demo.R;
import com.botpy.demo.base.BaseFragment;
import com.botpy.demo.ui.model.Row;
import com.botpy.demo.widget.SheetView;

import java.util.ArrayList;
import java.util.List;

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
        SheetView sheetView = (SheetView) view.findViewById(R.id.sheet_view);
        sheetView.setRows(createRowData());
    }

    private List<Row> createRowData() {
        List<Row> rows = new ArrayList<>();
        Row row1 = new Row();
        row1.cells.add("未出险时长");
        row1.cells.add("保费折扣");
        rows.add(row1);

        Row row2 = new Row();
        row2.cells.add("连续一年未出险");
        row2.cells.add("0.685");
        rows.add(row2);

        Row row3 = new Row();
        row3.cells.add("连续二年未出险");
        row3.cells.add("0.567");
        rows.add(row3);

        Row row4 = new Row();
        row4.cells.add("连续三年未出险");
        row4.cells.add("0.486");
        rows.add(row4);

        Row row5 = new Row();
        row5.cells.add("连续四年未出险");
        row5.cells.add("0.405");
        rows.add(row5);

        Row row6 = new Row();
        row6.cells.add("连续五年未出险");
        row6.cells.add("0.324");
        rows.add(row6);

        return rows;
    }

    @Override
    public void initData(Bundle saveInstanceState) {
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
