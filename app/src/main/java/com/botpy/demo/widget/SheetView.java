package com.botpy.demo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.botpy.demo.R;
import com.botpy.demo.ui.model.Row;

import java.util.ArrayList;
import java.util.List;

/**
 * TableView
 *
 * Created by weiTeng on 2016/5/12.
 */
public class SheetView extends View {

    private Paint mPaint, mBorderPaint;
    private int mRowCount, mColumnCount;
    private int mCellPadding;

    private List<Row> mRows;

    private Rect[][] mTableRect;
    private float mTextOffsetY;
    private int mBorderWidth;
    private int mBorderColor;
    private int mHeaderColor;

    public SheetView(Context context) {
        this(context, null);
    }

    public SheetView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SheetView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SheetView);
        mCellPadding = typedArray.getDimensionPixelSize(R.styleable.SheetView_cell_padding,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, context.getResources().getDisplayMetrics()));
        mBorderWidth = typedArray.getDimensionPixelOffset(R.styleable.SheetView_border_width,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.5f, context.getResources().getDisplayMetrics()));
        mRowCount = typedArray.getInt(R.styleable.SheetView_row, 1);
        mColumnCount = typedArray.getInt(R.styleable.SheetView_column, 1);
        mHeaderColor = typedArray.getColor(R.styleable.SheetView_header_background, 0);
        mBorderColor = typedArray.getColor(R.styleable.SheetView_border_color, 0xffffffff);
        int textColor = typedArray.getColor(R.styleable.SheetView_text_color, 0xff808080);

        typedArray.recycle();

        if (mTableRect == null) {
            mTableRect = new Rect[mRowCount][mColumnCount];
        }

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(textColor);
        mPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 13.0f, getResources().getDisplayMetrics()));

        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setColor(mBorderColor);

        mTextOffsetY = (mPaint.descent() + mPaint.ascent()) / 2;
    }

    public void setRows(List<Row> rows) {
        this.mRows = rows;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width, height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize - getPaddingLeft() - getPaddingRight();
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = getDefaultTextMaxWidth();
        } else {
            width = getDefaultTextMaxWidth();
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize - getPaddingBottom() - getPaddingTop();
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = getDefaultTextMaxHeight();
        } else {
            height = getDefaultTextMaxHeight();
        }

        createTableRect(width, height);
        setMeasuredDimension(width + getPaddingLeft() + getPaddingRight(), height + getPaddingBottom() + getPaddingTop());
    }

    private void createTableRect(int width, int height) {
        int cellWidth = width / mColumnCount;
        int cellHeight = height / mRowCount;

        for (int i = 0; i < mTableRect.length; i++) {
            for (int j = 0; j < mTableRect[i].length; j++) {

                if (mTableRect[i][j] == null) {
                    mTableRect[i][j] = new Rect();
                }

                if (j == 0) {
                    mTableRect[i][j].left = getPaddingLeft();
                } else {
                    mTableRect[i][j].left = mTableRect[i][j - 1].right;
                }

                if (i == 0) {
                    mTableRect[i][j].top = getPaddingTop();
                } else {
                    mTableRect[i][j].top = mTableRect[i - 1][j].bottom;
                }
                mTableRect[i][j].right = mTableRect[i][j].left + cellWidth;
                mTableRect[i][j].bottom =  mTableRect[i][j].top + cellHeight;
            }
        }
    }

    private int getDefaultTextMaxWidth() {
        int max = 0;
        for (int i = 0; i < mRowCount; i++) {
            Rect tempRect = new Rect();
            String content = mRows.get(0).getRowContent(i);
            mPaint.getTextBounds(content, 0, content.length(), tempRect);
            max += (mCellPadding * 2 + tempRect.width());
        }
        return max;
    }

    private int getDefaultTextMaxHeight() {
        int max = 0;
        for (int i = 0; i < mColumnCount; i++) {
            Rect tempRect = new Rect();
            String content = mRows.get(i).getRowContent(0);
            mPaint.getTextBounds(content, 0, content.length(), tempRect);
            max += (mCellPadding * 2 + tempRect.height());
        }
        return max;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mBorderPaint.setStrokeWidth(mBorderWidth);

        Rect textRect = new Rect();
        for (int i = 0; i < mTableRect.length; i++) {
            for (int j = 0; j < mTableRect[i].length; j++) {
                Rect rect = mTableRect[i][j];

                // draw sheet border
                if (i == 0 && mHeaderColor != 0) {
                    mBorderPaint.setColor(mHeaderColor);
                    mBorderPaint.setStyle(Paint.Style.FILL);
                    canvas.drawRect(rect, mBorderPaint);
                }

                mBorderPaint.setColor(mBorderColor);
                mBorderPaint.setStyle(Paint.Style.STROKE);
                canvas.drawRect(rect, mBorderPaint);

                // draw text
                final String text = mRows.get(i).cells.get(j);
                if (!TextUtils.isEmpty(text)) {
                    mPaint.getTextBounds(text, 0, text.length(), textRect);
                    canvas.drawText(text, 0, text.length(), rect.left + (rect.right - rect.left - textRect.width()) / 2,
                            rect.top + (rect.height() / 2 - mTextOffsetY), mPaint);
                }
            }
        }

        // draw border line again
        mBorderPaint.setStrokeWidth(mBorderWidth + 2);
        Rect rect1 = mTableRect[0][0];
        Rect rect2 = mTableRect[0][mColumnCount - 1];
        Rect rect3 = mTableRect[mRowCount - 1][mColumnCount - 1];
        Rect rect4 = mTableRect[mRowCount - 1][0];

        canvas.drawLine(rect1.left, rect1.top, rect2.right, rect2.top, mBorderPaint);
        canvas.drawLine(rect2.right, rect2.top, rect3.right, rect3.bottom, mBorderPaint);
        canvas.drawLine(rect3.right, rect3.bottom, rect4.left, rect4.bottom, mBorderPaint);
        canvas.drawLine(rect4.left, rect4.bottom, rect1.left, rect1.top, mBorderPaint);
    }
}
