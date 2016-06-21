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
    private SheetView mSheetView;

    @Override
    public void initView(View view) {

        SheetView sheetView = (SheetView) view.findViewById(R.id.sheet_view);
        sheetView.setRows(createRowData());

        mSheetView = (SheetView) view.findViewById(R.id.sheet_view_2);
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

    private List<Row> createRowData2() {
        List<Row> rows = new ArrayList<>();
        Row row1 = new Row();
        row1.cells.add("次数");
        row1.cells.add("保险公司");
        row1.cells.add("开始日期");
        row1.cells.add("结束日期");
        row1.cells.add("赔偿金额");
        rows.add(row1);

        Row row2 = new Row();
        row2.cells.add("1");
        row2.cells.add("0.729324");
        row2.cells.add("0.81");
        row2.cells.add("0.729");
        row2.cells.add("0.81");
        rows.add(row2);

        Row row3 = new Row();
        row3.cells.add("3");
        row3.cells.add("0.802");
        row3.cells.add("0.891");
        row3.cells.add("0.802");
        row3.cells.add("0.891");
        rows.add(row3);

        Row row4 = new Row();
        row4.cells.add("4");
        row4.cells.add("0.875");
        row4.cells.add("0.972");
        row4.cells.add("0.875");
        row4.cells.add("0.972");
        rows.add(row4);

        Row row5 = new Row();
        row5.cells.add("5");
        row5.cells.add("1.0935");
        row5.cells.add("1.215");
        row5.cells.add("1.0935");
        row5.cells.add("1.215");
        rows.add(row5);
        return rows;
    }

    @Override
    public void initData(Bundle saveInstanceState) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSheetView.setRows(createRowData2());
                    }
                });
            }
        }).start();
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
