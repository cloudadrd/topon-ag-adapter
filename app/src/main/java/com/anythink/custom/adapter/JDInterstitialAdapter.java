package com.anythink.custom.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.anythink.interstitial.unitgroup.api.CustomInterstitialAdapter;
import com.jd.ad.sdk.imp.JadListener;
import com.jd.ad.sdk.imp.interstitial.InterstitialAd;
import com.jd.ad.sdk.work.JadPlacementParams;

import java.util.Map;

public class JDInterstitialAdapter extends CustomInterstitialAdapter {
    private int adWidth;
    private int adHeight;
    private String slotId;
    private InterstitialAd interstitialAd;
    private boolean adIsReady;
    private boolean isDestroyed;
    private String TAG = "JDIntersititialAdapter:";
    @Override
    public boolean isAdReady() {
        if (isDestroyed) {
            return false;
        }
        return adIsReady;
    }

    @Override
    public void show(Activity activity) {
        if (isDestroyed) {
            return;
        }

        if (isAdReady()){
            interstitialAd.showAd(null);
        }
    }

    @Override
    public String getNetworkName() {
        return "JD Custom";
    }

    @Override
    public void loadCustomNetworkAd(final Context context, Map<String, Object> serverExtra, final Map<String, Object> localExtra) {
        adIsReady = false;
        adWidth = 1;
        adHeight = 100;

        String appId = (String) serverExtra.get("app_id");
        slotId = (String) serverExtra.get("slot_id");
        String appName = (String) serverExtra.get("app_name");
        String w = (String) serverExtra.get("width");
        if (null != w && !w.isEmpty()) adWidth = Integer.parseInt(w);
        String h = (String) serverExtra.get("height");
        if (null != h && !h.isEmpty()) adHeight = Integer.parseInt(h);

        adWidth = JDUtils.px2dip(context,adWidth);
        adHeight = JDUtils.px2dip(context,adHeight);

        //检测传入参数
        if (TextUtils.isEmpty(appId) || TextUtils.isEmpty(slotId)) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError(TAG, "app_id or slot_id is empty!");
            }
            return;
        }

        JDUtils.JDSDKInit(appId,context);
        JadPlacementParams jadSlot = new JadPlacementParams.Builder()
                .setPlacementId(slotId)
                .setSize(adWidth, adHeight)
                .setSupportDeepLink(false)
                .build();
        Activity activity = (Activity) context;
        interstitialAd = new InterstitialAd(activity, jadSlot, new JadListener() {

            @Override
            public void onAdLoadSuccess() {
                Log.d(TAG, "onAdLoadSuccess.");
                if (mLoadListener != null) {
                    mLoadListener.onAdCacheLoaded();
                }

            }

            @Override
            public void onAdLoadFailed(int code, String error) {
                Log.d(TAG, "onAdLoadFailed.");
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError(TAG,error);
                }

            }

            @Override
            public void onAdRenderSuccess(View view) {
                //Step4: 在render成功之后调用show方法来展示广告
                Log.d(TAG, "onAdRenderSuccess.");
                adIsReady = true;

            }

            @Override
            public void onAdRenderFailed(int code, String error) {
                Log.d(TAG, "onAdRenderFailed.");

            }

            @Override
            public void onAdClicked() {
                Log.d(TAG, "onAdClicked.");
                if (mImpressListener != null) {
                    mImpressListener.onInterstitialAdClicked();
                }
            }

            @Override
            public void onAdExposure() {
                Log.d(TAG, "onAdExposure.");
                if (mImpressListener != null) {
                    mImpressListener.onInterstitialAdShow();
                }
            }

            @Override
            public void onAdDismissed() {
                Log.d(TAG, "onAdDismissed.");
                if (mImpressListener != null) {
                    mImpressListener.onInterstitialAdClose();
                }
            }
        });
        //Step3: 加载 InterstitialAd
        interstitialAd.loadAd();
    }

    @Override
    public void destory() {
        isDestroyed = true;
        interstitialAd = null;
        adIsReady = false;
    }

    @Override
    public String getNetworkPlacementId() {
        return slotId;
    }

    @Override
    public String getNetworkSDKVersion() {
         return  null;
    }

}
