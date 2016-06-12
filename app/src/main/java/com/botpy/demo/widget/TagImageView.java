package com.botpy.demo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

import com.botpy.demo.R;

/**
 * Created by weiTeng on 2016/6/2.
 */
public class TagImageView extends ImageView {

    private static final String TAG = "TagImageView";

    private int mDrawableWidth;
    private int mDrawableHeight;

    private Paint mPaint;
    private Paint mTextPaint;
    private Path mPath;
    private Rect mTagRect;
    private float mTextOffsetY;
    private String mTagText;

    public TagImageView(Context context) {
        this(context, null);
    }

    public TagImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(R.styleable.TagImageView);
        mTagText = ta.getString(R.styleable.TagImageView_tag_text);
        int bgColor = ta.getColor(R.styleable.TagImageView_tag_background, 0xffe4544f);
        int textColor = ta.getColor(R.styleable.TagImageView_tag_text_color, Color.WHITE);
        ta.recycle();

        if (mTagText == null || "".equals(mTagText)) {
            mTagText = "示例图";
        }
        initPaintParams(bgColor, textColor);
    }

    private void initPaintParams(int bgColor, int textColor) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(bgColor);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG | Paint.DITHER_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16f, getResources().getDisplayMetrics()));
        mTextPaint.setColor(textColor);

        mTextOffsetY = (mTextPaint.descent() + mTextPaint.ascent()) / 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mDrawableWidth = getMeasuredWidth();
        mDrawableHeight = getMeasuredHeight();

        if (mDrawableHeight > 0 && mDrawableWidth > 0) {
            createTagRect();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mDrawableWidth != w) {
            mDrawableWidth = w;
        }

        if (mDrawableHeight != h) {
            mDrawableHeight = h;
        }

        createTagRect();
    }

    private void createTagRect() {
        if (mTagRect == null) {
            mTagRect = new Rect();
        }
        mTagRect.left = getPaddingLeft();
        mTagRect.top = getPaddingTop();
        mTagRect.right = (int) (mTagRect.left + mDrawableHeight * 0.30f);
        mTagRect.bottom = (int) (mTagRect.top + mDrawableHeight * 0.30f);

        Rect tempRect = new Rect();
        mTextPaint.getTextBounds(mTagText, 0, mTagText.length(), tempRect);
        int tagWidth = tempRect.height() +
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, getResources().getDisplayMetrics()) * 2;
        int offset = (int) (tagWidth / 2.0f + 0.5);
        int d = (int) (offset * Math.sin(45));

        mPath = new Path();
        mPath.moveTo(mTagRect.right - d, mTagRect.top);
        mPath.lineTo(mTagRect.right + d, mTagRect.top);
        mPath.lineTo(mTagRect.left, mTagRect.bottom + d);
        mPath.lineTo(mTagRect.left, mTagRect.bottom - d);
        mPath.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.drawPath(mPath, mPaint);
        canvas.restore();

        canvas.save();
        canvas.translate(mTagRect.centerX(), mTagRect.centerY());
        canvas.rotate(-45);
        canvas.drawText(mTagText, 0, 0 - mTextOffsetY, mTextPaint);
        canvas.restore();
    }
}
