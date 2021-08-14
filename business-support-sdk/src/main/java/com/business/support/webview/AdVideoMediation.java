package com.business.support.webview;

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
import com.business.support.config.Const;
import com.business.support.utils.ContextHolder;
import com.business.support.utils.MDIDHandler;
import com.business.support.utils.SLog;
import com.business.support.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;


public class AdVideoMediation {

    private static final String TAG = "AdVideoMediation";

    private List<AdVideoInterface> mAdVideoInterfaces = new LinkedList<>();

    private ATRewardVideoAd mRewardVideoAd;

    private boolean isLoad;

    public boolean isReadyLoad;

    private BSRewardVideoListener mRewardVideoListener;

    private Context mContext;

    public static String POS_ID = "b5fb2228113cf7";

    public static void setPosId(String posId) {
        SLog.d(TAG, "setPosId posId=" + posId);
        AdVideoMediation.POS_ID = posId;
    }

    public void setRewardVideoListener(BSRewardVideoListener rewardVideoListener) {
        this.mRewardVideoListener = rewardVideoListener;
    }


    private static class Holder {
        @SuppressLint("StaticFieldLeak")
        private static final AdVideoMediation MANAGER = new AdVideoMediation();
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
        return AdVideoMediation.Holder.MANAGER;
    }

    private AdVideoMediation() {
    }

    public void loadVideo() {
        if (mContext == null || isLoad) {
            return;
        }
        isLoad = true;
        SLog.d(TAG, "loadVideo posId=" + POS_ID);
        mRewardVideoAd = new ATRewardVideoAd(mContext, POS_ID);
        mRewardVideoAd.setAdListener(new ATRewardVideoListener() {

            @Override
            public void onRewardedVideoAdLoaded() {
                Log.i(TAG, "onRewardedVideoAdLoaded");
                isReadyLoad = true;
                trackState(AdLogType.LOAD_SUCCESS);
                if (mRewardVideoListener != null) {
                    mRewardVideoListener.onRewardedVideoAdLoaded();
                }
            }

            @Override
            public void onRewardedVideoAdFailed(AdError errorCode) {
                Log.i(TAG, "onRewardedVideoAdFailed error:" + errorCode.printStackTrace());
                isReadyLoad = false;
                loadDelay();
                if (mRewardVideoListener != null) {
                    mRewardVideoListener.onRewardedVideoAdFailed(errorCode);
                }
            }

            @Override
            public void onRewardedVideoAdPlayStart(ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdPlayStart:\n" + entity.toString());

                trackState(AdLogType.IMP_SUCCESS, entity.getEcpm());

                if (mRewardVideoListener != null) {
                    mRewardVideoListener.onRewardedVideoAdPlayStart(entity);
                }
            }

            public void loadDelay() {
                Const.HANDLER.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRewardVideoAd.load();
                    }
                }, 10000);
            }

            @Override
            public void onRewardedVideoAdPlayEnd(ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdPlayEnd:\n" + entity.toString());

                if (mRewardVideoListener != null) {
                    mRewardVideoListener.onRewardedVideoAdPlayEnd(entity);
                }
            }


            @Override
            public void onRewardedVideoAdPlayFailed(AdError errorCode, ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdPlayFailed error:" + errorCode.printStackTrace());
                isReadyLoad = false;
                trackState(AdLogType.PLAY_FAIL);
                loadDelay();

                if (mRewardVideoListener != null) {
                    mRewardVideoListener.onRewardedVideoAdPlayFailed(errorCode, entity);
                }
            }

            @Override
            public void onRewardedVideoAdClosed(ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdClosed:\n" + entity.toString());
                isReadyLoad = false;
                trackState(AdLogType.PLAY_END_CLOSE);
                mRewardVideoAd.load();

                if (mRewardVideoListener != null) {
                    mRewardVideoListener.onRewardedVideoAdClosed(entity);
                }
            }

            @Override
            public void onRewardedVideoAdPlayClicked(ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdPlayClicked:\n" + entity.toString());
                trackState(AdLogType.VIDEO_CLICK);

                if (mRewardVideoListener != null) {
                    mRewardVideoListener.onRewardedVideoAdPlayClicked(entity);
                }
            }

            @Override
            public void onReward(ATAdInfo entity) {
                Log.e(TAG, "onReward:\n" + entity.toString());
                trackState(AdLogType.VIDEO_REWARD);

                if (mRewardVideoListener != null) {
                    mRewardVideoListener.onReward(entity);
                }
            }
        });

        mRewardVideoAd.load();
    }


    private void trackState(AdLogType adLogType) {
        trackState(adLogType, 0);
    }

    private void trackState(AdLogType adLogType, double ecpm) {
        for (AdVideoInterface adVideoInterface : mAdVideoInterfaces) {
            adVideoInterface.trackState(adLogType, ecpm);
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
