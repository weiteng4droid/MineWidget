package com.bigkoo.pickerview.lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import com.bigkoo.pickerview.R;
import com.bigkoo.pickerview.adapter.WheelAdapter;
import com.bigkoo.pickerview.listener.OnItemSelectedListener;
import com.bigkoo.pickerview.model.IPickerViewData;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 3d滚轮控件
 */
public class WheelView extends View {

    private static final String TAG = "WheelView";

    /**
     * 点击，滑翔(滑到尽头)，拖拽事件
     */
    public enum ACTION {
        CLICK, FLING, DAGGLE
    }

    Context mContext;

    Handler handler;
    private GestureDetector gestureDetector;
    OnItemSelectedListener onItemSelectedListener;

    // Timer mTimer;
    ScheduledExecutorService mExecutor = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> mFuture;

    Paint mOutPaint;
    Paint mCenterPaint;
    Paint mIndicatorPaint;

    WheelAdapter mAdapter;

    private String label;   // 附加单位
    int mTextSize;          // 选项的文字大小
    int maxTextWidth;
    int maxTextHeight;
    float itemHeight;       // 每行高度

    int mOutTextColor;
    int mCenterTextColor;
    int mDividerColor;

    // 条目间距倍数
    private float mLineSpacing;
    boolean isLoop;

    // 第一条线Y坐标值
    float firstLineY;
    //第二条线Y坐标
    float secondLineY;
    //中间Y坐标
    float centerY;

    //滚动总高度y值
    int totalScrollY;
    //初始化默认选中第几个
    int initPosition;
    //选中的Item是第几个
    private int selectedItem;
    int preCurrentIndex;
    //滚动偏移值,用于记录滚动了多少个item
    int change;

    // 显示几个条目
    int itemsVisible = 11;

    int measuredHeight;
    int measuredWidth;

    // 半圆周长
    int halfCircumference;
    // 半径
    int radius;

    private int mOffset = 0;
    private float previousY = 0;
    long startTime = 0;

    // 修改这个值可以改变滑行速度
    private static final int VELOCITYFLING = 5;
    int widthMeasureSpec;

    private int mGravity = Gravity.CENTER;
    private int drawCenterContentStart = 0;             // 中间选中文字开始绘制位置
    private int drawOutContentStart = 0;                // 非中间文字开始绘制位置
    private static final float SCALECONTENT = 0.8F;     // 非中间文字则用此控制高度，压扁形成3d错觉
    private static final float CENTERCONTENTOFFSET = 6; // 中间文字文字居中需要此偏移值

    public WheelView(Context context) {
        this(context, null);
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PickView, 0, 0);
        mGravity = a.getInt(R.styleable.PickView_picker_gravity, Gravity.CENTER);
        mOutTextColor = a.getColor(R.styleable.PickView_picker_textColorOut, mOutTextColor);
        mCenterTextColor = a.getColor(R.styleable.PickView_picker_textColorCenter, mCenterTextColor);
        mDividerColor = a.getColor(R.styleable.PickView_picker_dividerColor, mDividerColor);
        mTextSize = a.getDimensionPixelSize(R.styleable.PickView_picker_textSize,
                (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics()));
        mLineSpacing = a.getFloat(R.styleable.PickView_picker_lineSpacing, 1.4f);
        a.recycle();

        initLoopView(context);
    }

    private void initLoopView(Context context) {
        this.mContext = context;
        handler = new MessageHandler(this);
        gestureDetector = new GestureDetector(context, new LoopViewGestureListener(this));
        gestureDetector.setIsLongpressEnabled(false);

        isLoop = true;

        totalScrollY = 0;
        initPosition = -1;

        initPaints();
    }

    private void initPaints() {
        mOutPaint = new Paint();
        mOutPaint.setColor(mOutTextColor);
        mOutPaint.setAntiAlias(true);
        mOutPaint.setTypeface(Typeface.MONOSPACE);
        mOutPaint.setTextSize(mTextSize);

        mCenterPaint = new Paint();
        mCenterPaint.setColor(mCenterTextColor);
        mCenterPaint.setAntiAlias(true);
        mCenterPaint.setTextScaleX(1.1F);
        mCenterPaint.setTypeface(Typeface.MONOSPACE);
        mCenterPaint.setTextSize(mTextSize);

        mIndicatorPaint = new Paint();
        mIndicatorPaint.setColor(mDividerColor);
        mIndicatorPaint.setAntiAlias(true);

        if (android.os.Build.VERSION.SDK_INT >= 11) {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }
    }

    private void remeasure() {
        if (mAdapter == null) {
            return;
        }

        measureTextWidthHeight();

        //最大Text的高度乘间距倍数得到 可见文字实际的总高度，半圆的周长
        halfCircumference = (int) (itemHeight * (itemsVisible - 1));
        //整个圆的周长除以PI得到直径，这个直径用作控件的总高度
        measuredHeight = (int) ((halfCircumference * 2) / Math.PI);
        //求出半径
        radius = (int) (halfCircumference / Math.PI);
        //控件宽度，这里支持weight
        measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        //计算两条横线和控件中间点的Y位置
        firstLineY = (measuredHeight - itemHeight) / 2.0F;
        secondLineY = (measuredHeight + itemHeight) / 2.0F;
        centerY = (measuredHeight + maxTextHeight) / 2.0F - CENTERCONTENTOFFSET;
        //初始化显示的item的position，根据是否loop
        if (initPosition == -1) {
            if (isLoop) {
                initPosition = (mAdapter.getItemsCount() + 1) / 2;
            } else {
                initPosition = 0;
            }
        }

        preCurrentIndex = initPosition;
    }

    /**
     * 计算最大len的Text的宽高度
     */
    private void measureTextWidthHeight() {
        Rect rect = new Rect();
        for (int i = 0; i < mAdapter.getItemsCount(); i++) {
            String s1 = getContentText(mAdapter.getItem(i));
            mCenterPaint.getTextBounds(s1, 0, s1.length(), rect);
            int textWidth = rect.width();
            if (textWidth > maxTextWidth) {
                maxTextWidth = textWidth;
            }
            mCenterPaint.getTextBounds("\u661F\u671F", 0, 2, rect); // 星期
            int textHeight = rect.height();
            if (textHeight > maxTextHeight) {
                maxTextHeight = textHeight;
            }
        }
        itemHeight = mLineSpacing * maxTextHeight;
    }

    void smoothScroll(ACTION action) {
        cancelFuture();
        if (action == ACTION.FLING || action == ACTION.DAGGLE) {
            mOffset = (int) ((totalScrollY % itemHeight + itemHeight) % itemHeight);
            if ((float) mOffset > itemHeight / 2.0F) {
                mOffset = (int) (itemHeight - (float) mOffset);
            } else {
                mOffset = -mOffset;
            }
        }
        //停止的时候，位置有偏移，不是全部都能正确停止到中间位置的，这里把文字位置挪回中间去
        mFuture = mExecutor.scheduleWithFixedDelay(new SmoothScrollTimerTask(this, mOffset), 0, 10, TimeUnit.MILLISECONDS);
    }

    protected final void scrollBy(float velocityY) {
        cancelFuture();

        mFuture = mExecutor.scheduleWithFixedDelay(new InertiaTimerTask(this, velocityY), 0, VELOCITYFLING, TimeUnit.MILLISECONDS);
    }

    public void cancelFuture() {
        if (mFuture != null && !mFuture.isCancelled()) {
            mFuture.cancel(true);
            mFuture = null;
        }
    }

    /**
     * 设置是否循环滚动
     * @param cyclic 是否循环
     */
    public final void setCyclic(boolean cyclic) {
        isLoop = cyclic;
    }

    public final void setTextSize(int size) {
        if (size > 0 && mTextSize != size) {
            mTextSize = size;
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize);
        }
    }

    private void setTextSize(int unit, int textSize){
        mOutPaint.setTextSize(TypedValue.applyDimension(unit, textSize, getResources().getDisplayMetrics()));
        mCenterPaint.setTextSize(TypedValue.applyDimension(unit, textSize, getResources().getDisplayMetrics()));
        requestLayout();
        invalidate();
    }

    public final void setCurrentItem(int currentItem) {
        this.initPosition = currentItem;
        totalScrollY = 0;//回归顶部，不然重设setCurrentItem的话位置会偏移的，就会显示出不对位置的数据
        invalidate();
    }

    public final void setOnItemSelectedListener(OnItemSelectedListener OnItemSelectedListener) {
        this.onItemSelectedListener = OnItemSelectedListener;
    }

    public final void setAdapter(WheelAdapter adapter) {
        this.mAdapter = adapter;
        remeasure();
        invalidate();
    }

    public final WheelAdapter getAdapter() {
        return mAdapter;
    }

    public final int getCurrentItem() {
        return selectedItem;
    }

    protected final void onItemSelected() {
        if (onItemSelectedListener != null) {
            postDelayed(new OnItemSelectedRunnable(this), 200L);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mAdapter == null) {
            return;
        }
        // 可见的item数组
        Object visibles[] = new Object[itemsVisible];
        // 滚动的Y值高度除去每行Item的高度，得到滚动了多少个item，即change数
        change = (int) (totalScrollY / itemHeight);
        try {
            // 滚动中实际的预选中的item(即经过了中间位置的item) ＝ 滑动前的位置 ＋ 滑动相对位置
            preCurrentIndex = initPosition + change % mAdapter.getItemsCount();
        } catch (ArithmeticException e) {
            Log.e(TAG, "Error occur adapter.getItemsCount() == 0");
        }

        if (!isLoop) {// 不循环的情况
            if (preCurrentIndex < 0) {
                preCurrentIndex = 0;
            }
            if (preCurrentIndex > mAdapter.getItemsCount() - 1) {
                preCurrentIndex = mAdapter.getItemsCount() - 1;
            }
        } else {// 循环
            if (preCurrentIndex < 0) {// 举个例子：如果总数是5，preCurrentIndex ＝ －1，那么preCurrentIndex按循环来说，其实是0的上面，也就是4的位置
                preCurrentIndex = mAdapter.getItemsCount() + preCurrentIndex;
            }
            if (preCurrentIndex > mAdapter.getItemsCount() - 1) {//同理上面,自己脑补一下
                preCurrentIndex = preCurrentIndex - mAdapter.getItemsCount();
            }
        }

        //跟滚动流畅度有关，总滑动距离与每个item高度取余，即并不是一格格的滚动，每个item不一定滚到对应Rect里的，这个item对应格子的偏移值
        int itemHeightOffset = (int) (totalScrollY % itemHeight);
        // 设置数组中每个元素的值
        int counter = 0;
        while (counter < itemsVisible) {
            int index = preCurrentIndex - (itemsVisible / 2 - counter);//索引值，即当前在控件中间的item看作数据源的中间，计算出相对源数据源的index值

            //判断是否循环，如果是循环数据源也使用相对循环的position获取对应的item值，如果不是循环则超出数据源范围使用""空白字符串填充，在界面上形成空白无数据的item项
            if (isLoop) {
                index = getLoopMappingIndex(index);
                visibles[counter] = mAdapter.getItem(index);
            } else if (index < 0) {
                visibles[counter] = "";
            } else if (index > mAdapter.getItemsCount() - 1) {
                visibles[counter] = "";
            } else {
                visibles[counter] = mAdapter.getItem(index);
            }

            counter++;
        }

        // 中间两条横线
        canvas.drawLine(0.0F, firstLineY, measuredWidth, firstLineY, mIndicatorPaint);
        canvas.drawLine(0.0F, secondLineY, measuredWidth, secondLineY, mIndicatorPaint);
        // 单位的Label
        if (label != null) {
            int drawRightContentStart = measuredWidth - getTextWidth(mCenterPaint, label);
            //靠右并留出空隙
            canvas.drawText(label, drawRightContentStart - CENTERCONTENTOFFSET, centerY, mCenterPaint);
        }
        counter = 0;
        while (counter < itemsVisible) {
            canvas.save();
            // L(弧长)=α（弧度）* r(半径) （弧度制）
            // 求弧度--> (L * π ) / (π * r)   (弧长X派/半圆周长)
            float itemHeight = maxTextHeight * mLineSpacing;
            double radian = ((itemHeight * counter - itemHeightOffset) * Math.PI) / halfCircumference;
            // 弧度转换成角度(把半圆以Y轴为轴心向右转90度，使其处于第一象限及第四象限
            float angle = (float) (90D - (radian / Math.PI) * 180D);
            // 九十度以上的不绘制
            if (angle >= 90F || angle <= -90F) {
                canvas.restore();
            } else {
                String contentText = getContentText(visibles[counter]);

                //计算开始绘制的位置
                measuredCenterContentStart(contentText);
                measuredOutContentStart(contentText);
                float translateY = (float) (radius - Math.cos(radian) * radius - (Math.sin(radian) * maxTextHeight) / 2D);
                //根据Math.sin(radian)来更改canvas坐标系原点，然后缩放画布，使得文字高度进行缩放，形成弧形3d视觉差
                canvas.translate(0.0F, translateY);
                canvas.scale(1.0F, (float) Math.sin(radian));
                if (translateY <= firstLineY && maxTextHeight + translateY >= firstLineY) {
                    // 条目经过第一条线
                    canvas.save();
                    canvas.clipRect(0, 0, measuredWidth, firstLineY - translateY);
                    canvas.scale(1.0F, (float) Math.sin(radian) * SCALECONTENT);
                    canvas.drawText(contentText, drawOutContentStart, maxTextHeight, mOutPaint);
                    canvas.restore();
                    canvas.save();
                    canvas.clipRect(0, firstLineY - translateY, measuredWidth, (int) (itemHeight));
                    canvas.scale(1.0F, (float) Math.sin(radian) * 1F);
                    canvas.drawText(contentText, drawCenterContentStart, maxTextHeight - CENTERCONTENTOFFSET, mCenterPaint);
                    canvas.restore();
                } else if (translateY <= secondLineY && maxTextHeight + translateY >= secondLineY) {
                    // 条目经过第二条线
                    canvas.save();
                    canvas.clipRect(0, 0, measuredWidth, secondLineY - translateY);
                    canvas.scale(1.0F, (float) Math.sin(radian) * 1.0F);
                    canvas.drawText(contentText, drawCenterContentStart, maxTextHeight - CENTERCONTENTOFFSET, mCenterPaint);
                    canvas.restore();
                    canvas.save();
                    canvas.clipRect(0, secondLineY - translateY, measuredWidth, (int) (itemHeight));
                    canvas.scale(1.0F, (float) Math.sin(radian) * SCALECONTENT);
                    canvas.drawText(contentText, drawOutContentStart, maxTextHeight, mOutPaint);
                    canvas.restore();
                } else if (translateY >= firstLineY && maxTextHeight + translateY <= secondLineY) {
                    // 中间条目
                    canvas.clipRect(0, 0, measuredWidth, (int) (itemHeight));
                    canvas.drawText(contentText, drawCenterContentStart, maxTextHeight - CENTERCONTENTOFFSET, mCenterPaint);
                    int preSelectedItem = mAdapter.indexOf(visibles[counter]);
                    if (preSelectedItem != -1) {
                        selectedItem = preSelectedItem;
                    }
                } else {
                    // 其他条目
                    canvas.save();
                    canvas.clipRect(0, 0, measuredWidth, (int) (itemHeight));
                    canvas.scale(1.0F, (float) Math.sin(radian) * SCALECONTENT);
                    canvas.drawText(contentText, drawOutContentStart, maxTextHeight, mOutPaint);
                    canvas.restore();
                }
                canvas.restore();
            }
            counter++;
        }
    }

    //递归计算出对应的index
    private int getLoopMappingIndex(int index) {
        if (index < 0) {
            index = index + mAdapter.getItemsCount();
            index = getLoopMappingIndex(index);
        } else if (index > mAdapter.getItemsCount() - 1) {
            index = index - mAdapter.getItemsCount();
            index = getLoopMappingIndex(index);
        }
        return index;
    }

    /**
     * 根据传进来的对象获取getPickerViewText()方法，来获取需要显示的值
     * @param item 数据源的item
     * @return 对应显示的字符串
     */
    private String getContentText(Object item) {
        if (item == null) {
            return "";
        }
        else if (item instanceof IPickerViewData) {
            return ((IPickerViewData) item).getPickerViewText();
        }
        return item.toString();
    }

    private void measuredCenterContentStart(String content) {
        Rect rect = new Rect();
        mCenterPaint.getTextBounds(content, 0, content.length(), rect);
        switch (mGravity) {
            case Gravity.CENTER:
                drawCenterContentStart = (int) ((measuredWidth - rect.width()) * 0.5);
                break;
            case Gravity.LEFT:
                drawCenterContentStart = 0;
                break;
            case Gravity.RIGHT:
                drawCenterContentStart = measuredWidth - rect.width();
                break;
        }
    }

    private void measuredOutContentStart(String content) {
        Rect rect = new Rect();
        mOutPaint.getTextBounds(content, 0, content.length(), rect);
        switch (mGravity) {
            case Gravity.CENTER:
                drawOutContentStart = (int) ((measuredWidth - rect.width()) * 0.5);
                break;
            case Gravity.LEFT:
                drawOutContentStart = 0;
                break;
            case Gravity.RIGHT:
                drawOutContentStart = measuredWidth - rect.width();
                break;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.widthMeasureSpec = widthMeasureSpec;
        remeasure();
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean eventConsumed = gestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTime = System.currentTimeMillis();
                cancelFuture();
                previousY = event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                float dy = previousY - event.getRawY();
                previousY = event.getRawY();
                totalScrollY = (int) (totalScrollY + dy);

                // 边界处理。
                if (!isLoop) {
                    float top = -initPosition * itemHeight;
                    float bottom = (mAdapter.getItemsCount() - 1 - initPosition) * itemHeight;
                    if (totalScrollY - itemHeight * 0.3 < top) {
                        top = totalScrollY - dy;
                    } else if (totalScrollY + itemHeight * 0.3 > bottom) {
                        bottom = totalScrollY - dy;
                    }

                    if (totalScrollY < top) {
                        totalScrollY = (int) top;
                    } else if (totalScrollY > bottom) {
                        totalScrollY = (int) bottom;
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            default:
                if (!eventConsumed) {
                    float y = event.getY();
                    double l = Math.acos((radius - y) / radius) * radius;
                    int circlePosition = (int) ((l + itemHeight / 2) / itemHeight);

                    float extraOffset = (totalScrollY % itemHeight + itemHeight) % itemHeight;
                    mOffset = (int) ((circlePosition - itemsVisible / 2) * itemHeight - extraOffset);

                    if ((System.currentTimeMillis() - startTime) > 120) {
                        // 处理拖拽事件
                        smoothScroll(ACTION.DAGGLE);
                    } else {
                        // 处理条目点击事件
                        smoothScroll(ACTION.CLICK);
                    }
                }
                break;
        }
        invalidate();

        return true;
    }

    public void setLineSpacing(float lineSpacing) {
        mLineSpacing = lineSpacing;
    }

    /**
     * 获取Item个数
     * @return item个数
     */
    public int getItemsCount() {
        return mAdapter != null ? mAdapter.getItemsCount() : 0;
    }

    /**
     * 附加在右边的单位字符串
     * @param label 单位
     */
    public void setLabel(String label) {
        this.label = label;
    }

    public void setGravity(int gravity) {
        this.mGravity = gravity;
    }

    public int getTextWidth(Paint paint, String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil(widths[j]);
            }
        }
        return iRet;
    }
}