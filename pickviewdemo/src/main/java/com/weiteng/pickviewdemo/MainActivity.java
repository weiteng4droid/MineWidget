package com.weiteng.pickviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.model.IPickerViewData;
import com.weiteng.pickviewdemo.bean.PickerViewData;
import com.weiteng.pickviewdemo.bean.ProvinceBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ArrayList<ProvinceBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<IPickerViewData>>> options3Items = new ArrayList<>();
    private TextView tvTime, tvOptions;
    TimePickerView pvTime;
    OptionsPickerView<ProvinceBean> pvOptions;
    View vMasker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vMasker = findViewById(R.id.vMasker);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvOptions = (TextView) findViewById(R.id.tvOptions);

//      时间选择器
        pvTime = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);

//      控制时间范围
//      Calendar calendar = Calendar.getInstance();
//      pvTime.setRange(calendar.get(Calendar.YEAR) - 20, calendar.get(Calendar.YEAR));//要在setTime 之前才有效果哦

        pvTime.setTime(new Date());
        pvTime.setCyclic(false);
        pvTime.setCancelable(true);

        //时间选择后回调
        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

            @Override
            public void onTimeSelect(String date) {
                tvTime.setText(getTime(date));
            }
        });

        //弹出时间选择器
        tvTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                pvTime.show();
            }
        });
        //选项选择器
        pvOptions = new OptionsPickerView<>(this);

        //选项1
        options1Items.add(new ProvinceBean(0, "广东", "广东省，以岭南东道、广南东路得名", "其他数据"));
        options1Items.add(new ProvinceBean(1, "湖南", "湖南省地处中国中部、长江中游，因大部分区域处于洞庭湖以南而得名湖南", "芒果TV"));
        options1Items.add(new ProvinceBean(3, "广西", "嗯～～", ""));
        options1Items.add(new ProvinceBean(4, "陕西", "嗯～～", ""));
        options1Items.add(new ProvinceBean(5, "山西", "嗯～～", ""));
        options1Items.add(new ProvinceBean(6, "甘肃", "嗯～～", ""));
        options1Items.add(new ProvinceBean(7, "江苏", "嗯～～", ""));
        options1Items.add(new ProvinceBean(8, "浙江", "嗯～～", ""));
        options1Items.add(new ProvinceBean(9, "福建", "嗯～～", ""));
        options1Items.add(new ProvinceBean(10, "湖北", "嗯～～", ""));

        //三级联动效果
        pvOptions.setPicker(options1Items);
        pvOptions.setCancelable(true);
        //设置选择的三级单位
//        pvOptions.setLabels("省", "市", "区");
        pvOptions.setCyclic(false);
        //设置默认选中的三级项目
        //监听确定选择按钮
        pvOptions.setSelectOptions(3);
        pvOptions.setOnOptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                //返回的分别是三个级别的选中位置
                String tx = options1Items.get(options1).getPickerViewText();
                tvOptions.setText(tx);
                vMasker.setVisibility(View.GONE);
            }
        });
        //点击弹出选项选择器
        tvOptions.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                pvOptions.show();
            }
        });
        pvOptions.setTextSize(18);
    }

    public static String getTime(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return format.format(date);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (pvOptions.isShowing() || pvTime.isShowing()) {
                pvOptions.dismiss();
                pvTime.dismiss();
                return true;
            }
            if (pvTime.isShowing()) {
                pvTime.dismiss();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
