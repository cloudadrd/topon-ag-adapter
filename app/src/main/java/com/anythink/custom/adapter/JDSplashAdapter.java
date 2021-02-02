package com.anythink.custom.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.jd.ad.sdk.imp.JadListener;
import com.jd.ad.sdk.imp.splash.SplashAd;
import com.jd.ad.sdk.work.JadPlacementParams;
import com.anythink.splashad.unitgroup.api.CustomSplashAdapter;
//import com.anythink.splashad.unitgroup.api.CustomSplashEventListener;

import java.util.Map;

public class JDSplashAdapter extends CustomSplashAdapter implements JadListener {
    private final String TAG = "JDSplashAdapter:";

    SplashAd splashAd;
    private ViewGroup contentView;
    String slotId = null;
    private Activity activity;
    private float tolerateTime = (float) 3.5;
    private int skipTime = 5;

    @Override
    public void loadCustomNetworkAd(final Context context, Map<String, Object> serverExtra, Map<String, Object> localExtra) {
        String appId = (String) serverExtra.get("app_id");
        slotId = (String) serverExtra.get("slot_id");
        String appName = (String) serverExtra.get("app_name");

        tolerateTime = Float.parseFloat((String) serverExtra.get("tolerate_time"));
        skipTime = Integer.parseInt((String) serverExtra.get("skip_time"));
        //检测传入参数
        if (TextUtils.isEmpty(appId) || TextUtils.isEmpty(slotId)) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError(TAG, "app_id or slot_id is empty!");
            }
            return;
        }

        JDUtils.JDSDKInit(appId,context);
        startLoad(context,slotId);
    }

    private void startLoad(Context context,String slotID) {
        int width = JDSplashAdapter.getScreenWidth(context);
        int height = JDSplashAdapter.getScreenHeight(context);
        JadPlacementParams jadParams = new JadPlacementParams.Builder()
                .setPlacementId(slotId)
                .setSize(width, height)
                .setTolerateTime(tolerateTime)
                .setSupportDeepLink(false)
                .setSkipTime(skipTime)
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
    public boolean isAdReady() {
        return false;
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

    @Override
    public void show(Activity activity, ViewGroup viewGroup) {

    }
}
