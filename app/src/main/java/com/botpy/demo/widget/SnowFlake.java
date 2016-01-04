package com.botpy.demo.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import java.util.Random;


public class SnowFlake {
    private static final float ANGE_RANGE = 0.1f;
    private static final float HALF_ANGLE_RANGE = ANGE_RANGE / 2f;
    private static final float HALF_PI = (float) Math.PI / 2f;
    private static final int ANGLE_SEED = 25;
    private static final float ANGLE_DIVISOR = 10000;
    private static final int INCREMENT_LOWER = 2;
    private static final int INCREMENT_UPPER = 4;
    private static final int FLAKE_SIZE_LOWER = 7;
    private static final int FLAKE_SIZE_UPPER = 20;

    private final TRandom random;
    private final Point position;
    private float angle;
    private final float increment;
    private final float flakeSize;
    private final Paint paint;

    public static SnowFlake create(int width, int height, Paint paint) {
        TRandom random = new TRandom();
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        Point position = new Point(x, y);
        float angle = random.nextInt(ANGLE_SEED) / ANGLE_SEED * ANGE_RANGE + HALF_PI - HALF_ANGLE_RANGE;
        float increment = random.getRandom(INCREMENT_LOWER, INCREMENT_UPPER);
        float flakeSize = random.getRandom(FLAKE_SIZE_LOWER, FLAKE_SIZE_UPPER);
        return new SnowFlake(random, position, angle, increment, flakeSize, paint);
    }

    SnowFlake(TRandom random, Point position, float angle, float increment, float flakeSize, Paint paint) {
        this.random = random;
        this.position = position;
        this.angle = angle;
        this.increment = increment;
        this.flakeSize = flakeSize;
        this.paint = paint;
    }

    private void move(int width, int height) {
        double x = position.x + (increment * Math.cos(angle));
        double y = position.y + (increment * Math.sin(angle));

        angle += random.getRandom(-ANGLE_SEED, ANGLE_SEED) / ANGLE_DIVISOR;

        position.set((int) x, (int) y);

        if (!isInside(width, height)) {
            reset(width);
        }
    }

    private boolean isInside(int width, int height) {
        int x = position.x;
        int y = position.y;
        return x >= -flakeSize - 1 && x + flakeSize <= width && y >= -flakeSize - 1 && y - flakeSize < height;
    }

    private void reset(int width) {
        position.x = random.nextInt(width);
        position.y = (int) (-flakeSize - 1);
        angle = random.nextInt(ANGLE_SEED) / ANGLE_SEED * ANGE_RANGE + HALF_PI - HALF_ANGLE_RANGE;
    }

    public void draw(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        move(width, height);
        canvas.drawCircle(position.x, position.y, flakeSize, paint);
    }

    static class TRandom{
        private Random random;

        public TRandom(){
            random = new Random();
        }

        public int nextInt(int value){
            return random.nextInt(value);
        }

        public int getRandom(int startValue, int endValue){
            return random.nextInt(endValue - startValue) + startValue;
        }
    }
}