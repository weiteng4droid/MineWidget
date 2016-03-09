package com.botpy.demo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * 绘图练习
 *
 * @author weiTeng on 2016/3/2.
 */
public class TestCanvasView extends View {

    // 整个控件的宽度和高度
    private int vWidth;
    private int vHeight;


    // 波浪的终点
    private float waveY;
    private float ctrX, ctrY;

    private Path mPath;
    private Paint mPaint;

    private boolean isInc;

    public TestCanvasView(Context context) {
        this(context, null);
    }

    public TestCanvasView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestCanvasView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(5);
        mPaint.setColor(0xFFA2D6AE);

        mPath = new Path();
        /*
        mPath.moveTo(100, 100);

        mPath.quadTo(200, 50, 300, 100);

        mPath.moveTo(400, 100);
        mPath.quadTo(500, 50, 600, 100);

        mPath.moveTo(200, 400);
        mPath.quadTo(350, 460, 500, 400);
        */
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        vWidth = w;
        vHeight = h;

        // 计算控制点Y坐标
        waveY = 1 / 8F * vHeight;

        // 计算端点Y坐标
        ctrY = -1 / 16F * vHeight;

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        mPath.moveTo(-1 / 4F * vWidth, waveY);

        /*
         * 以二阶曲线的方式通过控制点连接位于控件右边的终点
         * 终点的位置也是在控件外部
         * 我们只需不断让ctrX的大小变化即可实现“浪”的效果
         */
        mPath.quadTo(ctrX, ctrY, vWidth + 1 / 4F * vWidth, waveY);

        // 围绕控件闭合曲线
        mPath.lineTo(vWidth + 1 / 4F * vWidth, vHeight);
        mPath.lineTo(-1 / 4F * vWidth, vHeight);
        mPath.close();

        canvas.drawPath(mPath, mPaint);

        /*
         * 当控制点的x坐标大于或等于终点x坐标时更改标识值
         */
        if (ctrX >= vWidth + 1 / 4F * vWidth) {
            isInc = false;
        }
        /*
         * 当控制点的x坐标小于或等于起点x坐标时更改标识值
         */
        else if (ctrX <= -1 / 4F * vWidth) {
            isInc = true;
        }

        // 根据标识值判断当前的控制点x坐标是该加还是减
        ctrX = isInc ? ctrX + 20 : ctrX - 20;

        /*
         * 让“水”不断减少
         */
        if (ctrY <= vHeight - 200) {
            ctrY += 2;
            waveY += 2;
        }

        mPath.reset();

        // 重绘
        invalidate();
    }
}
