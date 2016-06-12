package com.botpy.demo.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.TypedValue;

/**
 * Created by weiTeng on 2016/6/3.
 */
public class BitmapUtil {

    /**
     * composite a tag bitmap
     */
    @NonNull
    public static Bitmap compositeBitmap(Context context, int id) {
        String mTagText = "示例图";
        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(0xffe4544f);

        Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG | Paint.DITHER_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16f, context.getResources().getDisplayMetrics()));
        mTextPaint.setColor(Color.WHITE);

        float mTextOffsetY = (mTextPaint.descent() + mTextPaint.ascent()) / 2;

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), id);
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        Bitmap mainBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);

        Rect mTagRect = new Rect();
        mTagRect.left = 0;
        mTagRect.top = 0;
        mTagRect.right = (int) (mTagRect.left + height * 0.30f);
        mTagRect.bottom = (int) (mTagRect.top + height * 0.30f);

        Rect tempRect = new Rect();
        mTextPaint.getTextBounds(mTagText, 0, mTagText.length(), tempRect);
        int tagWidth = tempRect.height() +
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, context.getResources().getDisplayMetrics()) * 2;
        int offset = (int) (tagWidth / 2.0f + 0.5);
        int d = (int) (offset * Math.sin(45));

        Path path = new Path();
        path.moveTo(mTagRect.right - d, mTagRect.top);
        path.lineTo(mTagRect.right + d, mTagRect.top);
        path.lineTo(mTagRect.left, mTagRect.bottom + d);
        path.lineTo(mTagRect.left, mTagRect.bottom - d);
        path.close();

        Canvas canvas = new Canvas(mainBitmap);
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.save();
        canvas.drawPath(path, mPaint);
        canvas.restore();

        canvas.save();
        canvas.translate(mTagRect.centerX(), mTagRect.centerY());
        canvas.rotate(-45);
        canvas.drawText(mTagText, 0, 0 - mTextOffsetY, mTextPaint);
        canvas.restore();
        return mainBitmap;
    }
}
