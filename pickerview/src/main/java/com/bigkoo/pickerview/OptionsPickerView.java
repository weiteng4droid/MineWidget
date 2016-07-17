package com.bigkoo.pickerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.bigkoo.pickerview.view.BasePickerView;
import com.bigkoo.pickerview.view.WheelOptions;

import java.util.List;

/**
 * 条件选择器
 * Created by Sai on 15/11/22.
 */
public class OptionsPickerView<T> extends BasePickerView implements View.OnClickListener {

    WheelOptions<T> mWheelOptions;
    private OnOptionsSelectListener optionsSelectListener;

    private static final String TAG_SUBMIT = "submit";
    private static final String TAG_CANCEL = "cancel";

    public OptionsPickerView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.pickerview_options, contentContainer);
        View btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setTag(TAG_SUBMIT);
        View btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setTag(TAG_CANCEL);
        btnSubmit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        final View view = findViewById(R.id.optionspicker);
        mWheelOptions = new WheelOptions<>(view);
    }

    public void setPicker(List<T> optionsItems) {
        mWheelOptions.setPicker(optionsItems, null, null, false);
    }

    public void setPicker(List<T> options1Items,
                          List<List<T>> options2Items, boolean linkage) {
        mWheelOptions.setPicker(options1Items, options2Items, null, linkage);
    }

    public void setPicker(List<T> options1Items,
                          List<List<T>> options2Items,
                          List<List<List<T>>> options3Items,
                          boolean linkage) {

        mWheelOptions.setPicker(options1Items, options2Items, options3Items, linkage);
    }

    public void setTextSize(int textSize) {
        mWheelOptions.setTextSize(textSize);
    }

    public void setSelectOptions(int option) {
        mWheelOptions.setCurrentItems(option, 0, 0);
    }

    public void setSelectOptions(int option1, int option2) {
        mWheelOptions.setCurrentItems(option1, option2, 0);
    }

    public void setSelectOptions(int option1, int option2, int option3) {
        mWheelOptions.setCurrentItems(option1, option2, option3);
    }

    public void setLabels(String label1) {
        mWheelOptions.setLabels(label1, null, null);
    }

    public void setLabels(String label1, String label2) {
        mWheelOptions.setLabels(label1, label2, null);
    }

    /**
     * 设置选项的单位
     *
     */
    public void setLabels(String label1, String label2, String label3) {
        mWheelOptions.setLabels(label1, label2, label3);
    }

    public void setCyclic(boolean cyclic) {
        mWheelOptions.setCyclic(cyclic);
    }

    public void setCyclic(boolean cyclic1, boolean cyclic2, boolean cyclic3) {
        mWheelOptions.setCyclic(cyclic1, cyclic2, cyclic3);
    }

    @Override
    public void onClick(View v) {
        String tag = (String) v.getTag();
        if (tag.equals(TAG_CANCEL)) {
            dismiss();
        } else {
            if (optionsSelectListener != null) {
                int[] optionsCurrentItems = mWheelOptions.getCurrentItems();
                optionsSelectListener.onOptionsSelect(optionsCurrentItems[0], optionsCurrentItems[1], optionsCurrentItems[2]);
            }
            dismiss();
        }
    }

    public interface OnOptionsSelectListener {
        void onOptionsSelect(int options1, int option2, int options3);
    }

    public void setOnOptionsSelectListener(
            OnOptionsSelectListener optionsSelectListener) {
        this.optionsSelectListener = optionsSelectListener;
    }
}
