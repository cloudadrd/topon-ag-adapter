package com.anythink.custom.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.adsgreat.base.callback.EmptyAdEventListener;
import com.adsgreat.base.config.Const;
import com.adsgreat.base.core.AGNative;
import com.adsgreat.base.core.AdsgreatSDK;
import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATCustomLoadListener;
import com.anythink.interstitial.unitgroup.api.CustomInterstitialAdapter;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.bytedance.sdk.openadsdk.TTInteractionAd;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

public class AdsGreatInterstitialAdapter extends CustomInterstitialAdapter {

    String slotId = null;
    private AGNative agnv = null;
    private boolean isDestroyed;
    @Override
    public boolean isAdReady() {
        if (isDestroyed) {
            return false;
        }

        return AdsgreatSDK.isInterstitialAvailable(agnv);
    }

    @Override
    public void show(Activity activity) {
        if (isDestroyed) {
            return;
        }

        if (AdsgreatSDK.isInterstitialAvailable(agnv)) {
            AdsgreatSDK.showInterstitialAd(agnv);
            if (mImpressListener != null) {
                mImpressListener.onInterstitialAdShow();
            }
        }
    }

    @Override
    public String getNetworkName() {
        return Const.getVersionNumber();
    }

    @Override
    public void loadCustomNetworkAd(final Context context, Map<String, Object> serverExtra, final Map<String, Object> localExtra) {
        slotId = (String) serverExtra.get("slot_id");
        if ( TextUtils.isEmpty(slotId)) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError("", "slot_id is empty!");
            }
            return;
        }
        AdsgreatSDK.initialize(context, slotId);
        AdsgreatSDK.preloadInterstitialAd(context, slotId, new InterstitialAdListener(){
            @Override
            public void onReceiveAdSucceed(AGNative agNative) {
                super.onReceiveAdSucceed(agNative);
                if (isDestroyed) {
                    return;
                }

                if (agNative != null && agNative.isLoaded()) {
                    agnv = agNative;
                    if (mLoadListener != null) {
                        mLoadListener.onAdDataLoaded();
                    }
                }else {
                    if (mLoadListener != null) {
                        mLoadListener.onAdLoadError("","Back parameter error.");
                    }
                }
            }

            @Override
            public void onLandPageShown(AGNative var1) {
                super.onLandPageShown(var1);
            }

            @Override
            public void onAdClicked(AGNative var1) {
                super.onAdClicked(var1);
                if (isDestroyed) {
                    return;
                }
                if (mImpressListener != null) {
                    mImpressListener.onInterstitialAdClicked();
                }
            }

            @Override
            public void onReceiveAdFailed(AGNative var1) {
                super.onReceiveAdFailed(var1);
                if (isDestroyed) {
                    return;
                }
                if (mLoadListener != null) {
                    String err = "onReceiveAdFailed";
                    if (null != var1) {
                        err = var1.getErrorsMsg();
                    }
                    mLoadListener.onAdLoadError("",err);
                }
            }


            @Override
            public void onAdClosed(AGNative var1) {
                super.onAdClosed(var1);
                if (isDestroyed) {
                    return;
                }
                if (mImpressListener != null) {
                    mImpressListener.onInterstitialAdClose();
                }
            }
        });

    }

    @Override
    public void destory() {
        isDestroyed = true;
        if (null != agnv) {
            agnv= null;
        }
    }

    @Override
    public String getNetworkPlacementId() {
        return slotId;
    }

    @Override
    public String getNetworkSDKVersion() {
        return Const.getVersionNumber();
    }

    private static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / (scale <= 0 ? 1 : scale) + 0.5f);
    }

    static class InterstitialAdListener extends EmptyAdEventListener {

        @Override
        public void onReceiveAdSucceed(AGNative agNative) {

        }

        @Override
        public void onLandPageShown(AGNative var1) {

        }

        @Override
        public void onAdClicked(AGNative var1) {

        }

        @Override
        public void onReceiveAdFailed(AGNative var1) {

        }


        @Override
        public void onAdClosed(AGNative var1) {

        }
    }
}
