package com.anythink.custom.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.jd.ad.sdk.imp.JadListener;
import com.jd.ad.sdk.imp.splash.SplashAd;
import com.jd.ad.sdk.work.JadPlacementParams;
import com.anythink.splashad.unitgroup.api.CustomSplashAdapter;
import com.anythink.splashad.unitgroup.api.CustomSplashEventListener;

import java.util.Map;

public class JDSplashAdapter extends CustomSplashAdapter implements JadListener {
    private final String TAG = "JDSplashAdapter:";

    SplashAd splashAd;
    private ViewGroup contentView;
    private JadPlacementParams adParams;
    String slotId = null;
    private CountDownTimer timer;
    private int fetchDelay = 3000;
    private boolean isTimerOut;
    private boolean isDestroyed;
    private static boolean isSplashReaday;
    private Activity activity;

    @Override
    public void loadCustomNetworkAd(final Context context, Map<String, Object> serverExtra, Map<String, Object> localExtra) {
        if (serverExtra.containsKey("slot_id")) {
            slotId = (String) serverExtra.get("slot_id");
            if (slotId == null) {
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError("", " slot_id is empty!");
                }
                return;
            }
        }else{
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError("", " slot_id is empty!");
            }
            return;
        }
        if (serverExtra.containsKey("fetchDelay")){
            try {
                fetchDelay = Integer.parseInt((String) serverExtra.get("fetchDelay"));
            } catch (Exception e) {
            }
        }
        startLoad(context,slotId);
    }

    private void startLoad(Context context,String slotID) {
        int width = JDSplashAdapter.getScreenWidth(context);
        int height = JDSplashAdapter.getScreenHeight(context);
        JadPlacementParams jadParams = new JadPlacementParams.Builder()
                .setPlacementId("2525")
                .setSize(width, height)
                .setTolerateTime(3.5f)
                .setSupportDeepLink(false)
                .setSkipTime(5)
                .build();
        activity = (Activity) context;
        splashAd = new SplashAd(activity, jadParams, JDSplashAdapter.this);
        splashAd.loadAd();
    }

    @Override
    public String getNetworkName() {
        return null;
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
        return null;
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }


    //JadListener
    @Override
    public void onAdLoadSuccess() {
        Log.d(TAG, "onAdLoadSuccess.");
        if (mLoadListener != null) {
            mLoadListener.onAdCacheLoaded();
        }
    }

    @Override
    public void onAdLoadFailed(int i, String s) {
        Log.d(TAG, "onAdLoadFailed."+s);
        if (mLoadListener != null) {
            mLoadListener.onAdLoadError(TAG,s);
        }
    }

    @Override
    public void onAdRenderSuccess(View view) {
        Log.d(TAG, "onAdRenderSuccess.");
        contentView = activity.findViewById(android.R.id.content);
        splashAd.showAd(contentView);
        if (mImpressionListener != null) {
            mImpressionListener.onSplashAdShow();
        }
    }

    @Override
    public void onAdRenderFailed(int i, String s) {
        Log.d(TAG, "onAdRenderFailed.");
    }

    @Override
    public void onAdClicked() {
        Log.d(TAG, "onAdClicked.");
        if (mImpressionListener != null) {
            mImpressionListener.onSplashAdClicked();
        }
    }

    @Override
    public void onAdExposure() {
        Log.d(TAG, "onAdExposure.");

    }

    @Override
    public void onAdDismissed() {
        Log.d(TAG, "onAdDismissed.");
        if (mImpressionListener != null) {
            mImpressionListener.onSplashAdDismiss();
        }
    }
}
