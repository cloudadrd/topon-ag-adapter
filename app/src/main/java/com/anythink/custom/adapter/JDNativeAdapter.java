package com.anythink.custom.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.anythink.nativead.unitgroup.api.CustomNativeAdapter;
import com.jd.ad.sdk.JadYunSdk;
import com.jd.ad.sdk.imp.JadListener;
import com.jd.ad.sdk.imp.feed.FeedAd;
import com.jd.ad.sdk.work.JadPlacementParams;

import java.util.Map;

public class JDNativeAdapter extends CustomNativeAdapter {

    private final String TAG = getClass().getSimpleName();
    private String slotId;

    private JDNativeAd jdNativeAd;

    private FeedAd feedAd;

    private int width = 0;
    private int height = 0;
    private String appId;

    @Override
    public void loadCustomNetworkAd(Context context, Map<String, Object> serverExtra, Map<String, Object> localExtra) {
        appId = (String) serverExtra.get("app_id");
        slotId = (String) serverExtra.get("slot_id");

        if (TextUtils.isEmpty(appId) || TextUtils.isEmpty(slotId)) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError(TAG, "app_id or slot_id is empty!");
            }
            return;
        }
        JDUtils.JDSDKInit(appId, context);
        try {
            if (localExtra.containsKey("jdad_width")) {
                width = Integer.parseInt(localExtra.get("jdad_width").toString());
            } else if (localExtra.containsKey("key_width")) {
                width = Integer.parseInt(localExtra.get("key_width").toString());
            }

            if (localExtra.containsKey("jdad_height")) {
                height = Integer.parseInt(localExtra.get("jdad_height").toString());
            } else if (localExtra.containsKey("key_height")) {
                height = Integer.parseInt(localExtra.get("key_height").toString());
            }
            width = JDUtils.px2dip(context,width);
            height = JDUtils.px2dip(context,height);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (context instanceof Activity) {
            loadAd((Activity) context);
        } else {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError(TAG, "context class type is not Activity...");
            }
        }
    }

    private void loadAd(Activity activity) {
        Log.d(TAG, "request width=" + width + ",height=" + height + ",appid=" + appId + ",slotId=" + slotId);
        JadPlacementParams params = new JadPlacementParams.Builder()
                .setPlacementId(slotId)//代码位ID
                .setSize(width, height)//期望个性化模板广告view的size,单位dp，注意这里要保证传入尺寸符合申请的模版要求的比例
                .setSupportDeepLink(true)// true: 支持deeplink；  false：不支持deeplink
                .setCloseHide(true)//true:隐藏关闭按钮  false:显示关闭按钮
                .build();


        feedAd = new FeedAd(activity, params, new JadListener() {
            /**
             * 加载成功
             */
            @Override
            public void onAdLoadSuccess() {
                Log.d(TAG, "FeedAd Load Success");
                if (mLoadListener != null) {
                    mLoadListener.onAdDataLoaded();
                }
            }

            /**
             * 加载失败
             * @param error error message
             */
            @Override
            public void onAdLoadFailed(int code, String error) {
                Log.d(TAG, "FeedAd Load Failed");
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError(TAG, "code=" + code + ", error=" + error);
                }
            }

            /**
             * 渲染成功
             * @param view ad view
             */
            @Override
            public void onAdRenderSuccess(View view) {
                Log.d(TAG, "FeedAd Render Success");
                if (mLoadListener != null) {
                    jdNativeAd = new JDNativeAd(view);
                    mLoadListener.onAdCacheLoaded(jdNativeAd);
                }
            }

            /**
             * 渲染失败
             * @param error error message
             */
            @Override
            public void onAdRenderFailed(int code, String error) {
                Log.d(TAG, "FeedAd Render Failed");
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError(TAG, "code=" + code + ", error=" + error);
                }
            }

            /**
             * 点击
             */
            @Override
            public void onAdClicked() {
                Log.d(TAG, "FeedAd Clicked");
                if (jdNativeAd != null) {
                    jdNativeAd.onAdClicked();
                }
            }

            /**
             * 曝光
             */
            @Override
            public void onAdExposure() {
                Log.d(TAG, "FeedAd Exposure Success");
                if (jdNativeAd != null) {
                    jdNativeAd.onAdExposure();
                }
            }

            /**
             * 关闭
             */
            @Override
            public void onAdDismissed() {
                Log.d(TAG, "FeedAd Dismissed");
                if (jdNativeAd != null) {
                    jdNativeAd.onAdDismissed();
                }
            }
        });
        feedAd.loadAd();
    }

    @Override
    public void destory() {
        if (feedAd != null)
            feedAd.destroy();
    }

    @Override
    public String getNetworkPlacementId() {
        return slotId;
    }

    @Override
    public String getNetworkSDKVersion() {
        return JadYunSdk.getSDKVersion();
    }

    @Override
    public String getNetworkName() {
        return "JingDong Custom";
    }
}
