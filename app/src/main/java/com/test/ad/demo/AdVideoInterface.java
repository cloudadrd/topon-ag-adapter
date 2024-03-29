package com.test.ad.demo;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.MainThread;
import android.util.Log;
import android.webkit.JavascriptInterface;


import com.baidu.mobads.AppActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * s
 * Created by jiantao.tu on 12/4/20.
 */
public class AdVideoInterface {

    public final CacheWebView webView;

    private static final String TAG = "AdVideoInterface";

    private final AdVideoMediation mAdVideoMediationHelper;

    private String callback;

    public AdVideoInterface(CacheWebView webView, AdVideoMediation adVideoMediationHelper) {
        if (webView == null) {
            throw new IllegalArgumentException("webView null");
        }
        this.webView = webView;
        mAdVideoMediationHelper = adVideoMediationHelper;
    }

    @JavascriptInterface
    public void showAd(String callback) {
        webView.post(new Runnable() {
            @Override
            public void run() {
                showInterfaceForMain(callback);
            }
        });
    }

    @JavascriptInterface
    public void isLoad() {
        webView.post(new Runnable() {
            @Override
            public void run() {
                if (mAdVideoMediationHelper.isReadyLoad) {
                    trackState(AdLogType.LOAD_SUCCESS);
                } else {
                    trackState(AdLogType.LOAD_NO_READY);
                }
            }
        });
    }

    @JavascriptInterface
    public void customCall(int event, String params) {
        webView.post(new Runnable() {
            @Override
            public void run() {
                customCallForMain(event, params);
            }
        });
    }

    @MainThread
    private void customCallForMain(int event, String params) {
        switch (event) {
            case 1://关闭webview，去绑定微信页面
                if (webView.getCustomContext() instanceof InnerWebViewActivity) {
                    Log.d(TAG, "call customCallForMain, type is 2, webView.getCustomContext() is InnerWebViewActivity.");
                    InnerWebViewActivity activity = (InnerWebViewActivity) webView.getCustomContext();
                    if (!activity.isDestroyed()) {
//                        activity.startActivity(new Intent(activity,AppActivity.class));
//                        activity.finish();
                        //调微信
                    } else {
                        Log.d(TAG, "call showAd, Activity is destroyed.");
                    }
                }
                break;
            case 2://
                break;
        }
    }


    @MainThread
    private void showInterfaceForMain(String callback) {
        this.callback = callback;
        if (mAdVideoMediationHelper.isReadyLoadForInterfaceIsNull) {
            trackState(AdLogType.LOAD_SUCCESS);
        }
        showAd();
    }


    @JavascriptInterface
    public void goBack() {
        Log.d(TAG, "goBack");
        webView.post(new Runnable() {
            @Override
            public void run() {
                if (webView.canGoBack()) {
                    webView.goBack();
                    webView.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            webView.loadUrl("javascript:xmActivityRefresh()");
                        }

                    }, 200);
                }
            }
        });

    }

    @JavascriptInterface
    public void close() {
        if (webView.getCustomContext() instanceof InnerWebViewActivity) {
            ((InnerWebViewActivity) webView.getCustomContext()).finish();
        }
    }

    @JavascriptInterface
    public void tracking(String name, String action) {
        try {
            JSONObject properties = new JSONObject();
            properties.put("action", action);
//            AppActivity.app.biInstance.track(name, properties);
            Log.d(TAG, "tracking name=" + name + ",action=" + action);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @JavascriptInterface
    public void printLog(String str) {
        Log.d(TAG, "log WebView callback str=" + str);
    }

    /**
     * * * * * * *
     */
    public void showAd() {
        boolean flag = false;
        if (webView.getCustomContext() instanceof Activity) {
            Log.d(TAG, "call showAd, webView.getCustomContext() is Activity.");
            Activity activity = (Activity) webView.getCustomContext();
            if (!activity.isDestroyed()) {
                flag = mAdVideoMediationHelper.show(activity);
            } else {
                Log.d(TAG, "call showAd, Activity is destroyed.");
            }
        }
        if (!flag) {
            trackState(AdLogType.PLAY_FAIL);
        }
    }

    public void trackState(AdLogType adLogType) {
        if (adLogType == null) return;
        String loadStr = String.format("javascript:%s('%s','%s')", "callbackfun", AdVideoMediation.POSID, adLogType.getTypeId());
        Log.d(TAG, "trackState loaStr=" + loadStr);
        webView.loadUrl(loadStr);
    }

}