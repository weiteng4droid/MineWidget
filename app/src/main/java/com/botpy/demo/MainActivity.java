package com.botpy.demo;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.botpy.demo.base.BaseFragment;
import com.botpy.demo.fragment.FirstFragment;
import com.botpy.demo.fragment.MineFrgment;
import com.botpy.demo.fragment.SecondFragment;
import com.botpy.demo.fragment.ThirdFragment;
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

        mTabview.setNormalDrawables(
                R.mipmap.home_insruance_normal,
                R.mipmap.home_msg_normal,
                R.mipmap.home_service_normal,
                R.mipmap.home_me_normal);
        mTabview.setSelectedDawables(
                R.mipmap.home_insruance_selected,
                R.mipmap.home_msg_selected,
                R.mipmap.home_service_selected,
                R.mipmap.home_me_seleted);

        mTabview.setDrawableWidth(80);
        mTabview.setCurrentIndex(mIndex);
        mTabview.setOnTabClickListener(this);
        mTabview.showNoticePoint(true);
        mTabview.showNoticePointAtPostion(0, true);
        mTabview.showNoticePointAtPostion(2, true);
//        mTabview.clearNoticePointOnTouch(true);
        initAllFragment();
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
            if (!to.isAdded()) {
                transaction.hide(mCurrentFragment).add(R.id.contentPanel, to).commit();
            } else {

                transaction.hide(mCurrentFragment).show(to).commit();
            }
            mCurrentFragment = to;
        }
    }
}
