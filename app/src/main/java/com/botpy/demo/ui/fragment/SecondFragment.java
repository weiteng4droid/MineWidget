package com.botpy.demo.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.botpy.demo.R;
import com.botpy.demo.adapter.MyAdapter;
import com.botpy.demo.base.BaseFragment;
import com.botpy.demo.ui.banner.SimpleImageBanner;
import com.botpy.demo.util.DataProvider;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

/**
 * @author weiTeng on 2015/12/7.
 */
public class SecondFragment extends BaseFragment {

    private static final String TAG = "SecondFragment";

    private SimpleImageBanner banner;

    @Override
    protected int getLayoutId() {
        return R.layout.layout_fragment_second;
    }

    @Override
    public void initView(View view) {
        banner = (SimpleImageBanner) view.findViewById(R.id.simple_banner);
        banner.setSource(DataProvider.getList())
                .setDelay(4)
                .setPeriod(4)
                .startScroll();

        banner.setOnItemClickL(new SimpleImageBanner.OnItemClickL() {

            @Override
            public void onItemClick(int position) {
                Toast.makeText(mActivity, "position = " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void initData(Bundle saveInstanceState) {

    }


    @Override
    public void reloadData() {
        super.reloadData();
        banner.setSource(DataProvider.reloadListData());
    }
}
