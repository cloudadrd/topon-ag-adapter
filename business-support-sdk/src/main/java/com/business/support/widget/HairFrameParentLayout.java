package com.business.support.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

public class HairFrameParentLayout extends FrameLayout {


    private HairFrameLayout hairFrameLayout = null;

    public HairFrameParentLayout(Context context) {
        super(context);
    }

    public HairFrameParentLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HairFrameParentLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        super.addView(child, params);
        init();
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        init();
    }


    public void init() {
        if (hairFrameLayout != null) {
            return;
        }
        hairFrameLayout = new HairFrameLayout(getContext());
        addView(hairFrameLayout, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (hairFrameLayout != null) {
            post(new Runnable() {
                @Override
                public void run() {
                    hairFrameLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getMeasuredHeight()));

                }
            });
//            hairFrameLayout.invalidate();
        }
    }


}
