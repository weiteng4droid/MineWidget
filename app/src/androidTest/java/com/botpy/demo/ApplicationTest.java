package com.botpy.demo;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.test.LoaderTestCase;
import android.util.Log;

import com.botpy.demo.cache.CacheManager;
import com.botpy.demo.ui.model.BannerItem;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {

    private static final String TAG = "AndroidTest";

    public ApplicationTest() {
        super(Application.class);
    }

    public void testCacheModel() {
        BannerItem bannerItem = new BannerItem();
        bannerItem.title = "快乐";
        bannerItem.imgUrl = "http://e.hiphotos.baidu.com/image/pic/item/1b4c510fd9f9d72a5c832b00d12a2834359bbbc3.jpg";

        CacheManager.cacheModel(getContext(), "bannerItem", bannerItem);
    }


    public void getCacheModel() {
        BannerItem bannerItem = CacheManager.getModelForKey(getContext(), "bannerItem");
        if (bannerItem != null) {
            Log.d(TAG, bannerItem.toString());
        } else {
            Log.d(TAG, "null");
        }
    }
}