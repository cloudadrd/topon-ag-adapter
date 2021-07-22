package com.test.ad.demo;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

public class MyFrameLayout extends FrameLayout {
    public MyFrameLayout(@NonNull Context context) {
        super(context);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean flag = super.dispatchTouchEvent(event);
        Log.i("MyFrameLayout", "dispatchTouchEvent x=" + event.getX() + ",y=" + event.getY() + ",flag=" + flag);
        return flag;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean flag = super.onInterceptTouchEvent(event);
        Log.i("MyFrameLayout", "onInterceptTouchEvent x=" + event.getX() + ",y=" + event.getY() + ",flag=" + flag);
        return flag;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean flag = super.onTouchEvent(event);

        Log.i("MyFrameLayout", "onTouchEvent x=" + event.getX() + ",y=" + event.getY() + ",flag=" + flag);
        return flag;
    }
}
