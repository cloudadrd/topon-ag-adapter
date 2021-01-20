package com.test.ad.demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.AdError;
import com.anythink.rewardvideo.api.ATRewardVideoAd;
import com.anythink.rewardvideo.api.ATRewardVideoListener;

import java.util.LinkedList;
import java.util.Queue;

public class RewardLoadManager {

    private static final String TAG = "RewardLoadManager";
    private Queue<ATRewardVideoAd> rewardQueue = new LinkedList<>();

    private final static int CACHE_COUNT = 3;

    private final static long DELAY_MILLIS = 2500L;

    Handler mainHandler = new Handler(Looper.getMainLooper());


    private boolean isLoadingRewardAD = false;

    private Context mContext;

    private static class Holder {
        @SuppressLint("StaticFieldLeak")
        private static RewardLoadManager manager = new RewardLoadManager();
    }

    private RewardLoadManager setContext(Context context) {
        mContext = context;
        return this;
    }

    public static RewardLoadManager getInstance(Context context) {

        Context tempContext = Holder.manager.mContext;
        if (tempContext == null && context != null) {
            tempContext = context.getApplicationContext();
        } else {
            throw new RuntimeException("params context is null");
        }
        return Holder.manager.setContext(tempContext);

    }


    private final Runnable loopRun = new Runnable() {
        @Override
        public void run() {

            if (rewardQueue.size() < CACHE_COUNT && !isLoadingRewardAD) {

                isLoadingRewardAD = true;
                ATRewardVideoAd mRewardVideoAd = new ATRewardVideoAd(mContext, "b5fb2228113cf7");

                mRewardVideoAd.setAdListener(new ATRewardVideoListener() {

                    @Override
                    public void onRewardedVideoAdLoaded() {
                        isLoadingRewardAD = false;
                        addATRewardVideoAd(mRewardVideoAd);
                        Log.i(TAG, "onRewardedVideoAdLoaded");
                    }

                    @Override
                    public void onRewardedVideoAdFailed(AdError errorCode) {
                        isLoadingRewardAD = false;
                        Log.i(TAG, "onRewardedVideoAdFailed error:" + errorCode.printStackTrace());
                    }

                    @Override
                    public void onRewardedVideoAdPlayStart(ATAdInfo entity) {
                        Log.i(TAG, "onRewardedVideoAdPlayStart:\n" + entity.toString());
                    }

                    @Override
                    public void onRewardedVideoAdPlayEnd(ATAdInfo entity) {
                        Log.i(TAG, "onRewardedVideoAdPlayEnd:\n" + entity.toString());
                    }


                    @Override
                    public void onRewardedVideoAdPlayFailed(AdError errorCode, ATAdInfo entity) {
                        Log.i(TAG, "onRewardedVideoAdPlayFailed error:" + errorCode.printStackTrace());
                    }

                    @Override
                    public void onRewardedVideoAdClosed(ATAdInfo entity) {
                        Log.i(TAG, "onRewardedVideoAdClosed:\n" + entity.toString());
                    }

                    @Override
                    public void onRewardedVideoAdPlayClicked(ATAdInfo entity) {
                        Log.i(TAG, "onRewardedVideoAdPlayClicked:\n" + entity.toString());
                    }

                    @Override
                    public void onReward(ATAdInfo entity) {
                        Log.e(TAG, "onReward:\n" + entity.toString());
                    }
                });

                mRewardVideoAd.load();
            }
            mainHandler.postDelayed(this, DELAY_MILLIS);
        }
    };

    private void addATRewardVideoAd(ATRewardVideoAd rewardVideoAd) {
        if (!rewardQueue.contains(rewardVideoAd)) {
            rewardQueue.offer(rewardVideoAd);
        }
    }

    public boolean showReward(Activity activity) {

        final ATRewardVideoAd rewardVideoAd = rewardQueue.poll();
        if (rewardVideoAd == null || !rewardVideoAd.isAdReady()) {
            return false;
        }
        rewardVideoAd.show(activity);
        return true;
    }

    /**
     * 在第一次load后去执行
     */
    public void loopLoadStart() {
        isLoadingRewardAD = false;
        mainHandler.post(loopRun);
    }

    public void clean() {
        mainHandler.removeCallbacks(loopRun);
        rewardQueue.clear();
    }
}
