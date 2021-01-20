package com.test.ad.demo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class RoundRainbowFrameLayout extends FrameLayout {
    private static final int[] GRADIENT_COLORS = {Color.parseColor("#3EFFD9"), Color.parseColor("#FF3B50")};
    private static final float BORDER_WIDTH = 1f;//dp
    private static final float BORDER_RADIUS = 3f;//dp


    private boolean drawBorder = true;
    private int[] borderColors = GRADIENT_COLORS;
    private float borderWidth = BORDER_WIDTH;
    private float borderRadius = BORDER_RADIUS;
    private int mHeight;
    private LinearGradient shader;
    private Rect rect;
    private RectF rectF;


    private int selectedColorIndex = 1;
    private Paint paint = new Paint();

    public RoundRainbowFrameLayout(Context context) {
        super(context);
        setWillNotDraw(false);
        setBorder(10, 20, new int[]{Color.BLUE, Color.YELLOW});
    }

    public RoundRainbowFrameLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        setBorder(10, 20, new int[]{Color.BLUE, Color.YELLOW});
    }

    public RoundRainbowFrameLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        setBorder(10, 20, new int[]{Color.BLUE, Color.YELLOW});
    }

    public void setBorder(float width, float radius) {
        setBorder(width, radius, GRADIENT_COLORS);
    }

    public void setBorder(float width, float radius, int[] colors) {
        this.drawBorder = width > 0;
        this.borderWidth = width;
        this.borderRadius = radius;
        this.borderColors = colors;
        paint.setAntiAlias(true);
        paint.setShadowLayer(5f, 0, 3, Color.GRAY);
        invalidate();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mHeight == 0) {
            mHeight = getMeasuredHeight();
        }
//        if (shader == null) {
//            shader = new LinearGradient(0, 0, textWidth, 0,
//                    borderColors, null, Shader.TileMode.CLAMP);
//        }
//        if (textWidth > 0) {
//            //得到父类中写字的那支笔，并套上线性渲染器
//            //设置渐变背景
//            paint.setShader(shader);
//        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
            postDelayed(runnable, 500);
        }
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        postDelayed(runnable, 500);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(runnable);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制渐变圆角边框
        if (mHeight > 0 && drawBorder) {
            canvas.save();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dip2Px(borderWidth));
            if (rect == null) {
                rect = new Rect();
            }
            canvas.getClipBounds(rect);
//            float tempVal = borderWidth / 2;
//            rectF = new RectF(tempVal+8, tempVal+8, rect.right - tempVal-8, rect.bottom - tempVal-8);
//            float radius = dip2Px(borderRadius);
            int tempIndex = selectedColorIndex == 0 ? 1 : 0;
            selectedColorIndex = tempIndex;
            paint.setColor(GRADIENT_COLORS[tempIndex]);
            canvas.drawRect(rect, paint);
            canvas.restore();
        }
    }

    private float dip2Px(float dip) {
        float density = getResources().getDisplayMetrics().density;
        return dip * density + .5f;
    }

}
