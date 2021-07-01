package com.anythink.custom.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;

import com.anythink.nativead.unitgroup.api.CustomNativeAd;

import java.util.List;

public class KSContentAd extends CustomNativeAd {

    private static String TAG = "TopOn-KS-Content:";
    private Fragment mKSContentFragment;
    private View mKSContentView;
    private Context mContext;

    public KSContentAd(final Context context,Fragment mKSCAd) {
        mKSContentFragment = mKSCAd;
        mContext = context;
    }

    @Override
    public void prepare(final View view, FrameLayout.LayoutParams layoutParams) {

    }

    @Override
    public void prepare(View view, List<View> clickViewList, FrameLayout.LayoutParams layoutParams) {

    }

    public void onClick() {

    }

    @Override
    public Bitmap getAdLogo() {
        return null;
    }

    @Override
    public void clear(final View view) {

    }

    @Override
    public View getAdMediaView(Object... object) {
        if (null != mContext && null != mKSContentFragment){
            mKSContentView = new View(mContext);
            mKSContentView.setTag(mKSContentFragment);
            return mKSContentView;
        }
        return null;
    }

    @Override
    public boolean isNativeExpress() {
        return false;
    }

    @Override
    public void destroy() {
        Log.i(TAG, "destroy()");
        if (mKSContentFragment != null) {
            mKSContentFragment = null;
        }
        if (null != mContext) {
            mContext = null;
        }
    }

}
