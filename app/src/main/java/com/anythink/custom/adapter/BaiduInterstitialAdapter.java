package com.anythink.custom.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.anythink.interstitial.unitgroup.api.CustomInterstitialAdapter;
import com.baidu.mobads.sdk.api.FullScreenVideoAd;

import java.util.Map;

public class BaiduInterstitialAdapter extends CustomInterstitialAdapter {
    private String slotId;
    public FullScreenVideoAd mFullScreenVideoAd;
    private boolean adIsReady;
    private boolean isDestroyed;
    private String TAG = "BaiduInterstitialAdapter:";
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
            mFullScreenVideoAd.show();
        }
    }

    @Override
    public String getNetworkName() {
        return "JD Custom";
    }

    @Override
    public void loadCustomNetworkAd(final Context context, Map<String, Object> serverExtra, final Map<String, Object> localExtra) {
        adIsReady = false;
        String appId = (String) serverExtra.get("app_id");
        slotId = (String) serverExtra.get("slot_id");
        String appName = (String) serverExtra.get("app_name");


        //检测传入参数
        if (TextUtils.isEmpty(appId) || TextUtils.isEmpty(slotId)) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError(TAG, "app_id or slot_id is empty!");
            }
            return;
        }


        // 全屏视频产品可以选择是否使用SurfaceView进行渲染视频
        mFullScreenVideoAd = new FullScreenVideoAd(context,
                slotId, new FullScreenVideoAd.FullScreenVideoAdListener(){

            @Override
            public void onAdShow() {
                Log.d(TAG, "onAdShow.");
                if (mImpressListener != null) {
                    mImpressListener.onInterstitialAdShow();
                }
            }

            @Override
            public void onAdClick() {
                Log.d(TAG, "onAdClicked.");
                if (mImpressListener != null) {
                    mImpressListener.onInterstitialAdClicked();
                }
            }

            @Override
            public void onAdClose(float v) {
                Log.d(TAG, "onAdClose.");
                if (mImpressListener != null) {
                    mImpressListener.onInterstitialAdClose();
                }
            }

            @Override
            public void onAdFailed(String s) {
                Log.d(TAG, "onAdFailed.");
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError(TAG,s);
                }
            }

            @Override
            public void onVideoDownloadSuccess() {
                Log.d(TAG, "onVideoDownloadSuccess.");
                adIsReady = true;
            }

            @Override
            public void onVideoDownloadFailed() {
                Log.d(TAG, "onVideoDownloadFailed.");
                if (mImpressListener != null) {
                    mImpressListener.onInterstitialAdVideoError(TAG,"video load error!");
                }
            }

            @Override
            public void playCompletion() {
                Log.d(TAG, "playCompletion.");
                if (mImpressListener != null) {
                    mImpressListener.onInterstitialAdVideoEnd();
                }
            }

            @Override
            public void onAdSkip(float v) {
                Log.d(TAG, "onAdSkip.");
                if (mImpressListener != null) {
                    mImpressListener.onInterstitialAdClose();
                }
            }

            @Override
            public void onAdLoaded() {
                Log.d(TAG, "onAdLoadSuccess.");
                if (mLoadListener != null) {
                    mLoadListener.onAdCacheLoaded();
                }
            }
        });

        if (null != mFullScreenVideoAd){
            mFullScreenVideoAd.setAppSid(appId);
            mFullScreenVideoAd.load();
        }

    }

    @Override
    public void destory() {
        isDestroyed = true;
        mFullScreenVideoAd = null;
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
