package com.weiteng.cache;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.weiteng.cache.model.BannerItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Bind(R.id.put_cache_btn)
    Button mPutCacheBtn;
    @Bind(R.id.get_cache_btn)
    Button mGetCacheBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.put_cache_btn)
    public void putCache() {
        List<BannerItem> bannerList = new ArrayList<>();
        BannerItem bannerItem1 = new BannerItem();
        bannerItem1.name = "五一快乐";
        bannerItem1.amount = 234234.2324;
        bannerItem1.descript = "劳动节是我们传统节日，也是劳动人民的节日";
        bannerItem1.url = "http://e.hiphotos.baidu.com/image/pic/item/1b4c510fd9f9d72a5c832b00d12a2834359bbbc3.jpg";

        BannerItem bannerItem2 = new BannerItem();
        bannerItem2.name = "儿童节";
        bannerItem2.amount = 2364.2324;
        bannerItem2.descript = "未成年的小孩子的节日";
        bannerItem2.url = "http://e.hiphotos.baidu.com/image/pic/item/1b4c510fd9f9d72a5c832b00d12a2834359bbbc3.jpg";

        BannerItem bannerItem3 = new BannerItem();
        bannerItem3.name = "端午节";
        bannerItem3.amount = 515.2324;
        bannerItem3.descript = "每年的端午节，吃粽子，赛龙舟";
        bannerItem3.url = "http://e.hiphotos.baidu.com/image/pic/item/1b4c510fd9f9d72a5c832b00d12a2834359bbbc3.jpg";

        bannerList.add(bannerItem1);
        bannerList.add(bannerItem2);
        bannerList.add(bannerItem3);

        CacheManager.cacheModel(this, "bannerList", bannerList);
    }

    @OnClick(R.id.get_cache_btn)
    public void getCache() {
        List<BannerItem> bannerList = CacheManager.getModelForKey(this, "bannerList");
        if (bannerList != null) {
            Log.d(TAG, bannerList.toString());
        } else {
            Log.d(TAG, "null");
        }
    }
}
