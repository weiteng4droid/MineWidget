package com.weiteng.image.app;

import android.app.Application;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.weiteng.image.R;

/**
 * Created by weiTeng on 2016/12/16.
 */
public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initImageLoader();
    }

    public void initImageLoader() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.photo_default_icon)
                .showImageOnLoading(R.drawable.photo_default_icon)
                .showImageOnFail(R.drawable.photo_default_icon)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .memoryCacheExtraOptions(480, 800)
                .threadPoolSize(3) 								// 默认线程池3个
                .threadPriority(Thread.NORM_PRIORITY - 1) 		// default
                .tasksProcessingOrder(QueueProcessingType.FIFO) // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(5 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13) 					// default
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) 	// 设置存储文件名称的时候使用hashCode
                .imageDownloader(new BaseImageDownloader(this))		            // 设置基础类下载框架
                .defaultDisplayImageOptions(options)                            // 设置显示的配置
                .build();
        ImageLoader.getInstance().init(config);
    }
}
