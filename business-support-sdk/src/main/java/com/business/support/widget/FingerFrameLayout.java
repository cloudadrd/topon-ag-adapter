package com.business.support.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.business.support.R;
import com.business.support.utils.Utils;

public class FingerFrameLayout extends FrameLayout {

    private int mHeight, mWidth;

    private ImageView mImageView;

    public FingerFrameLayout(Context context) {
        super(context);
        init();
    }

    public FingerFrameLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FingerFrameLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        setWillNotDraw(false);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mHeight == 0) {
            mHeight = getMeasuredHeight();
            mWidth = getMeasuredWidth();
        }
        if (mHeight > 0 && mWidth > 0 && getChildCount() > 0 && isAttachedToWindow()) {
            post(new Runnable() {
                @Override
                public void run() {
                    ImageView imageView = getImageView();
                    if (imageView.getParent() != null) return;
                    addView(imageView);
                    AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
                    animationDrawable.start();
                }
            });

        }
    }

    public ImageView getImageView() {
        if (mImageView != null) return mImageView;
        mImageView = new ImageView(getContext());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(Utils.dp2px(76), Utils.dp2px(76));
        layoutParams.gravity = Gravity.END;
        layoutParams.rightMargin = Utils.dp2px(100);
        mImageView.setBackgroundResource(R.drawable.bssdk_finger);
        mImageView.setLayoutParams(layoutParams);
        return mImageView;
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
