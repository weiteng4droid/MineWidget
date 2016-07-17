package com.bigkoo.pickerview.view;

import android.view.View;

import com.bigkoo.pickerview.R;
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.OnItemSelectedListener;

import java.util.List;

public class WheelOptions<T> {

	private View view;
	private WheelView mOption1;
	private WheelView mOption2;
	private WheelView mOption3;

	private List<T> mOptions1Items;
	private List<List<T>> mOptions2Items;
	private List<List<List<T>>> mOptions3Items;

    private boolean linkage = false;
    private OnItemSelectedListener wheelListener_option1;
    private OnItemSelectedListener wheelListener_option2;

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public WheelOptions(View view) {
		super();
		this.view = view;
		setView(view);
	}

	public void setPicker(List<T> items) {
		setPicker(items, null, null, false);
	}

	public void setPicker(List<T> items1, List<List<T>> items2, boolean linkage) {
		setPicker(items1, items2, null, linkage);
	}

	public void setPicker(List<T> options1Items,
			List<List<T>> options2Items,
			List<List<List<T>>> options3Items,
			boolean linkage) {

        this.linkage = linkage;
		this.mOptions1Items = options1Items;
		this.mOptions2Items = options2Items;
		this.mOptions3Items = options3Items;
		int len = ArrayWheelAdapter.DEFAULT_LENGTH;
		if (this.mOptions3Items == null) {
			len = 8;
		}
		if (this.mOptions2Items == null) {
			len = 12;
		}
		// 选项1
		mOption1 = (WheelView) view.findViewById(R.id.options1);
		mOption1.setAdapter(new ArrayWheelAdapter<>(mOptions1Items, len));	// 设置显示数据
		mOption1.setCurrentItem(0);											// 初始化时显示的数据
		// 选项2
		mOption2 = (WheelView) view.findViewById(R.id.options2);
		if (mOptions2Items != null) {
			mOption2.setAdapter(new ArrayWheelAdapter<>(mOptions2Items.get(0)));  // 设置显示数据
			mOption2.setCurrentItem(mOption1.getCurrentItem());                   // 初始化时显示的数据
		}
		// 选项3
		mOption3 = (WheelView) view.findViewById(R.id.options3);
		if (mOptions3Items != null) {
			mOption3.setAdapter(new ArrayWheelAdapter<>(mOptions3Items.get(0).get(0)));  // 设置显示数据
			mOption3.setCurrentItem(mOption3.getCurrentItem());                          // 初始化时显示的数据
		}

		if (this.mOptions2Items == null)
			mOption2.setVisibility(View.GONE);
		if (this.mOptions3Items == null)
			mOption3.setVisibility(View.GONE);

		// 联动监听器
        wheelListener_option1 = new OnItemSelectedListener() {

			@Override
			public void onItemSelected(int index) {
				int opt2Select = 0;
				if (mOptions2Items != null) {
                    opt2Select = mOption2.getCurrentItem();//上一个opt2的选中位置
					//新opt2的位置，判断如果旧位置没有超过数据范围，则沿用旧位置，否则选中最后一项
                    opt2Select = opt2Select >= mOptions2Items.get(index).size() - 1 ? mOptions2Items.get(index).size() - 1 : opt2Select;

					mOption2.setAdapter(new ArrayWheelAdapter<>(mOptions2Items.get(index)));
					mOption2.setCurrentItem(opt2Select);
				}
				if (mOptions3Items != null) {
                    wheelListener_option2.onItemSelected(opt2Select);
				}
			}
		};
        wheelListener_option2 = new OnItemSelectedListener() {

			@Override
			public void onItemSelected(int index) {
				if (mOptions3Items != null) {
                    int opt1Select = mOption1.getCurrentItem();
                    opt1Select = opt1Select >= mOptions3Items.size() - 1 ? mOptions3Items.size() - 1 : opt1Select;
                    index = index >= mOptions2Items.get(opt1Select).size() - 1 ?  mOptions2Items.get(opt1Select).size() - 1 : index;
					int opt3 = mOption3.getCurrentItem();//上一个opt3的选中位置
                    //新opt3的位置，判断如果旧位置没有超过数据范围，则沿用旧位置，否则选中最后一项
                    opt3 = opt3 >= mOptions3Items.get(opt1Select).get(index).size() - 1 ? mOptions3Items.get(opt1Select).get(index).size() - 1 : opt3;

					mOption3.setAdapter(new ArrayWheelAdapter<>(mOptions3Items.get(mOption1.getCurrentItem()).get(index)));
					mOption3.setCurrentItem(opt3);

				}
			}
		};

		// 添加联动监听
		if (options2Items != null && linkage) {
			mOption1.setOnItemSelectedListener(wheelListener_option1);
		}

		if (options3Items != null && linkage) {
			mOption2.setOnItemSelectedListener(wheelListener_option2);
		}
	}

	/**
	 * 设置选项的单位
	 * @param label1 单位
	 * @param label2 单位
	 * @param label3 单位
	 */
	public void setLabels(String label1, String label2, String label3) {
		if (label1 != null)
			mOption1.setLabel(label1);
		if (label2 != null)
			mOption2.setLabel(label2);
		if (label3 != null)
			mOption3.setLabel(label3);
	}

	public void setTextSize(int textSize) {
		mOption1.setTextSize(textSize);
		mOption2.setTextSize(textSize);
		mOption3.setTextSize(textSize);
	}

	/**
	 * 设置是否循环滚动
	 * @param cyclic 是否循环
	 */
	public void setCyclic(boolean cyclic) {
		mOption1.setCyclic(cyclic);
		mOption2.setCyclic(cyclic);
		mOption3.setCyclic(cyclic);
	}

	/**
	 * 分别设置第一二三级是否循环滚动
	 * @param cyclic1,cyclic2,cyclic3 是否循环
	 */
	public void setCyclic(boolean cyclic1,boolean cyclic2,boolean cyclic3) {
        mOption1.setCyclic(cyclic1);
        mOption2.setCyclic(cyclic2);
        mOption3.setCyclic(cyclic3);
	}
    /**
     * 设置第二级是否循环滚动
     * @param cyclic 是否循环
     */
    public void setOption2Cyclic(boolean cyclic) {
        mOption2.setCyclic(cyclic);
    }

	/**
     * 设置第三级是否循环滚动
     * @param cyclic 是否循环
     */
    public void setOption3Cyclic(boolean cyclic) {
        mOption3.setCyclic(cyclic);
    }

	/**
	 * 返回当前选中的结果对应的位置数组 因为支持三级联动效果，分三个级别索引，0，1，2
	 * @return 索引数组
     */
	public int[] getCurrentItems() {
		int[] currentItems = new int[3];
		currentItems[0] = mOption1.getCurrentItem();
		currentItems[1] = mOption2.getCurrentItem();
		currentItems[2] = mOption3.getCurrentItem();
		return currentItems;
	}

	public void setCurrentItems(int option1, int option2, int option3) {
        if(linkage){
            itemSelected(option1, option2, option3);
        }
        mOption1.setCurrentItem(option1);
        mOption2.setCurrentItem(option2);
        mOption3.setCurrentItem(option3);
	}

	private void itemSelected(int opt1Select, int opt2Select, int opt3Select) {
		if (mOptions2Items != null) {
			mOption2.setAdapter(new ArrayWheelAdapter<>(mOptions2Items.get(opt1Select)));
			mOption2.setCurrentItem(opt2Select);
		}
		if (mOptions3Items != null) {
			mOption3.setAdapter(new ArrayWheelAdapter<>(mOptions3Items.get(opt1Select).get(opt2Select)));
			mOption3.setCurrentItem(opt3Select);
		}
	}
}
