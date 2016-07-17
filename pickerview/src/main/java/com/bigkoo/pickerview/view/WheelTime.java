package com.bigkoo.pickerview.view;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.bigkoo.pickerview.R;
import com.bigkoo.pickerview.TimePickerView.Type;
import com.bigkoo.pickerview.adapter.NumericWheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.OnItemSelectedListener;

import android.content.Context;
import android.view.View;

/**
 * Created by weiTeng on 2016/4/31
 */
public class WheelTime {

	public static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);

	private View mView;
	private WheelView mYearWheel;
	private WheelView mMonthWheel;
	private WheelView mDayWheel;
	private WheelView mHourWheel;
	private WheelView mMinWheel;

	private Type type;
	public static final int DEFAULT_START_YEAR = 1990;
	public static final int DEFAULT_END_YEAR = 2100;

	private int startYear = DEFAULT_START_YEAR;
	private int endYear = DEFAULT_END_YEAR;


	public WheelTime(View view) {
		super();
		this.mView = view;
		type = Type.ALL;
		setView(view);
	}
	public WheelTime(View view,Type type) {
		super();
		this.mView = view;
		this.type = type;
		setView(view);
	}
	public void setPicker(int year ,int month,int day){
		this.setPicker(year, month, day, 0, 0);
	}
	
	public void setPicker(int year ,int month , int day, int h, int m) {
		// 添加大小月月份并将其转换为list,方便之后的判断
		String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
		String[] months_little = { "4", "6", "9", "11" };

		final List<String> listBig = Arrays.asList(months_big);
		final List<String> listLittle = Arrays.asList(months_little);

		Context context = mView.getContext();
		// 年
		mYearWheel = (WheelView) mView.findViewById(R.id.year);
		mYearWheel.setAdapter(new NumericWheelAdapter(startYear, endYear)); // 设置"年"的显示数据
		mYearWheel.setLabel(context.getString(R.string.picker_view_year));	// 添加文字
		mYearWheel.setCurrentItem(year - startYear);						// 初始化时显示的数据

		// 月
		mMonthWheel = (WheelView) mView.findViewById(R.id.month);
		mMonthWheel.setAdapter(new NumericWheelAdapter(1, 12));
		mMonthWheel.setLabel(context.getString(R.string.picker_view_month));
		mMonthWheel.setCurrentItem(month);

		// 日
		mDayWheel = (WheelView) mView.findViewById(R.id.day);
		// 判断大小月及是否闰年,用来确定"日"的数据
		if (listBig.contains(String.valueOf(month + 1))) {
			mDayWheel.setAdapter(new NumericWheelAdapter(1, 31));
		} else if (listLittle.contains(String.valueOf(month + 1))) {
			mDayWheel.setAdapter(new NumericWheelAdapter(1, 30));
		} else {
			// 处理闰年
			if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
				mDayWheel.setAdapter(new NumericWheelAdapter(1, 29));
			} else {
				mDayWheel.setAdapter(new NumericWheelAdapter(1, 28));
			}
		}
		mDayWheel.setLabel(context.getString(R.string.picker_view_day));
		mDayWheel.setCurrentItem(day - 1);

        mHourWheel = (WheelView) mView.findViewById(R.id.hour);
		mHourWheel.setAdapter(new NumericWheelAdapter(0, 23));
		mHourWheel.setLabel(context.getString(R.string.picker_view_hours));// 添加文字
		mHourWheel.setCurrentItem(h);

		mMinWheel = (WheelView) mView.findViewById(R.id.min);
		mMinWheel.setAdapter(new NumericWheelAdapter(0, 59));
		mMinWheel.setLabel(context.getString(R.string.picker_view_minutes));// 添加文字
		mMinWheel.setCurrentItem(m);

		// 添加"年"监听
		OnItemSelectedListener wheelListener_year = new OnItemSelectedListener() {
			@Override
			public void onItemSelected(int index) {
				int year_num = index + startYear;
				// 判断大小月及是否闰年,用来确定"日"的数据
				int maxItem = 30;
				if (listBig
						.contains(String.valueOf(mMonthWheel.getCurrentItem() + 1))) {
					mDayWheel.setAdapter(new NumericWheelAdapter(1, 31));
					maxItem = 31;
				} else if (listLittle.contains(String.valueOf(mMonthWheel
						.getCurrentItem() + 1))) {
					mDayWheel.setAdapter(new NumericWheelAdapter(1, 30));
					maxItem = 30;
				} else {
					if ((year_num % 4 == 0 && year_num % 100 != 0)
							|| year_num % 400 == 0){
						mDayWheel.setAdapter(new NumericWheelAdapter(1, 29));
						maxItem = 29;
					}
					else{
						mDayWheel.setAdapter(new NumericWheelAdapter(1, 28));
						maxItem = 28;
					}
				}
				if (mDayWheel.getCurrentItem() > maxItem - 1){
					mDayWheel.setCurrentItem(maxItem - 1);
				}
			}
		};
		// 添加"月"监听
		OnItemSelectedListener wheelListener_month = new OnItemSelectedListener() {
			@Override
			public void onItemSelected(int index) {
				int month_num = index + 1;
				int maxItem = 30;
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (listBig.contains(String.valueOf(month_num))) {
					mDayWheel.setAdapter(new NumericWheelAdapter(1, 31));
					maxItem = 31;
				} else if (listLittle.contains(String.valueOf(month_num))) {
					mDayWheel.setAdapter(new NumericWheelAdapter(1, 30));
					maxItem = 30;
				} else {
					if (((mYearWheel.getCurrentItem() + startYear) % 4 == 0 && (mYearWheel
							.getCurrentItem() + startYear) % 100 != 0)
							|| (mYearWheel.getCurrentItem() + startYear) % 400 == 0){
						mDayWheel.setAdapter(new NumericWheelAdapter(1, 29));
						maxItem = 29;
					}
					else{
						mDayWheel.setAdapter(new NumericWheelAdapter(1, 28));
						maxItem = 28;
					}
				}
				if (mDayWheel.getCurrentItem() > maxItem - 1){
					mDayWheel.setCurrentItem(maxItem - 1);
				}

			}
		};
		mYearWheel.setOnItemSelectedListener(wheelListener_year);
		mMonthWheel.setOnItemSelectedListener(wheelListener_month);

		switch(type) {
			case ALL:
				break;

			case YEAR_MONTH_DAY:
				mHourWheel.setVisibility(View.GONE);
				mMinWheel.setVisibility(View.GONE);
				break;

			case HOURS_MINS:
				mYearWheel.setVisibility(View.GONE);
				mMonthWheel.setVisibility(View.GONE);
				mDayWheel.setVisibility(View.GONE);
				break;

			case MONTH_DAY_HOUR_MIN:
				mYearWheel.setVisibility(View.GONE);
				break;

			case YEAR_MONTH:
				mDayWheel.setVisibility(View.GONE);
				mHourWheel.setVisibility(View.GONE);
				mMinWheel.setVisibility(View.GONE);
		}
	}

	public void setTextSize(int textSize) {
		mDayWheel.setTextSize(textSize);
		mMonthWheel.setTextSize(textSize);
		mYearWheel.setTextSize(textSize);
		mHourWheel.setTextSize(textSize);
		mMinWheel.setTextSize(textSize);
	}

	/**
	 * 设置是否循环滚动
	 * @param cyclic
	 */
	public void setCyclic(boolean cyclic){
		mYearWheel.setCyclic(cyclic);
		mMonthWheel.setCyclic(cyclic);
		mDayWheel.setCyclic(cyclic);
		mHourWheel.setCyclic(cyclic);
		mMinWheel.setCyclic(cyclic);
	}

	public String getTime() {
		return String.valueOf((mYearWheel.getCurrentItem() + startYear)) + "-" +
				(mMonthWheel.getCurrentItem() + 1) + "-" +
				(mDayWheel.getCurrentItem() + 1) + " " +
				mHourWheel.getCurrentItem() + ":" +
				mMinWheel.getCurrentItem();
	}

	public View getView() {
		return mView;
	}

	public void setView(View view) {
		this.mView = view;
	}

	public int getStartYear() {
		return startYear;
	}

	public void setStartYear(int startYear) {
		this.startYear = startYear;
	}

	public int getEndYear() {
		return endYear;
	}

	public void setEndYear(int endYear) {
		this.endYear = endYear;
	}
}
