package com.botpy.demo.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.botpy.demo.interf.BaseFragmentInterface;

/**
 * Created by weiTeng on 2015/12/7.
 */
public abstract class BaseFragment extends Fragment implements BaseFragmentInterface{

    protected Activity mActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected int getLayoutId(){
        return 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayoutId(), container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initView(view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData(savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if(!hidden){
            reloadData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        relase();
    }

    @Override
    public void relase() {

    }

    @Override
    public void reloadData(){

    }
}
