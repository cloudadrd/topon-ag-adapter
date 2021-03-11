package com.anythink.custom.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.anythink.nativead.unitgroup.api.CustomNativeAdapter;
import com.anythink.network.baidu.BaiduATConst;
import com.anythink.network.baidu.BaiduATInitManager;
import com.baidu.mobad.feeds.BaiduNativeManager;
import com.baidu.mobad.feeds.NativeErrorCode;
import com.baidu.mobad.feeds.NativeResponse;
import com.baidu.mobad.feeds.RequestParameters;
import com.baidu.mobad.feeds.XAdNativeResponse;
import com.baidu.mobads.component.FeedNativeView;
import com.baidu.mobads.component.StyleParams;
import com.baidu.mobads.rewardvideo.RewardVideoAd;
import com.jd.ad.sdk.JadYunSdk;
import com.jd.ad.sdk.imp.JadListener;
import com.jd.ad.sdk.imp.feed.FeedAd;
import com.jd.ad.sdk.work.JadPlacementParams;

import java.util.List;
import java.util.Map;

public class BaiduNativeAdapter extends CustomNativeAdapter {

    private final String TAG = getClass().getSimpleName();
    private String slotId;

    private BaiduNativeManager mBaiduNativeManager;
    //    private View mNativeView;
    private NativeResponse nativeAd;

    private BaiduNativeAd baiduNativeAd;

    private int width = 0;
    private int height = 0;

    @Override
    public void loadCustomNetworkAd(Context context, Map<String, Object> serverExtra, Map<String, Object> localExtra) {
        String appId = (String) serverExtra.get("app_id");
        slotId = (String) serverExtra.get("slot_id");

        if (TextUtils.isEmpty(appId) || TextUtils.isEmpty(slotId)) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError(TAG, "app_id or slot_id is empty!");
            }
            return;
        }
        try {
            if (localExtra.containsKey("bdad_width")) {
                width = Integer.parseInt(localExtra.get("bdad_width").toString());
            } else if (localExtra.containsKey("key_width")) {
                width = Integer.parseInt(localExtra.get("key_width").toString());
            }

            if (localExtra.containsKey("bdad_height")) {
                height = Integer.parseInt(localExtra.get("bdad_height").toString());
            } else if (localExtra.containsKey("key_height")) {
                height = Integer.parseInt(localExtra.get("key_height").toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        RewardVideoAd.setAppSid(appId);
        Log.d(TAG, "setAppsID " + appId);
        if (mBaiduNativeManager == null) {
            Log.d(TAG, "insk = " + slotId);
            mBaiduNativeManager = new BaiduNativeManager(context.getApplicationContext(), slotId);//mInstancesKey
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
        mBaiduNativeManager.loadFeedAd(requestParameters, new BaiduNativeManager.NativeLoadListener() {
            @Override
            public void onNativeLoad(List<NativeResponse> nativeResponses) {
                Log.d(TAG, "onNativeLoad");
                if (nativeResponses != null && nativeResponses.size() > 0) {
                    nativeAd = nativeResponses.get(0);
                    if (mLoadListener != null) {
                        mLoadListener.onAdDataLoaded();
                        baiduNativeAd = renderAd(context);
                        mLoadListener.onAdCacheLoaded(baiduNativeAd);
                    }
                } else {
                    if (mLoadListener != null) {
                        mLoadListener.onAdLoadError(TAG, "no ad fill");
                    }
                }
            }

            @Override
            public void onLoadFail(String message, String errorCode) {
                Log.d(TAG, "onLoadFail reason:" + message + "errorCode:" + errorCode);
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError(TAG, "code=" + errorCode + ", error=" + message);
                }
            }

            @Override
            public void onNativeFail(NativeErrorCode errorCode) {
                // 建议使用onLoadFail回调获取详细的请求失败的原因
                Log.d(TAG, "onNativeFail reason:" + errorCode.name());
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
//                if (baiduNativeAd != null) {
//                    baiduNativeAd.onAdDismissed();
//                }
            }
        });
    }

    private BaiduNativeAd renderAd(Context context) {
        FeedNativeView newAdView = new FeedNativeView(context);
        newAdView.setAdData((XAdNativeResponse) nativeAd);
        StyleParams params = new StyleParams.Builder()
                .build();
        newAdView.changeViewLayoutParams(params);

        return new BaiduNativeAd(newAdView, nativeAd);
    }

    @Override
    public void destory() {
        nativeAd = null;
        if (baiduNativeAd != null)
            baiduNativeAd.destroy();
        mBaiduNativeManager = null;
    }

    @Override
    public String getNetworkPlacementId() {
        return slotId;
    }

    @Override
    public String getNetworkSDKVersion() {
        return BaiduATConst.getNetworkVersion();
    }

    @Override
    public String getNetworkName() {
        return "Baidu Custom";
    }
}
