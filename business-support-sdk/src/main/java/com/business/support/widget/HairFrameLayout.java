package com.business.support.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;


public class HairFrameLayout extends FrameLayout {

    private int mHeight, mWidth;
    private LinearGradient shader;
    private Rect rect;
    private RectF rectF;

    private int selectedColorIndex = 1;
    private Paint paint = new Paint();

    private Path mPath = new Path();

    public HairFrameLayout(Context context) {
        super(context);
        init();
    }

    public HairFrameLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HairFrameLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        setWillNotDraw(false);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dip2Px(0.5f));
        paint.setAntiAlias(true);
    }

    public void setBorder(float width, float radius, int[] colors) {
        paint.setAntiAlias(true);
        paint.setShadowLayer(5f, 0, 3, Color.GRAY);
        invalidate();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mHeight == 0) {
            mHeight = getMeasuredHeight();
            mWidth = getMeasuredWidth();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();
        if (mHeight > 0 && mWidth > 0) {

            float baseWhScale = 3.6f;
            float whScale = (baseWhScale - ((float) mWidth / mHeight)) / baseWhScale;
            float wValue = mWidth * whScale / 4;

//            paint.setColor(Color.YELLOW);
//            paint.setStyle(Paint.Style.FILL) ;
//            canvas.drawCircle(mWidth * 0.3f, mHeight * 0.94f, 4, paint);
//            canvas.drawCircle(mWidth * 0.36f - wValue, mHeight * 0.24f, 4, paint);
//            canvas.drawCircle(mWidth * 0.54f , 0, 4, paint);
//            canvas.drawCircle(mWidth * 0.64f, mHeight * 0.20f, 4, paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.BLACK);
            mPath.reset();
//            mPath.moveTo(200, 600);
//            mPath.cubicTo(220, 100, 500, 0, 700, 60);
            float hValue = 0;//mHeight * whScale;
            mPath.moveTo(mWidth * 0.3f, mHeight * 0.94f);
            mPath.cubicTo(mWidth * 0.36f - wValue, mHeight * 0.24f, mWidth * 0.54f, 0, mWidth * 0.64f, mHeight * 0.20f);
            Log.i("tjt852", "mWidth=" + mWidth + ",mHeight=" + mHeight + ",wValue=" + wValue + ",hValue=" + hValue);
            canvas.drawPath(mPath, paint);

        }
    }

    private float dip2Px(float dip) {
        float density = getResources().getDisplayMetrics().density;
        return dip * density + .5f;
    }

}
