package com.botpy.demo.interf;

import android.os.Bundle;
import android.view.View;

/**
 * Created by weiTeng on 2015/12/7.
 */
public interface BaseFragmentInterface {

    void initView(View view);

    void initData(Bundle saveInstaceState);

    void reloadData();

    void relase();
}
