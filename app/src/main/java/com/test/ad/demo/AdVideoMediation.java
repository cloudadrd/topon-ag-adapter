package com.test.ad.demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.AdError;
import com.anythink.rewardvideo.api.ATRewardVideoAd;
import com.anythink.rewardvideo.api.ATRewardVideoListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class AdVideoMediation {

    private static final String TAG = "AdVideoMediation";

    private List<AdVideoInterface> mAdVideoInterfaces = new LinkedList<>();

    private ATRewardVideoAd mRewardVideoAd;

    private boolean isLoad;

    public boolean isReadyLoad;

    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private Context mContext;

    public static final String POSID = "b5fb2228113cf7";

    private static class Holder {
        @SuppressLint("StaticFieldLeak")
        private static AdVideoMediation manager = new AdVideoMediation();
    }

    public void setContext(Context context) {
        mContext = context.getApplicationContext();
    }


    public void addAdVideoInterface(AdVideoInterface adVideoInterface) {
        if (!mAdVideoInterfaces.contains(adVideoInterface)) {
            mAdVideoInterfaces.add(adVideoInterface);
        }
    }

    public void removeAdVideoInterface(AdVideoInterface adVideoInterface) {
        mAdVideoInterfaces.remove(adVideoInterface);
    }

    public static AdVideoMediation getInstance() {
        return AdVideoMediation.Holder.manager;
    }

    private AdVideoMediation() {
    }

    public void loadVideo() {
        if (mContext == null || isLoad) {
            return;
        }
        isLoad = true;
        mRewardVideoAd = new ATRewardVideoAd(mContext, POSID);
        mRewardVideoAd.setAdListener(new ATRewardVideoListener() {

            @Override
            public void onRewardedVideoAdLoaded() {
                Log.i(TAG, "onRewardedVideoAdLoaded");
                isReadyLoad = true;
                trackState(AdLogType.LOAD_SUCCESS);
            }

            @Override
            public void onRewardedVideoAdFailed(AdError errorCode) {
                Log.i(TAG, "onRewardedVideoAdFailed error:" + errorCode.printStackTrace());
                isReadyLoad = false;
                loadDelay();
            }

            @Override
            public void onRewardedVideoAdPlayStart(ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdPlayStart:\n" + entity.toString());
                trackState(AdLogType.IMP_SUCCESS);
            }

            public void loadDelay() {
                mainHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRewardVideoAd.load();
                    }
                }, 10000);
            }

            @Override
            public void onRewardedVideoAdPlayEnd(ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdPlayEnd:\n" + entity.toString());
            }


            @Override
            public void onRewardedVideoAdPlayFailed(AdError errorCode, ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdPlayFailed error:" + errorCode.printStackTrace());
                isReadyLoad = false;
                trackState(AdLogType.PLAY_FAIL);
                loadDelay();
            }

            @Override
            public void onRewardedVideoAdClosed(ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdClosed:\n" + entity.toString());
                isReadyLoad = false;
                trackState(AdLogType.PLAY_END_CLOSE);
                mRewardVideoAd.load();
            }

            @Override
            public void onRewardedVideoAdPlayClicked(ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdPlayClicked:\n" + entity.toString());
                trackState(AdLogType.VIDEO_CLICK);
            }

            @Override
            public void onReward(ATAdInfo entity) {
                Log.e(TAG, "onReward:\n" + entity.toString());
                trackState(AdLogType.VIDEO_REWARD);
            }
        });

        mRewardVideoAd.load();
    }

    private void trackState(AdLogType adLogType) {
        for (AdVideoInterface adVideoInterface : mAdVideoInterfaces) {
            adVideoInterface.trackState(adLogType);
        }
    }

    public boolean show(Activity activity) {
        if (mRewardVideoAd.isAdReady()) {
            mRewardVideoAd.show(activity);
            Log.d(TAG, "call show, show success. isReadyLoad=" + isReadyLoad);
            return true;
        }
        Log.d(TAG, "call show, reward is not ready. isReadyLoad=" + isReadyLoad);
        return false;
    }


}
