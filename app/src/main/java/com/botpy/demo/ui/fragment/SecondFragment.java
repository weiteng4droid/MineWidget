package com.botpy.demo.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.botpy.demo.R;
import com.botpy.demo.base.BaseFragment;
import com.botpy.demo.cache.CacheManager;
import com.botpy.demo.ui.banner.SimpleImageBanner;
import com.botpy.demo.ui.model.BannerItem;
import com.botpy.demo.util.DataProvider;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * @author weiTeng on 2015/12/7.
 */
public class SecondFragment extends BaseFragment {

    private static final String TAG = "SecondFragment";

    @InjectView(R.id.put_cache_btn)
    Button mCacheButton;

    @InjectView(R.id.get_cache_btn)
    Button mGetCacheButton;

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

    @OnClick(R.id.put_cache_btn)
    public void putCache() {
        BannerItem bannerItem = new BannerItem();
        bannerItem.title = "快乐";
        bannerItem.imgUrl = "http://e.hiphotos.baidu.com/image/pic/item/1b4c510fd9f9d72a5c832b00d12a2834359bbbc3.jpg";

        CacheManager.cacheModel(mActivity, "bannerItem", bannerItem);
    }

    @OnClick(R.id.get_cache_btn)
    public void getCache() {
        BannerItem bannerItem = CacheManager.getModelForKey(mActivity, "bannerItem");
        if (bannerItem != null) {
            Log.d(TAG, bannerItem.toString());
        } else {
            Log.d(TAG, "null");
        }
    }
}
