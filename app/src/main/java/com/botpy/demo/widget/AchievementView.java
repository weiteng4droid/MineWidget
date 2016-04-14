package com.botpy.demo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.botpy.demo.R;
import com.botpy.demo.ui.model.ChartModel;

import java.util.ArrayList;

/**
 * 我也业绩图表
 * @author weiTeng
 * @since 2015-12-8 18:01:06
 * @version v1.5.4
 */
public class AchievementView extends View {

	private int mBarWidth = 50;
	private int mBarGap = 20;
	private int mDefaultCount = 4;	// 默认四个小柱子
	private int mMaxHeight = 100;	// 柱状图的最小高度（如果设置不精确的测量模式，就是用该高度）
	private int mChartUnit = 100;	// 图表的单位数据

	private Rect[] mVehicleBounds;	// 所有柱状图的边界
	private Rect[] mTextBounds;		// 文字的边界
	private Rect mCacheBound;		// 最大区域的边界
	private Rect mTempTextBound;	// 临时文字的边界

	private int mTextSize;
	private int mTextColor;

	private Paint mPaint;
	private Paint mTextPaint;

	private ArrayList<ChartModel> mChartModels;
	private AchievementAnimation mAnimation;
	private long mDefaultDuration = 2000;
	private float[] mItemHights = new float[mDefaultCount];
	private String[] mTexts = new String[mDefaultCount];

	public AchievementView(Context context) {
		this(context, null);
	}

	public AchievementView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AchievementView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AchievementView);
		mTextSize = ta.getDimensionPixelSize(R.styleable.AchievementView_achievement_textSize, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, context.getResources().getDisplayMetrics()));
		mTextColor = ta.getColor(R.styleable.AchievementView_achievement_textColor, 0xFF0099CC);
		ta.recycle();

		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setColor(mTextColor);
		mTextPaint.setTextSize(mTextSize);

		if(mTempTextBound == null){
			mTempTextBound = new Rect();
		}
		String temp = "100";
		mTextPaint.getTextBounds(temp, 0, temp.length(), mTempTextBound);

		if(mChartModels == null){
			mChartModels = new ArrayList<>();
		}
		mAnimation = new AchievementAnimation();
		mAnimation.setDuration(mDefaultDuration);
	}

	public void setAnimaDuration(long duration){
		if(mDefaultDuration != duration){
			mDefaultDuration = duration;
			mAnimation.setDuration(mDefaultDuration);
		}
	}

	public void setTextSize(int textSize){
		setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
	}

	private void setTextSize(int unit, int textSize) {
		mPaint.setTextSize(TypedValue.applyDimension(unit, textSize, getResources().getDisplayMetrics()));

		if(mTextSize != textSize){
			mTextSize = textSize;
			requestLayout();
			startAnimation(mAnimation);
		}
	}

	public void setTextColor(int textColor){
		if(mTextColor != textColor){
			mTextColor = textColor;
			invalidate();
			startAnimation(mAnimation);
		}
	}

	/**
	 * 设置图表的模型数据
	 */
	public void setChartModels(ArrayList<ChartModel> models){
		if(mChartModels == null){
			mChartModels = new ArrayList<>();
		}
		if(models == null || models.size() == 0){
			return;
		}

		mChartModels.clear();
		mChartModels.addAll(models);
		requestLayout();
		startAnimation(mAnimation);
	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);

		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int width;
		int height;

		if(widthMode == MeasureSpec.EXACTLY){
			width = widthSize;
			mBarGap = (width - getPaddingLeft() - getPaddingRight() - 4 * mBarWidth) / 3;
		}else{
			width = mBarWidth * mDefaultCount + mBarGap * (mDefaultCount - 1) + getPaddingLeft() + getPaddingRight();
		}

		if(heightMode == MeasureSpec.EXACTLY){
			height = heightSize;
			mMaxHeight = height - getPaddingTop() - getPaddingBottom() - mTempTextBound.height() - 4;
		}else{
			height = mMaxHeight + mTempTextBound.height() + getPaddingBottom() + getPaddingTop() + 4;
		}

		if(mTextBounds == null){
			mTextBounds = new Rect[mDefaultCount];
		}
		if(mVehicleBounds == null){
			mVehicleBounds = new Rect[mDefaultCount];
		}

		if(mCacheBound == null){
			mCacheBound = new Rect();
		}
		mCacheBound.left = getPaddingLeft();
		mCacheBound.top = getPaddingTop();
		mCacheBound.right = width - getPaddingRight();
		mCacheBound.bottom = height - getPaddingBottom();

		for (int i = 0; i < mChartModels.size(); i++) {
			ChartModel model = mChartModels.get(i);
			if(mTextBounds[i] == null){
				mTextBounds[i] = new Rect();
			}
			if(mVehicleBounds[i] == null){
				mVehicleBounds[i] = new Rect();
			}
			mTextPaint.getTextBounds(model.text, 0, model.text.length(), mTextBounds[i]);		// 获取文字的区域

			int number = Integer.parseInt(model.text);
			int barHeight;
			if(number >= mChartUnit){
				barHeight = mMaxHeight;
			}else {
				barHeight = number * mMaxHeight / mChartUnit;
			}

			mVehicleBounds[i].left = i * (mBarWidth + mBarGap) + mCacheBound.left;
			mVehicleBounds[i].top = mCacheBound.bottom - barHeight;
			mVehicleBounds[i].right = mVehicleBounds[i].left + mBarWidth;
			mVehicleBounds[i].bottom = mCacheBound.bottom;
		}
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(mChartModels.size() > 0) {
			for (int i = 0; i < mVehicleBounds.length; i++) {
				ChartModel model = mChartModels.get(i);
				mPaint.setColor(model.color);
				Rect rectangle = mVehicleBounds[i];
				canvas.drawRect(rectangle.left, mItemHights[i], rectangle.right, rectangle.bottom, mPaint);
				mTextPaint.getTextBounds(mTexts[i], 0, mTexts[i].length(), mTempTextBound);
				canvas.drawText(mTexts[i], mVehicleBounds[i].left + (rectangle.width() - mTempTextBound.width()) / 2, mItemHights[i] - mTextBounds[i].height() / 4, mTextPaint);
			}
		}
		mPaint.setColor(0xff0000ff);
		canvas.drawLine(mCacheBound.left - getPaddingLeft(), mCacheBound.bottom, mCacheBound.right + getPaddingRight(), mCacheBound.bottom, mPaint);
	}

	private class AchievementAnimation extends Animation {

		@Override
		protected void applyTransformation(float interpolatedTime, Transformation t) {
			super.applyTransformation(interpolatedTime, t);
			if(interpolatedTime <= 1.0f){
				for (int i = 0; i < mVehicleBounds.length; i++) {
					mItemHights[i] = mVehicleBounds[i].bottom - mVehicleBounds[i].height() * interpolatedTime;
					mTexts[i] = String.valueOf((int)(Integer.parseInt(mChartModels.get(i).text) * interpolatedTime));
				}
			}
			postInvalidate();
		}
	}
}
