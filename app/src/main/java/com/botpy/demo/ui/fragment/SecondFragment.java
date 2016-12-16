package com.botpy.demo.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.botpy.demo.R;
import com.botpy.demo.base.BaseFragment;
import com.botpy.demo.widget.QuickIndexView;

import butterknife.InjectView;

/**
 * @author weiTeng on 2015/12/7.
 */
public class SecondFragment extends BaseFragment {

    private static final String TAG = "SecondFragment";
    /*
    @InjectView(R.id.word_label)
    TextView mHintLabel;

    @InjectView(R.id.quick_index_view)
    QuickIndexView mIndexView;
    */
    /*
    @InjectView(R.id.put_cache_btn)
    Button mCacheButton;

    @InjectView(R.id.get_cache_btn)
    Button mGetCacheButton;

    @InjectView(R.id.tag_image_view)
    ImageView mTagImageView;
    */

    @Override
    protected int getLayoutId() {
        return R.layout.layout_fragment_second;
    }

    @Override
    public void initView(View view) {
        /*
        mIndexView.setShowLabel(mHintLabel);
        mIndexView.setOnQuickIndexListener(new QuickIndexView.OnQuickIndexListener() {

            @Override
            public void onQuickIndex(int index, String word) {
                Log.d(TAG, "index = " + index + ", word = " + word);
            }
        });
        */
    }

    @Override
    public void initData(Bundle saveInstanceState) {

    }


    @Override
    public void reloadData() {
        super.reloadData();
    }

    /*
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
            Toast.makeText(mActivity, bannerItem.title, Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "null");
        }
    }
    */
}
