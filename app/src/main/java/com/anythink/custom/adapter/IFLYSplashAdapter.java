package com.anythink.custom.adapter;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;

import com.adsgreat.base.config.Const;
import com.anythink.splashad.unitgroup.api.CustomSplashAdapter;
import com.bun.miitmdid.core.MdidSdkHelper;
import com.bun.miitmdid.interfaces.IIdentifierListener;
import com.bun.miitmdid.interfaces.IdSupplier;
import com.shu.priory.IFLYAdSDK;
import com.shu.priory.IFLYSplashAd;
import com.shu.priory.config.AdError;
import com.shu.priory.config.AdKeys;
import com.shu.priory.listener.IFLYSplashListener;

import java.util.Map;

public class IFLYSplashAdapter extends CustomSplashAdapter {
    private final String TAG = "IFLYSplashAdapter:";
    private IFLYSplashAd mSplashAd;
    String slotId = null;
    private int countDown = 5;

    @Override
    public void loadCustomNetworkAd(final Context context, Map<String, Object> serverExtra, Map<String, Object> localExtra) {
        String appId = (String) serverExtra.get("app_id");
        slotId = (String) serverExtra.get("slot_id");
        String appName = (String) serverExtra.get("app_name");
        String cd = (String) serverExtra.get("count_down");
       if (null != cd && !cd.isEmpty()) {
           countDown = Integer.parseInt(cd);
       }
        //检测传入参数
        if (TextUtils.isEmpty(appId) || TextUtils.isEmpty(slotId)) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError(TAG, "app_id or slot_id is empty!");
            }
            return;
        }
        IFLYAdSDK.init(context);
        startLoad(context,slotId);
    }

    private void startLoad(Context context,String slotID) {
        mSplashAd = new IFLYSplashAd(context, slotID, new IFLYSplashListener() {
            @Override
            public void onConfirm() {
                Log.d(TAG, "onConfirm.");
            }


            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel.");
            }

            @Override
            public void onAdExposure() {
                Log.d(TAG, "onAdExposure.");
                if (mImpressionListener != null) {
                    mImpressionListener.onSplashAdShow();
                }
            }

            @Override
            public void onAdSkip() {
                Log.d(TAG, "onAdSkip.");
                if (mImpressionListener != null) {
                    mImpressionListener.onSplashAdDismiss();
                }
            }

            @Override
            public void onAdClick() {
                Log.d(TAG, "onAdClick.");
                if (mImpressionListener != null) {
                    mImpressionListener.onSplashAdClicked();
                }
            }

            @Override
            public void onAdTimeOver() {
                Log.d(TAG, "onAdTimeOver.");
                if (mImpressionListener != null) {
                    mImpressionListener.onSplashAdDismiss();
                }
            }

            @Override
            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded.");
                if (mLoadListener != null) {
                    mLoadListener.onAdCacheLoaded();
                }
            }

            @Override
            public void onAdFailed(AdError error) {
                Log.d(TAG, "onAdFailed."+ error.getErrorDescription());
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError(TAG,error.getErrorDescription());
                }
            }
        });

        getOaidAndLoadAd(context);

    }

    @Override
    public String getNetworkName() {
        return "IFLY Custom";
    }

    @Override
    public boolean isAdReady() {
        return mSplashAd != null;
    }

    @Override
    public void destory() {
        mSplashAd = null;

    }

    @Override
    public String getNetworkPlacementId() {
        return slotId;
    }

    @Override
    public String getNetworkSDKVersion() {
        return Const.getVersionNumber();
    }

    @Override
    public void show(Activity activity, ViewGroup viewGroup) {
        Log.d(TAG, "show.");
        mSplashAd.showAd(viewGroup);
    }
    public void getOaidAndLoadAd (Context context) {
        Application application =  (Application)context.getApplicationContext();
        MdidSdkHelper.InitSdk(application, true, new IIdentifierListener() {
            @Override
            public void OnSupport(boolean b, final IdSupplier idSupplier) {
                if (idSupplier != null && idSupplier.isSupported()) {
                    String oaidStr = idSupplier.getOAID();
                    mSplashAd.setParameter(AdKeys.OAID, oaidStr);
                    mSplashAd.setParameter(AdKeys.COUNT_DOWN, countDown);
                    mSplashAd.setParameter(AdKeys.DEBUG_MODE, false);
                    mSplashAd.loadAd();
                }
            }
        });
    }
}
