package com.botpy.demo.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.botpy.demo.R;
import com.botpy.demo.base.BaseFragment;
import com.botpy.demo.ui.fragment.FirstFragment;
import com.botpy.demo.ui.fragment.MineFrgment;
import com.botpy.demo.ui.fragment.SecondFragment;
import com.botpy.demo.ui.fragment.ThirdFragment;
import com.botpy.demo.widget.TabView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity implements TabView.OnTabClickListener{

    private static final String TAG = MainActivity.class.getSimpleName();

    @InjectView(R.id.tabview)
    TabView mTabview;

    private ArrayList<BaseFragment> mFragments;
    private FragmentManager mFragmentManager;
    private int mIndex = 0;
    private BaseFragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        mFragmentManager = getSupportFragmentManager();

        mTabview.setStateListDrawables(R.drawable.home_insruance_icon, R.drawable.home_msg_icon,
                R.drawable.home_service_icon, R.drawable.home_mine_icon);
        mTabview.setDrawableWidth(getResources().getDimensionPixelOffset(R.dimen.w_dp_28));
        mTabview.setCurrentIndex(mIndex);
        mTabview.setOnTabClickListener(this);
        mTabview.showNoticePoint(true);
        mTabview.showNoticePointAtPosition(0, true);
        mTabview.showNoticePointAtPosition(2, true);
//        mTabview.clearNoticePointOnTouch(true);
        initAllFragment();
        LinearLayout linearLayout = new LinearLayout(this);
        TextView textView = new TextView(this);
        linearLayout.addView(textView);
    }

    private void initAllFragment() {
        if(mFragments == null){
            mFragments = new ArrayList<>();
        }

        mFragments.add(new FirstFragment());
        mFragments.add(new SecondFragment());
        mFragments.add(new ThirdFragment());
        mFragments.add(new MineFrgment());

        mCurrentFragment = mFragments.get(mIndex);
        mFragmentManager.beginTransaction().replace(R.id.contentPanel, mCurrentFragment).commit();
    }

    @Override
    public void onTabClick(int index) {
        mIndex = index;
        switchContent(mFragments.get(mIndex));

    }

    public void switchContent(BaseFragment to) {
        if (mCurrentFragment != to) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.addToBackStack(null);
            if (!to.isAdded()) {
                transaction.hide(mCurrentFragment).add(R.id.contentPanel, to).commit();
            } else {

                transaction.hide(mCurrentFragment).show(to).commit();
            }
            mCurrentFragment = to;
        }
    }
}
