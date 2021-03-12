package com.anythink.custom.adapter;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.anythink.nativead.unitgroup.api.CustomNativeAd;
import com.shu.priory.conn.NativeDataRef;

import java.util.List;

public class IFLYNative extends CustomNativeAd {

    private static String TAG = "OM-AG-Native:";
    private NativeDataRef dataRef;


    public IFLYNative(NativeDataRef dataRef) {
        this.dataRef = dataRef;
        setAdData();
    }

    public void setAdData() {
        setTitle(dataRef.getTitle());
        setDescriptionText(dataRef.getDesc());
        setIconImageUrl(dataRef.getIconUrl());
        setMainImageUrl(dataRef.getImgUrl());
        setCallToActionText(dataRef.getCtatext());
        try {
            setStarRating(Double.parseDouble(dataRef.getRating()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void prepare(final View view, FrameLayout.LayoutParams layoutParams) {
        if (dataRef != null) {
            dataRef.onExposure(view);
            notifyAdImpression();
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dataRef.onClick(v);
                    notifyAdClicked();
                }
            });
        }
    }

    @Override
    public void prepare(View view, List<View> clickViewList, FrameLayout.LayoutParams layoutParams) {
        if (dataRef != null && clickViewList != null && clickViewList.size() > 1) {
            dataRef.onExposure(view);
            notifyAdImpression();
            for (View view1 : clickViewList) {
                view1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dataRef.onClick(v);
                        notifyAdClicked();
                    }
                });
            }

        }
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
        if (dataRef != null) {
            dataRef = null;
        }
    }
}
