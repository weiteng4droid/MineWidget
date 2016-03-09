package com.botpy.demo.ui.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.botpy.demo.R;
import com.botpy.demo.widget.SlideLayout;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends Activity {

    private SlideLayout mSlideLayout;
    private ListView mListView;
    private List<String> mItems;
    private TextView mMenuTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);
        mSlideLayout = (SlideLayout) findViewById(R.id.slidelayout);
        mListView = (ListView) findViewById(R.id.slide_listview);
        mMenuTv = (TextView) findViewById(R.id.menu_tv);

        mMenuTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSlideLayout.isClosed()){
                    mSlideLayout.openMenu();
                } else {
                    mSlideLayout.closeMenu();
                }
            }
        });

        mItems = new ArrayList<>();
        mItems.add("新闻");
        mItems.add("音乐");
        mItems.add("笑话");
        mItems.add("娱乐");
        mItems.add("吐槽");
        mItems.add("新闻");
        mItems.add("音乐");
        mItems.add("笑话");
        mItems.add("娱乐");
        mItems.add("吐槽");
        mItems.add("新闻");
        mItems.add("音乐");
        mItems.add("笑话");
        mItems.add("娱乐");
        mItems.add("吐槽");

        mListView.setAdapter(new SlideAdapter());
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String content = (String) parent.getItemAtPosition(position);
                Toast.makeText(HomeActivity.this, content, Toast.LENGTH_SHORT).show();
            }
        });

        mSlideLayout.setOnSlideChangedListener(new SlideLayout.OnSlideChangedListener() {
            @Override
            public void opened() {
                Toast.makeText(HomeActivity.this, "open", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void closed() {
                Toast.makeText(HomeActivity.this, "close", Toast.LENGTH_SHORT).show();
            }
        });
    }

    class SlideAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if(convertView == null){
                view = View.inflate(HomeActivity.this, R.layout.item_slide_layout, null);
            }else{
                view = convertView;
            }
            TextView tv = (TextView) view.findViewById(R.id.item_label);
            tv.setText(mItems.get(position));
            return view;
        }
    }
}
