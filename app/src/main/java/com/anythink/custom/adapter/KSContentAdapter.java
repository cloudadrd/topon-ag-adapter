package com.anythink.custom.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;


import com.kwad.sdk.api.KsAdSDK;
import com.kwad.sdk.api.KsContentPage;
import com.kwad.sdk.api.KsContentPage.ContentItem;


import com.anythink.nativead.unitgroup.api.CustomNativeAdapter;
import com.kwad.sdk.api.KsScene;
import com.kwad.sdk.api.SdkConfig;

import java.util.Map;

public class KSContentAdapter extends CustomNativeAdapter {

    private static String TAG = "TopOn-KS-Content:";
    private String slotId;
    private KsContentPage mKsContentPage;
    private KSContentAd  mKSCAd ;

    @Override
    public void loadCustomNetworkAd(final Context context, Map<String, Object> serverExtra, final Map<String, Object> localExtra) {

        String appId = (String) serverExtra.get("app_id");
        slotId = (String) serverExtra.get("slot_id");
        String appName = (String) serverExtra.get("app_name");
        //检测传入参数
        if (TextUtils.isEmpty(appId) || TextUtils.isEmpty(slotId)) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError(TAG, "app_id or slot_id is empty!");
            }
            return;
        }

        //初始化ks
        initSdk(context, appId, appName,false);
        loadContentAd(context ,Long.parseLong(slotId));

    }

    private void initSdk(final Context activity, String appId, String appName, boolean isDebug) {
        KsAdSDK.init(activity, new SdkConfig.Builder()
                .appId(appId) // aapId，必填
                .appName(appName) //appName，非必填
                .showNotification(true) // 是否展示下载通知栏 .debug(true)
                .debug(isDebug) // 是否开启sdk 调试⽇日志 可选
                .build());
    }

    private void loadContentAd(final Context context,long posId) {
        KsScene adScene = new KsScene.Builder(posId).build();
        mKsContentPage = KsAdSDK.getLoadManager().loadContentPage(adScene);
        if (null != mKsContentPage && null != mKsContentPage.getFragment()) {
            mKSCAd = new KSContentAd(context,mKsContentPage.getFragment());
            if (mLoadListener != null) {
                mLoadListener.onAdCacheLoaded(mKSCAd);
            }
        }else{
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError(TAG, "mKsContentPage is empty!");
            }
        }
    }

    private void initListener() {
        // 接口回调在主线程，误做耗时操作
        if (null == mKsContentPage) return;
        mKsContentPage.setPageListener(new KsContentPage.PageListener() {
            @Override
            public void onPageEnter(ContentItem item) {
                Log.d("ContentPage", "position: " + item.position + "页面Enter");
            }

            @Override
            public void onPageResume(ContentItem item) {
                Log.d("ContentPage", "position: " + item.position + "页面Resume");
            }

            @Override
            public void onPagePause(ContentItem item) {
                Log.d("ContentPage", "position: " + item.position + "页面Pause");
            }

            @Override
            public void onPageLeave(ContentItem item) {
                Log.d("ContentPage", "position: " + item.position + "页面Leave");
            }

        });

        // 接口回调在主线程，误做耗时操作
        mKsContentPage.setVideoListener(new KsContentPage.VideoListener() {
            @Override
            public void onVideoPlayStart(ContentItem item) {
                Log.d("ContentPage", "position: " + item.position + "视频PlayStart");
            }

            @Override
            public void onVideoPlayPaused(ContentItem item) {
                Log.d("ContentPage", "position: " + item.position + "视频PlayPaused");
            }

            @Override
            public void onVideoPlayResume(ContentItem item) {
                Log.d("ContentPage", "position: " + item.position + "视频PlayResume");
            }

            @Override
            public void onVideoPlayCompleted(ContentItem item) {
                Log.d("ContentPage", "position: " + item.position + "视频PlayCompleted");
            }

            @Override
            public void onVideoPlayError(ContentItem item, int what, int extra) {
                Log.d("ContentPage", "position: " + item.position + "视频PlayError");
            }
        });
    }

    @Override
    public String getNetworkName() {
        return "KSContent Custom";
    }

    @Override
    public void destory() {
        if(null != mKsContentPage){
            mKsContentPage = null;
        }
        if(null != mKSCAd) {
            mKSCAd = null;
        }
    }

    @Override
    public String getNetworkPlacementId() {
        return slotId;
    }

    @Override
    public String getNetworkSDKVersion() {
        return null;
    }
}
