/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.anythink.custom.adapter;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.adsgreat.base.config.Const;
import com.anythink.banner.unitgroup.api.CustomBannerAdapter;
import com.bun.miitmdid.core.MdidSdkHelper;
import com.bun.miitmdid.interfaces.IIdentifierListener;
import com.bun.miitmdid.interfaces.IdSupplier;
import com.shu.priory.IFLYAdSDK;
import com.shu.priory.IFLYBannerAd;
import com.shu.priory.config.AdError;
import com.shu.priory.config.AdKeys;
import com.shu.priory.listener.IFLYAdListener;

import java.util.Map;

public class IFLYBannerAdapter extends CustomBannerAdapter {
    private final String TAG = "IFLYBannerAdapter:";
    private IFLYBannerAd mBannerView;
    private String slotId = "";
    private IFLYAdListener mAdListener = null;


    @Override
    public View getBannerView() {
        return mBannerView;
    }

    @Override
    public String getNetworkName() {
        return "IFLY Custom";
    }

    @Override
    public void loadCustomNetworkAd(final Context context, final Map<String, Object> serverExtra, Map<String, Object> localExtra) {
        String appId = (String) serverExtra.get("app_id");
        slotId = (String) serverExtra.get("slot_id");
        if (TextUtils.isEmpty(appId) || TextUtils.isEmpty(slotId)) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError("", "app_id or slot_id is empty!");
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
        IFLYAdSDK.init(context);
        //创建横幅广告，传入广告位ID
        mBannerView = IFLYBannerAd.createBannerAd(context, slotId);
        mATBannerView.removeAllViews();
        mATBannerView.addView(mBannerView);
        setAdListener();
        getOaidAndLoadAd(context);
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

    public void getOaidAndLoadAd (Context context) {
        Application application =  (Application)context.getApplicationContext();
        MdidSdkHelper.InitSdk(application, true, new IIdentifierListener() {
            @Override
            public void OnSupport(boolean b, final IdSupplier idSupplier) {
                if (idSupplier != null && idSupplier.isSupported()) {
                    String oaidStr = idSupplier.getOAID();
                    mBannerView.setParameter(AdKeys.OAID, oaidStr);
                    mBannerView.setParameter(AdKeys.DEBUG_MODE, true);
                    mBannerView.loadAd(mAdListener);
                }
            }
        });
    }

    public void setAdListener() {
        mAdListener = new IFLYAdListener() {
            @Override
            public void onAdReceive() {
                //展示广告
                Log.d(TAG, "onAdClick");
                mBannerView.showAd();
                if (mLoadListener != null) {
                    mLoadListener.onAdCacheLoaded();
                }
            }

            @Override
            public void onAdFailed(AdError error) {
                //获取广告失败
                Log.d(TAG, "onAdClick");
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError(TAG, error.getErrorDescription());
                }
            }

            @Override
            public void onAdClick() {
                Log.d(TAG, "onAdClick");
                if (mImpressionEventListener != null) {
                    mImpressionEventListener.onBannerAdClicked();
                }
            }

            @Override
            public void onAdClose() {
                Log.d(TAG, "onAdClose");
                if (mImpressionEventListener != null) {
                    mImpressionEventListener.onBannerAdClose();
                }
            }

            @Override
            public void onAdExposure() {
                Log.d(TAG, "onAdExposure");
                if (mImpressionEventListener != null) {
                    mImpressionEventListener.onBannerAdShow();
                }
            }

            @Override
            public void onConfirm() {
                Log.d(TAG, "onConfirm");
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel");
            }

        };
    }
}
