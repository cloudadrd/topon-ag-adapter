package com.anythink.custom.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.anythink.splashad.unitgroup.api.CustomSplashAdapter;
import com.jd.ad.sdk.imp.JadListener;
import com.jd.ad.sdk.imp.splash.SplashAd;
import com.jd.ad.sdk.work.JadPlacementParams;

import java.util.Map;

//import com.anythink.splashad.unitgroup.api.CustomSplashEventListener;

public class JDSplashAdapter extends CustomSplashAdapter implements JadListener {
    private final String TAG = "JDSplashAdapter:";

    SplashAd splashAd;
    private ViewGroup contentView;
    String slotId = null;
    private Activity activity;
    private float tolerateTime = (float) 3.5;
    private int skipTime = 5;
    float maxRate = (float) 0.0;
    float miniRate = (float) 0.0;
    float floatUpDown = (float) 0.0;
    //"max_rate":"0.61","mini_rate":"0.49","float_up_down":"0.05"

    @Override
    public void loadCustomNetworkAd(final Context context, Map<String, Object> serverExtra, Map<String, Object> localExtra) {
        String appId = (String) serverExtra.get("app_id");
        slotId = (String) serverExtra.get("slot_id");
        String appName = (String) serverExtra.get("app_name");

        tolerateTime = Float.parseFloat((String) serverExtra.get("tolerate_time"));
        skipTime = Integer.parseInt((String) serverExtra.get("skip_time"));
        maxRate = Float.parseFloat((String) serverExtra.get("max_rate"));
        miniRate = Float.parseFloat((String) serverExtra.get("mini_rate"));
        floatUpDown = Float.parseFloat((String) serverExtra.get("float_up_down"));

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
        int width = JDUtils.getScreenWidth(context);
        int height = JDUtils.getScreenHeight(context);
        width = JDUtils.px2dip(context,width);
        height = JDUtils.px2dip(context,height);

        if(floatUpDown >0.00){
            if(((float)width/(float)height) > maxRate  && ((float)width/(float)height) < maxRate + floatUpDown){
                width = (int)((float)height * maxRate)-1;
            }

            if(((float)width/(float)height) < miniRate  && ((float)width/(float)height) > miniRate - floatUpDown){
                width = (int)((float)height * miniRate)+1;
            }
        }


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
        return "JD Custom";
    }

    @Override
    public boolean isAdReady() {
        return splashAd != null;
    }

    @Override
    public void destory() {
        splashAd = null;
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
//        contentView = activity.findViewById(android.R.id.content);
        if (mLoadListener != null) {
            mLoadListener.onAdCacheLoaded();
        }


    }

    @Override
    public void onAdRenderFailed(int i, String s) {
        Log.d(TAG, "onAdRenderFailed.");
        if (mLoadListener != null) {
            mLoadListener.onAdLoadError(TAG,s);
        }
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
        if (mImpressionListener != null) {
            mImpressionListener.onSplashAdShow();
        }

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
        Log.d(TAG, "show.");
        splashAd.showAd(viewGroup);
    }
}
