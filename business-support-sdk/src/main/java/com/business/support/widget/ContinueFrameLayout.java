package com.business.support.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;

import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;


import com.business.support.R;
import com.business.support.utils.Utils;

public class ContinueFrameLayout extends FrameLayout {

    private int mHeight, mWidth;

    private Paint paint = new Paint();

    private Button mButton;

    public ContinueFrameLayout(Context context) {
        super(context);
    }

    public ContinueFrameLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ContinueFrameLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mHeight == 0) {
            mHeight = getMeasuredHeight();
            mWidth = getMeasuredWidth();
        }

    }

    public void display(final long delayed) {
        if (mHeight > 0 && mWidth > 0 && getChildCount() > 0 && isAttachedToWindow()) {
            post(new Runnable() {
                @Override
                public void run() {

                    Button button = getButton();
                    if (button.getParent() != null) return;

                    addView(button);

                    Animation rotateAnimation = new ScaleAnimation(0.92f, 1.0f, 0.92f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    rotateAnimation.setRepeatMode(Animation.REVERSE);
                    rotateAnimation.setRepeatCount(Animation.INFINITE);
                    rotateAnimation.setDuration(500);
                    button.startAnimation(rotateAnimation);

                    updateColorDelayed(delayed);
                }
            });

        }
    }

    public void updateColorDelayed(long delayed) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mButton != null) {
                    mButton.setBackgroundResource(R.drawable.bssdk_continue_btn_normal);
                }
            }
        }, delayed);
    }

    public Button getButton() {
        if (mButton != null) return mButton;
        mButton = new Button(getContext());
        mButton.setTextSize(19);
        mButton.setTextColor(Color.WHITE);
        mButton.setText("继续赚钱");
        mButton.setPadding(0, 0, 0, Utils.dp2px(3));
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(Utils.dp2px(120), Utils.dp2px(44));
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        mButton.setBackgroundResource(R.drawable.bssdk_continue_btn);
        mButton.setLayoutParams(layoutParams);
        mButton.setClickable(false);
        return mButton;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private float dip2Px(float dip) {
        float density = getResources().getDisplayMetrics().density;
        return dip * density + .5f;
    }

}
