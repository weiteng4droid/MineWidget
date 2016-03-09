package com.botpy.demo.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.botpy.demo.R;
import com.botpy.demo.adapter.MyAdapter;
import com.botpy.demo.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

/**
 * @author weiTeng on 2015/12/7.
 */
public class SecondFragment extends BaseFragment {

    private static final String TAG = "SecondFragment";

    @InjectView(R.id.my_list_view)
    ListView myListView;

    private MyAdapter adapter;

    private List<String> contentList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.layout_fragment_second;
    }

    @Override
    public void initView(View view) {
        initList();

        adapter = new MyAdapter(mActivity, 0, contentList);
        myListView.setAdapter(adapter);
        adapter.setOnDeleteItemListener(new MyAdapter.OnDeleteItemListener() {

            @Override
            public void onDeleteItem(String name) {

                contentList.remove(name);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void initData(Bundle saveInstaceState) {

    }

    private void initList() {
        contentList.add("Content Item 1");
        contentList.add("Content Item 2");
        contentList.add("Content Item 3");
        contentList.add("Content Item 4");
        contentList.add("Content Item 5");
        contentList.add("Content Item 6");
        contentList.add("Content Item 7");
        contentList.add("Content Item 8");
        contentList.add("Content Item 9");
        contentList.add("Content Item 10");
        contentList.add("Content Item 11");
        contentList.add("Content Item 12");
        contentList.add("Content Item 13");
        contentList.add("Content Item 14");
        contentList.add("Content Item 15");
        contentList.add("Content Item 16");
        contentList.add("Content Item 17");
        contentList.add("Content Item 18");
        contentList.add("Content Item 19");
        contentList.add("Content Item 20");
    }

    @Override
    public void reloadData() {
        super.reloadData();
        Log.d(TAG, " SecondFragment : reloadData()");
    }
}
