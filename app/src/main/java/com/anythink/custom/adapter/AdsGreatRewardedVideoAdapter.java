package com.anythink.custom.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.adsgreat.base.callback.VideoAdLoadListener;
import com.adsgreat.base.core.AGError;
import com.adsgreat.base.core.AGNative;
import com.adsgreat.base.core.AGVideo;
import com.adsgreat.base.core.AdsgreatSDK;
import com.adsgreat.base.core.AdsgreatSDKInternal;
import com.adsgreat.video.core.AdsGreatVideo;
import com.adsgreat.video.core.RewardedVideoAdListener;
import com.anythink.rewardvideo.unitgroup.api.CustomRewardVideoAdapter;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class AdsGreatRewardedVideoAdapter extends CustomRewardVideoAdapter {


    private static String TAG = "OM-Cloudmobi-Plugin1: ";

    String slotId = "";

    private AGVideo mRvAd;

    private AtomicBoolean isPreload = new AtomicBoolean();

    private AGNative agnv = null;

    private boolean isDestroyed = false;

    public class VideoAdListenerImpl extends RewardedVideoAdListener {

        @Override
        public void videoStart() {
            Log.d(TAG, "videoStart: ");
            if (mImpressionListener != null) {
                mImpressionListener.onRewardedVideoAdPlayStart();
            }
        }

        @Override
        public void videoFinish() {
            Log.d(TAG, "videoFinish: ");
            if (mImpressionListener != null) {
                mImpressionListener.onRewardedVideoAdPlayEnd();
            }
        }

        @Override
        public void videoError(Exception e) {
            Log.d(TAG, "onAdFailed: " + e.getMessage());
            if (mImpressionListener != null) {
                mImpressionListener.onRewardedVideoAdPlayFailed(TAG, "RewardedVideo load failed : " + e.getMessage());
            }
        }

        @Override
        public void videoClosed() {
            Log.d(TAG, "videoClosed: ");
            if (mImpressionListener != null) {
                mImpressionListener.onRewardedVideoAdClosed();
            }
        }

        @Override
        public void videoClicked() {
            Log.d(TAG, "videoClicked: ");
            if (mImpressionListener != null) {
                mImpressionListener.onRewardedVideoAdPlayClicked();
            }
        }

        @Override
        public void videoRewarded(String rewardName, String rewardAmount) {
            Log.d(TAG, "videoRewarded: rewardName=" + rewardName + ",rewardAmount=" + rewardAmount);
            if (mImpressionListener != null) {
                mImpressionListener.onReward();
            }

        }
    }

    private void loadRvAd(Context activity, String adUnitId) {
        if (!isPreload.compareAndSet(false, true)) {
            mLoadListener.onAdLoadError(TAG, "ad loading, no need to load repeatedly");
            return;
        }
        AGVideo zcVideo = mRvAd;
        if (zcVideo == null) {
            realLoadRvAd(activity, adUnitId);
        } else {
            if (mLoadListener != null) {
                mLoadListener.onAdDataLoaded();
                mLoadListener.onAdCacheLoaded();
            }
        }
    }

    private VideoAdLoadListener create(final String adUnitId) {

        return new VideoAdLoadListener() {

            @Override
            public void onVideoAdLoadSucceed(AGVideo zcVideo) {
                Log.d(TAG, "onVideoAdLoadSucceed: ");
                mRvAd = zcVideo;
                if (mLoadListener != null) {
                    mLoadListener.onAdDataLoaded();
                    mLoadListener.onAdCacheLoaded();
                }
            }

            @Override
            public void onVideoAdLoadFailed(AGError zcError) {
                isPreload.set(false);
                String message = "";
                if (zcError != null) {
                    message = zcError.getMsg();
                }
                Log.d(TAG, "onAdFailed: " + message);
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError(TAG, "RewardedVideo load failed : " + message);
                }
            }
        };
    }

    private void realLoadRvAd(Context activity, final String adUnitId) {
        VideoAdLoadListener videoAdLoadListener = create(adUnitId);
        AdsGreatVideo.preloadRewardedVideo(activity, adUnitId, videoAdLoadListener);
    }


    @Override
    public boolean isAdReady() {
        AGVideo video = mRvAd;
        if (video == null) return false;

        if (!AdsGreatVideo.isRewardedVideoAvailable(video)) {
            Log.d(TAG, "onAdFailed: mp4 creative error");
            isPreload.set(false);
            mRvAd = null;
            if (mImpressionListener != null) {
                mImpressionListener.onRewardedVideoAdPlayFailed(TAG, "onAdFailed: mp4 creative error");
            }
        }
        return AdsGreatVideo.isRewardedVideoAvailable(video);
    }

    @Override
    public void show(Activity activity) {
        AGVideo agVideo = mRvAd;
        if (activity != null && agVideo != null) {
            AdsGreatVideo.showRewardedVideo(agVideo, new VideoAdListenerImpl());
            isPreload.set(false);
            mRvAd = null;
        } else {
            if (mImpressionListener != null) {
                mImpressionListener.onRewardedVideoAdPlayFailed(TAG, "RewardedVideo is not ready");
            }
        }
    }

    @Override
    public String getNetworkName() {
        return PangleInitManager.getInstance().getNetworkName();
    }

    @Override
    public void loadCustomNetworkAd(final Context context, Map<String, Object> serverExtra, final Map<String, Object> localExtra) {
        String appId = (String) serverExtra.get("app_id");
            slotId = (String) serverExtra.get("slot_id");

        if (TextUtils.isEmpty(appId) || TextUtils.isEmpty(slotId)) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError("", "app_id or slot_id is empty!");
            }
            return;
        }
        Object customIdObj = serverExtra.get("custom_id");
        if (customIdObj instanceof String) {
            String customId = (String) customIdObj;
            if (!TextUtils.isEmpty(customId)) {
                AdsgreatSDKInternal.setUserId(customId);
            }
        }
        AdsgreatSDK.setSchema(true);
        AdsgreatSDK.initialize(context, appId);
        loadRvAd(context, slotId);
    }

    @Override
    public void destory() {

    }

    @Override
    public String getNetworkPlacementId() {
        return slotId;
    }

    @Override
    public String getNetworkSDKVersion() {
        return PangleInitManager.getInstance().getNetworkVersion();
    }

    private static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / (scale <= 0 ? 1 : scale) + 0.5f);
    }


}
