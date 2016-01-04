package com.botpy.demo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by weiTeng on 2016/1/4.
 */
public class SnowView extends View {

    private static final int NUM_SNOWFLAKES = 150;
    private static final int DELAY = 5;

    private SnowFlake[] mSnowFlaks = new SnowFlake[NUM_SNOWFLAKES];

    public SnowView(Context context) {
        super(context);
    }

    public SnowView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SnowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(w != oldw || h != oldh){
            resize(w, h);
        }
    }

    public void resize(int width, int hight){
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);

        for(int i = 0; i < NUM_SNOWFLAKES; i++){
            mSnowFlaks[i] = SnowFlake.create(width, hight, paint);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for(SnowFlake flake : mSnowFlaks){
            flake.draw(canvas);
        }
        getHandler().postDelayed(flashRunnable, DELAY);
    }

    private Runnable flashRunnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };
}
