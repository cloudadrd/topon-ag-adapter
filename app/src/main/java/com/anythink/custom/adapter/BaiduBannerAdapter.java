/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.anythink.custom.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.anythink.banner.unitgroup.api.CustomBannerAdapter;
import com.anythink.network.baidu.BaiduATConst;
import com.baidu.mobads.sdk.api.BaiduNativeManager;
import com.baidu.mobads.sdk.api.NativeResponse;
import com.baidu.mobads.sdk.api.RequestParameters;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class BaiduBannerAdapter extends CustomBannerAdapter {
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    public static final int ID_LARGE_IMAGE = generateViewId();
    public static final int ID_ADLOGO_IMAGE = generateViewId();
    public static final int ID_ADICON_IMAGE = generateViewId();

    private final String TAG = getClass().getSimpleName();
    private String slotId;

    private BaiduNativeManager mBaiduNativeManager;
    private NativeResponse nativeAd;
    private RelativeLayout mNBView;
    private boolean is320_50 = false;
    private boolean is300_250 = false;


    @Override
    public void loadCustomNetworkAd(Context context, Map<String, Object> serverExtra, Map<String, Object> localExtra) {
        String appId = (String) serverExtra.get("app_id");
        slotId = (String) serverExtra.get("slot_id");
        String w = (String) serverExtra.get("width");
        String h = (String) serverExtra.get("height");
        int width = 0, height= 0;
        if (null != w){
            width = Integer.parseInt(w);
        }
        if (null != h){
            height = Integer.parseInt(h);
        }

        if (320 == width && 50 == height) {
            is320_50 = true;
        }

        if (300 == width && 250 == height) {
            is300_250 = true;
        }

        if (!(is320_50 || is300_250)) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError(TAG, "banner Ad size error!");
            }
            return;
        }
        if (TextUtils.isEmpty(appId) || TextUtils.isEmpty(slotId)) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError(TAG, "app_id or slot_id is empty!");
            }
            return;
        }

        if (mBaiduNativeManager == null) {
            Log.d(TAG, "insk = " + slotId);
            mBaiduNativeManager = new BaiduNativeManager(context.getApplicationContext(), slotId);//mInstancesKey
            if (null != mBaiduNativeManager) {
                mBaiduNativeManager.setAppSid(appId);
                Log.d(TAG, "setAppsID " + appId);
            }
        }
        if (context instanceof Activity) {
            requestAd(context.getApplicationContext());
        } else {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError(TAG, "context class type is not Activity...");
            }
        }
    }

    /**
     * 请求Feed默认模板广告数据
     */
    private void requestAd(Context context) {
        // 若与百度进行相关合作，可使用如下接口上报广告的上下文
        RequestParameters requestParameters = new RequestParameters.Builder()
                .downloadAppConfirmPolicy(RequestParameters.DOWNLOAD_APP_CONFIRM_ONLY_MOBILE)
                .build();
        mBaiduNativeManager.loadFeedAd(requestParameters, new BaiduNativeManager.FeedAdListener() {
            @Override
            public void onNativeLoad(List<NativeResponse> nativeResponses) {
                Log.d(TAG, "onNativeLoad");
                if (nativeResponses != null && nativeResponses.size() > 0) {
                    nativeAd = nativeResponses.get(0);
                    if (mLoadListener != null) {
                        mLoadListener.onAdDataLoaded();
                        renderAd(context);
                        mLoadListener.onAdCacheLoaded();
                    }
                } else {
                    if (mLoadListener != null) {
                        mLoadListener.onAdLoadError(TAG, "no ad fill");
                    }
                }
            }

            @Override
            public void onNoAd(int code, String msg) {
                Log.d(TAG, "onLoadFail reason:" + msg + "errorCode:" + code);
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError(TAG, "code=" + code + ", error=" + msg);
                }
            }

            @Override
            public void onNativeFail(int errorCode, String message) {
                // 建议使用onLoadFail回调获取详细的请求失败的原因
                Log.d(TAG, "onNativeFail reason:" + message);
            }

            @Override
            public void onVideoDownloadSuccess() {
                Log.d(TAG, "onVideoDownloadSuccess");
            }

            @Override
            public void onVideoDownloadFailed() {
                Log.d(TAG, "onVideoDownloadFailed");
            }

            @Override
            public void onLpClosed() {
                Log.d(TAG, "onLpClosed");
            }
        });
    }

    private void createAd32050(Context context) {
        String title = nativeAd.getTitle();
        String desc = nativeAd.getDesc();
        if (null == title) title = desc;
        if (null == desc ) desc = title;
        if ((null != title && null != desc) && (title.length() > desc.length())){
            String temp = desc;
            desc = title;
            title = desc;
        }

        mNBView = new RelativeLayout(context);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(dip2px(context, 320.0f), dip2px(context, 50.0f));
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        mNBView.setLayoutParams(lp);
        ShapeDrawable rectShapeDrawable = new ShapeDrawable();
        Paint paint = rectShapeDrawable.getPaint();
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        mNBView.setBackground(rectShapeDrawable);

        ImageView imageView = new ImageView(context);
        imageView.setId(ID_ADICON_IMAGE);
        RelativeLayout.LayoutParams imageViewLayout = new RelativeLayout.LayoutParams(dip2px(context, 75.0f), dip2px(context, 49.0f));
        imageViewLayout.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);//CENTER_CROP
        imageViewLayout.setMargins(dip2px(context, 1.0f), dip2px(context, 0.5f), dip2px(context, 243.0f), dip2px(context, 0.5f));
        mNBView.addView(imageView, imageViewLayout);
        AQuery aq = new AQuery(imageView);
        if (null == nativeAd.getIconUrl()){
            aq.id(ID_ADICON_IMAGE).image(nativeAd.getImageUrl(), false, true);
        }else{
            aq.id(ID_ADICON_IMAGE).image(nativeAd.getIconUrl(), false, true);
        }

        TextView textView = new TextView(context);
        textView.setTextSize(15);
        textView.setLines(1);
        textView.setTextColor(Color.BLACK);
        String txt = title;
        textView.setText(txt);
        textView.setGravity(Gravity.LEFT);
        //给TextView添加位置布局
        RelativeLayout.LayoutParams textLayout = new RelativeLayout.LayoutParams(dip2px(context, 160.0f), dip2px(context, 20.0f));
        textLayout.addRule(RelativeLayout.LEFT_OF);
        textLayout.setMargins(dip2px(context, 80.0f), dip2px(context, 5.0f), dip2px(context, 80.0f), dip2px(context, 25.0f));
        //加入到RelativeLayout的布局里
        mNBView.addView(textView, textLayout);

        TextView desView = new TextView(context);
        desView.setTextSize(10);
        desView.setLines(1);
        desView.setTextColor(Color.GRAY);
        String des = desc;
        desView.setText(des);
        desView.setGravity(Gravity.LEFT);
        //给TextView添加位置布局
        RelativeLayout.LayoutParams desLayout = new RelativeLayout.LayoutParams(dip2px(context, 160.0f), dip2px(context, 15.0f));
        desLayout.addRule(RelativeLayout.LEFT_OF);
        desLayout.setMargins(dip2px(context, 80.0f), dip2px(context, 30.0f), dip2px(context, 80.0f), dip2px(context, 5.0f));
        //加入到RelativeLayout的布局里
        mNBView.addView(desView, desLayout);

        Button btn = new Button(context);
        btn.setText("立即查看");
        btn.setTextSize(11);
        RelativeLayout.LayoutParams btnLayout = new RelativeLayout.LayoutParams(dip2px(context, 70.0f), dip2px(context, 40.0f));
        btnLayout.addRule(RelativeLayout.LEFT_OF);
        btnLayout.setMargins(dip2px(context, 245.0f), dip2px(context, 5.0f), dip2px(context, 5.0f), dip2px(context, 5.0f));
        //加入到RelativeLayout的布局里
        mNBView.addView(btn, btnLayout);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "nativeAd.handleClick");
                nativeAd.handleClick(v);
            }
        });



    }
    private void renderAd(Context context) {
        if (is320_50) {
            createAd32050(context);
        }else if (is300_250){
            mNBView = new RelativeLayout(context);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(dip2px(context, 300.0f), dip2px(context, 250.0f));
            lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            mNBView.setLayoutParams(lp);
            ShapeDrawable rectShapeDrawable = new ShapeDrawable();
            Paint paint = rectShapeDrawable.getPaint();
            paint.setColor(Color.GRAY);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1);
            mNBView.setBackground(rectShapeDrawable);

            ImageView imageView = new ImageView(context);
            imageView.setId(ID_LARGE_IMAGE);
            RelativeLayout.LayoutParams imageViewLayout = new RelativeLayout.LayoutParams(dip2px(context, 300.0f), dip2px(context, 200.0f));
            imageViewLayout.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);//CENTER_CROP
            imageViewLayout.setMargins(dip2px(context, 1.0f), dip2px(context, 1.0f), dip2px(context, 1.0f), dip2px(context, 1.0f));
            mNBView.addView(imageView, imageViewLayout);
            AQuery aq = new AQuery(imageView);
            aq.id(ID_LARGE_IMAGE).image(nativeAd.getImageUrl(), false, true);

            TextView textView = new TextView(context);
            textView.setTextSize(16);
            textView.setLines(1);
            textView.setTextColor(Color.GRAY);
            String txt = nativeAd.getTitle();
            textView.setText(txt);
            textView.setGravity(Gravity.LEFT);
            //给TextView添加位置布局
            RelativeLayout.LayoutParams textLayout = new RelativeLayout.LayoutParams(dip2px(context, 276), dip2px(context, 40.0f));
            textLayout.addRule(RelativeLayout.BELOW, ID_LARGE_IMAGE);
            textLayout.addRule(RelativeLayout.ALIGN_LEFT, ID_LARGE_IMAGE);
            textLayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
            textLayout.setMargins(dip2px(context, 5.0f), dip2px(context, 10.0f), dip2px(context, 20.0f), dip2px(context, 0.0f));
            //加入到RelativeLayout的布局里
            mNBView.addView(textView, textLayout);

            ImageView adLogo = new ImageView(context);
            adLogo.setId(ID_ADLOGO_IMAGE);
            RelativeLayout.LayoutParams logoViewLayout = new RelativeLayout.LayoutParams(dip2px(context, 16), dip2px(context, 16));
            logoViewLayout.addRule(RelativeLayout.ALIGN_RIGHT, ID_LARGE_IMAGE);
            logoViewLayout.addRule(RelativeLayout.BELOW, ID_LARGE_IMAGE);
            logoViewLayout.setMargins(dip2px(context, 283.5f), dip2px(context, 34.0f), dip2px(context, 0.5f), dip2px(context, 0.0f));
            mNBView.addView(adLogo, logoViewLayout);
            AQuery aqLogo = new AQuery(adLogo);
            aqLogo.id(ID_ADLOGO_IMAGE).image(nativeAd.getBaiduLogoUrl(), false, true);
        }else {
            return;
        }
        mNBView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "nativeAd.handleClick");
                nativeAd.handleClick(v);
            }
        });

        nativeAd.registerViewForInteraction(mNBView, new NativeResponse.AdInteractionListener() {
            @Override
            public void onAdClick() {
                Log.d(TAG, "onAdClick");
                if (mImpressionEventListener != null) {
                    mImpressionEventListener.onBannerAdClicked();
                }
            }

            @Override
            public void onADExposed() {
                Log.d(TAG, "onADExposed");
                if (null != nativeAd) {
                    nativeAd.recordImpression(mNBView);
                }

                if (mImpressionEventListener != null) {
                    mImpressionEventListener.onBannerAdShow();
                }

            }


            @Override
            public void onADExposureFailed(int i) {

            }

            @Override
            public void onADStatusChanged() {
                Log.d(TAG, "onADStatusChanged");
            }

            @Override
            public void onAdUnionClick() {

            }
        });

        nativeAd.setAdPrivacyListener(new NativeResponse.AdPrivacyListener() {
            @Override
            public void onADPrivacyClick() {
                Log.d(TAG, "onADPrivacyClick");
            }

            @Override
            public void onADPermissionShow() {
                Log.d(TAG, "onADPermissionShow");
            }

            @Override
            public void onADPermissionClose() {
                Log.d(TAG, "onADPermissionClose");
            }
        });
    }

    //生成ID
    public static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public View getBannerView() {
        return mNBView;
    }

    @Override
    public String getNetworkName() {
        return "Baidu Custom";
    }


    @Override
    public void destory() {
       if (null != mBaiduNativeManager) {
           mBaiduNativeManager = null;
       }

       if (null != nativeAd){
           nativeAd = null;
       }

       if (null != mNBView){
           mNBView = null;
       }

    }

    @Override
    public String getNetworkPlacementId() {
        return slotId;
    }

    @Override
    public String getNetworkSDKVersion() {
        return BaiduATConst.getNetworkVersion();
    }
}
