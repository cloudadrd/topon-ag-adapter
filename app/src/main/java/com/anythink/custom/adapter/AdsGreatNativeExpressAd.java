package com.anythink.custom.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.adsgreat.base.core.AdvanceNative;
import com.anythink.nativead.unitgroup.api.CustomNativeAd;

import java.util.List;

public class AdsGreatNativeExpressAd extends CustomNativeAd {

    private static final String TAG = AdsGreatNativeExpressAd.class.getSimpleName();
    private AdvanceNative mAgNative;


    public AdsGreatNativeExpressAd(AdvanceNative agNative) {
        mAgNative = agNative;
    }


    @Override
    public void prepare(final View view, FrameLayout.LayoutParams layoutParams) {
    }

    @Override
    public void prepare(View view, List<View> clickViewList, FrameLayout.LayoutParams layoutParams) {

    }


    public void onClick(){
        notifyAdClicked();
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
        if (mAgNative != null) {
            return mAgNative;
        }
        return null;
    }

    @Override
    public boolean isNativeExpress() {
        return true;
    }

    @Override
    public void destroy() {
        Log.i(TAG, "destroy()");
        if (mAgNative != null) {
            mAgNative = null;
        }
    }


}
