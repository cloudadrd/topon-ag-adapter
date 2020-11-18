package com.anythink.custom.adapter;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.adsgreat.base.core.AdvanceNative;
import com.anythink.nativead.unitgroup.api.CustomNativeAd;

import java.util.ArrayList;
import java.util.List;

public class AdsGreatNativeAd extends CustomNativeAd {

    private static String TAG = "OM-AG-Native:";
    private AdvanceNative mAgNative;


    public AdsGreatNativeAd(AdvanceNative agNative) {
        mAgNative = agNative;
        setAdData();
    }

    public void setAdData() {
        setTitle(mAgNative.getTitle());
        setDescriptionText(mAgNative.getDesc());
        setIconImageUrl(mAgNative.getIconUrl());
        ArrayList<String> imageStringList = new ArrayList<>();
        setMainImageUrl(mAgNative.getImageUrl());
//        imageStringList.add(mAgNative.getImageUrl());
//        setImageUrlList(imageStringList);
        setCallToActionText(mAgNative.getButtonStr());
        try {
            setStarRating(Double.parseDouble(mAgNative.getRate()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void prepare(final View view, FrameLayout.LayoutParams layoutParams) {
        if(mAgNative!=null){
            mAgNative.registeADClickArea(view);
        }
    }

    @Override
    public void prepare(View view, List<View> clickViewList, FrameLayout.LayoutParams layoutParams) {

    }


    public void onClick() {
        notifyAdClicked();
        Log.d(TAG, "onClick");
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
//        if (mAgNative != null) {
//            return mAgNative;
//        }
        return null;
    }

    @Override
    public boolean isNativeExpress() {
        return false;
    }

    @Override
    public void destroy() {
        Log.i(TAG, "destroy()");
        if (mAgNative != null) {
            mAgNative = null;
        }
    }




}
