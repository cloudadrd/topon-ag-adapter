/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.anythink.custom.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.anythink.banner.unitgroup.api.CustomBannerAdapter;
import com.growstarry.kern.callback.AdEventListener;
import com.growstarry.kern.config.Const;
import com.growstarry.kern.core.GTNative;
import com.growstarry.kern.core.GrowsTarrySDK;
import com.growstarry.kern.enums.AdSize;
import com.growstarry.kern.vo.AdsVO;

import java.util.Map;

public class GrowStarryBannerAdapter extends CustomBannerAdapter {
    private final String TAG = "GrowStarryBannerAdapter:";
    private GTNative mBannerView;
    private String slotId = "";

    @Override
    public View getBannerView() {
        return mBannerView;
    }

    @Override
    public String getNetworkName() {
        return "growstarry network";
    }

    @Override
    public void loadCustomNetworkAd(final Context context, final Map<String, Object> serverExtra, Map<String, Object> localExtra) {
        slotId = (String) serverExtra.get("slot_id");
        String sizeType = (String) serverExtra.get("size_type");
        if (TextUtils.isEmpty(slotId) || TextUtils.isEmpty(sizeType)) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError("", "slot_id or sizeType is empty!");
            }
            return;
        }

        if (!(context instanceof Activity)) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError("", "Context must be activity.");
            }
            return;
        }

        //初始化
        GrowsTarrySDK.initialize(context, slotId);
        AdSize adSize = null;
        if ("1".equals(sizeType)) {
            adSize = AdSize.AD_SIZE_320X50;
        } else if ("2".equals(sizeType)) {
            adSize = AdSize.AD_SIZE_320X100;
        } else if ("3".equals(sizeType)) {
            adSize = AdSize.AD_SIZE_300X250;
        }
        if (adSize == null) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError("", "The value of sizeType needs to be between 1 and 3");
            }
        }
        getAdBanner(context, adSize, slotId);
        //创建横幅广告，传入广告位ID
    }

    private AdEventListener mAdListener = new AdEventListener() {

        @Override
        public void onReceiveAdSucceed(GTNative gtNative) {
            if (gtNative != null) {
                mBannerView = gtNative;
                if (mLoadListener != null) {
                    mLoadListener.onAdCacheLoaded();
                }
            } else {
                mLoadListener.onAdLoadError(TAG, "onReceiveAdSucceed GTNative is null");
            }
        }

        @Override
        public void onReceiveAdVoSucceed(AdsVO adsVO) {

        }

        @Override
        public void onReceiveAdFailed(GTNative gtNative) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError(TAG, gtNative.getErrorsMsg());
            }
        }

        @Override
        public void onShowSucceed(GTNative gtNative) {
            if (mImpressionEventListener != null) {
                mImpressionEventListener.onBannerAdShow();
            }
        }

        @Override
        public void onLandPageShown(GTNative gtNative) {

        }

        @Override
        public void onAdClicked(GTNative gtNative) {
            if (mImpressionEventListener != null) {
                mImpressionEventListener.onBannerAdClicked();
            }
        }

        @Override
        public void onAdClosed(GTNative gtNative) {
            if (mImpressionEventListener != null) {
                mImpressionEventListener.onBannerAdClose();
            }
        }
    };

    private void getAdBanner(Context context, AdSize adSize, String slotId) {
        GrowsTarrySDK.getBannerAd(context, slotId, adSize, mAdListener);
    }

    @Override
    public void destory() {
        mBannerView = null;
        mAdListener = null;
    }

    @Override
    public String getNetworkPlacementId() {
        return slotId;
    }

    @Override
    public String getNetworkSDKVersion() {
        return Const.getVersionNumber();
    }

}
