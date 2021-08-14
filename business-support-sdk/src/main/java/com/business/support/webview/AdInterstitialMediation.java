package com.business.support.webview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.AdError;
import com.anythink.interstitial.api.ATInterstitial;
import com.anythink.interstitial.api.ATInterstitialListener;
import com.business.support.config.Const;
import com.business.support.utils.BSInterstitialListener;
import com.business.support.utils.SLog;

import java.util.LinkedList;
import java.util.List;


public class AdInterstitialMediation {

    private static final String TAG = "AdInterstitialMediation";

    private final List<AdVideoInterface> mAdVideoInterfaces = new LinkedList<>();

    private ATInterstitial mInterstitialAd;

    private boolean isLoad;

    public boolean isReadyLoad;

    private BSInterstitialListener mInterstitialListener;

    private Context mContext;

    public static String POS_ID = "b5fb2228113cf7";

    public static void setPosId(String posId) {
        SLog.d(TAG, "setPosId posId=" + posId);
        AdInterstitialMediation.POS_ID = posId;
    }

    public void setInterstitialListener(BSInterstitialListener mInterstitialListener) {
        this.mInterstitialListener = mInterstitialListener;
    }

    private static class Holder {
        @SuppressLint("StaticFieldLeak")
        private static final AdInterstitialMediation MANAGER = new AdInterstitialMediation();
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

    public static AdInterstitialMediation getInstance() {
        return AdInterstitialMediation.Holder.MANAGER;
    }

    private AdInterstitialMediation() {
    }

    public void loadInterstitial() {
        if (mContext == null || isLoad) {
            return;
        }
        isLoad = true;
        SLog.d(TAG, "loadInterstitial posId=" + POS_ID);
        mInterstitialAd = new ATInterstitial(mContext, POS_ID);
        mInterstitialAd.setAdListener(new ATInterstitialListener() {

            @Override
            public void onInterstitialAdLoaded() {
                Log.i(TAG, "onInterstitialAdLoaded");
                isReadyLoad = true;
                trackState(AdLogType.LOAD_SUCCESS);

                if (mInterstitialListener != null) {
                    mInterstitialListener.onInterstitialAdLoaded();
                }
            }

            @Override
            public void onInterstitialAdLoadFail(AdError errorCode) {
                Log.i(TAG, "onInterstitialAdLoadFail error:" + errorCode.printStackTrace());
                isReadyLoad = false;
                loadDelay();

                if (mInterstitialListener != null) {
                    mInterstitialListener.onInterstitialAdLoadFail(errorCode);
                }
            }

            @Override
            public void onInterstitialAdVideoStart(ATAdInfo entity) {
                Log.i(TAG, "onInterstitialAdVideoStart:\n" + entity.toString());
                if (mInterstitialListener != null) {
                    mInterstitialListener.onInterstitialAdVideoStart(entity);
                }
            }

            public void loadDelay() {
                Const.HANDLER.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mInterstitialAd.load();
                    }
                }, 10000);
            }

            @Override
            public void onInterstitialAdVideoEnd(ATAdInfo entity) {
                Log.i(TAG, "onInterstitialAdVideoEnd:\n" + entity.toString());

                if (mInterstitialListener != null) {
                    mInterstitialListener.onInterstitialAdVideoEnd(entity);
                }
            }


            @Override
            public void onInterstitialAdVideoError(AdError adError) {
                Log.i(TAG, "onInterstitialAdVideoError error:" + adError.printStackTrace());
                isReadyLoad = false;
                trackState(AdLogType.PLAY_FAIL);
                loadDelay();

                if (mInterstitialListener != null) {
                    mInterstitialListener.onInterstitialAdVideoError(adError);
                }
            }

            @Override
            public void onInterstitialAdClose(ATAdInfo entity) {
                Log.i(TAG, "onInterstitialAdClose:\n" + entity.toString());
                isReadyLoad = false;
                trackState(AdLogType.PLAY_END_CLOSE);
                mInterstitialAd.load();

                if (mInterstitialListener != null) {
                    mInterstitialListener.onInterstitialAdClose(entity);
                }
            }

            @Override
            public void onInterstitialAdClicked(ATAdInfo entity) {
                Log.i(TAG, "onInterstitialAdClicked:\n" + entity.toString());
                trackState(AdLogType.VIDEO_CLICK);

                if (mInterstitialListener != null) {
                    mInterstitialListener.onInterstitialAdClicked(entity);
                }
            }

            @Override
            public void onInterstitialAdShow(ATAdInfo entity) {
                Log.i(TAG, "onInterstitialAdShow:\n" + entity.toString());
                trackState(AdLogType.IMP_SUCCESS, entity.getEcpm());

                if (mInterstitialListener != null) {
                    mInterstitialListener.onInterstitialAdShow(entity);
                }
            }


        });

        mInterstitialAd.load();
    }

    private void trackState(AdLogType adLogType) {
        trackState(adLogType, 0);
    }

    private void trackState(AdLogType adLogType, double ecpm) {
        for (AdVideoInterface adVideoInterface : mAdVideoInterfaces) {
            adVideoInterface.trackStateInterstitial(adLogType, ecpm);
        }
    }

    public boolean show(Activity activity) {
        if (mInterstitialAd.isAdReady()) {
            mInterstitialAd.show(activity);
            Log.d(TAG, "call show, show success. isReadyLoad=" + isReadyLoad);
            return true;
        }
        Log.d(TAG, "call show, reward is not ready. isReadyLoad=" + isReadyLoad);
        return false;
    }


}
