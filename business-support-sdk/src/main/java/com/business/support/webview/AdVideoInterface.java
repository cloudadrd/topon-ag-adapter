package com.business.support.webview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;

import androidx.annotation.MainThread;
import androidx.annotation.RequiresApi;

import com.business.support.YMBusinessService;
import com.business.support.utils.ImageResultListener;
import com.business.support.utils.MDIDHandler;
import com.business.support.utils.StatusBarUtils;
import com.business.support.utils.Utils;
import com.zcoup.multidownload.entitis.FileInfo;
import com.zcoup.multidownload.service.LoadListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Locale;

import cn.thinkingdata.android.ThinkingAnalyticsSDK;

/**
 * s
 * Created by jiantao.tu on 12/4/20.
 */
public class AdVideoInterface {

    public final CacheWebView webView;

    private static final String TAG = "AdVideoInterface";

    private final AdVideoMediation mAdVideoMediation;

    private final AdInterstitialMediation mAdInterstitialMediation;

    public static WebViewToNativeListener nativeListener = null;

    private String callback;

    private String callbackInterstitial;

    public AdVideoInterface(CacheWebView webView, AdVideoMediation adVideoMediation, AdInterstitialMediation adInterstitialMediation) {
        if (webView == null) {
            throw new IllegalArgumentException("webView null");
        }
        this.webView = webView;
        mAdVideoMediation = adVideoMediation;
        mAdInterstitialMediation = adInterstitialMediation;
    }

    @JavascriptInterface
    public void showAd(final String callback) {
        webView.post(new Runnable() {
            @Override
            public void run() {
                showInterfaceForMain(callback, AdType.REWARD);
            }
        });
    }


    @JavascriptInterface
    public void showInterstitial(final String callback) {
        webView.post(new Runnable() {
            @Override
            public void run() {
                showInterfaceForMain(callback, AdType.INTERSTITIAL);
            }
        });
    }

    @MainThread
    private void showInterfaceForMain(String callback, AdType adType) {
        if (adType == AdType.REWARD) {
            this.callback = callback;
        } else {
            this.callbackInterstitial = callback;
        }

        showAd(adType);
    }

    /**
     * * * * * * *
     */
    public void showAd(AdType adType) {
        boolean flag = false;
        if (webView.getCustomContext() instanceof Activity) {
            Log.d(TAG, "call showAd, webView.getCustomContext() is Activity. adType=" + adType.name());
            Activity activity = (Activity) webView.getCustomContext();
            if (!activity.isDestroyed()) {
                if (adType == AdType.REWARD) {
                    flag = mAdVideoMediation.show(activity);
                } else {
                    flag = mAdInterstitialMediation.show(activity);
                }

            } else {
                Log.d(TAG, "call showAd, Activity is destroyed.");
            }
        }
        if (!flag) {
            if (adType == AdType.REWARD) {
                trackState(AdLogType.PLAY_FAIL);
            } else {
                trackStateInterstitial(AdLogType.PLAY_FAIL);
            }

        }
    }

    @JavascriptInterface
    public void isLoad() {
        webView.post(new Runnable() {
            @Override
            public void run() {
                if (mAdVideoMediation != null && mAdVideoMediation.isReadyLoad) {
                    trackState(AdLogType.LOAD_SUCCESS);
                } else {
                    trackState(AdLogType.LOAD_NO_READY);
                }
            }
        });
    }

    @JavascriptInterface
    public void isLoadInterstitial() {
        webView.post(new Runnable() {
            @Override
            public void run() {
                if (mAdInterstitialMediation != null && mAdInterstitialMediation.isReadyLoad) {
                    trackStateInterstitial(AdLogType.LOAD_SUCCESS);
                } else {
                    trackStateInterstitial(AdLogType.LOAD_NO_READY);
                }
            }
        });
    }

    @JavascriptInterface
    public void customCall(final int event, final String params) {
        webView.post(new Runnable() {
            @Override
            public void run() {
                customCallForMain(event, params);
            }
        });
    }

//    @JavascriptInterface
//    public void requestProtobuf() {
//        int[] result = new int[]{8, 1, 16, 3, 26, 32, 50, 48, 50, 49, 48, 54, 48, 50, 49, 49, 52, 53, 49, 52, 53, 53, 57, 57, 49, 52, 48, 49, 54, 48, 49, 57, 49, 55, 55, 52, 55, 50, 34, 30, 49, 54, 51, 49, 49, 55, 53, 48, 51, 49, 50, 57, 54, 49, 51, 51, 48, 49, 55, 49, 48, 54, 56, 56, 50, 51, 48, 55, 49, 55, 40, 128, 244, 238, 204, 188, 47, 48, 0, 56, 1, 64, 111, 72, 1, 82, 32, 50, 48, 50, 49, 48, 51, 48, 57, 49, 53, 49, 51, 53, 49, 53, 50, 57, 49, 54, 51, 53, 52, 54, 49, 52, 49, 50, 49, 54, 55, 54, 56, 88, 0, 96, 0, 106, 2, 67, 78, 114, 2, 90, 72, 128, 1, 128, 244, 238, 204, 188, 47, 146, 1, 127, 10, 43, 66, 101, 97, 114, 101, 114, 32, 51, 48, 55, 56, 57, 55, 49, 100, 45, 99, 54, 57, 99, 45, 52, 53, 53, 102, 45, 97, 99, 99, 49, 45, 55, 99, 51, 57, 102, 53, 55, 51, 99, 102, 101, 101, 16, 3, 26, 3, 49, 46, 48, 56, 1, 64, 1, 74, 2, 67, 78, 82, 46, 10, 2, 104, 53, 18, 2, 104, 53, 26, 2, 104, 53, 34, 32, 50, 48, 50, 49, 48, 54, 48, 50, 49, 49, 52, 53, 49, 52, 53, 53, 57, 57, 49, 52, 48, 49, 54, 48, 49, 57, 49, 55, 55, 52, 55, 50, 90, 17, 8, 0, 18, 10, 49, 50, 55, 46, 48, 46, 48, 46, 53, 51, 24, 144, 63};
//        byte[] bytes = new byte[result.length];
//        for (int i = 0; i < bytes.length; i++) {
//            if (!((result[i] & 0xFF) == result[i])) {
//                throw new RuntimeException("convert error...");
//            }
//            bytes[i] = (byte) result[i];
//        }
//        String hexStr = com.vilyever.socketclient.util.Utils.byte2HexStr(bytes);
//        String loadStr = String.format(Locale.getDefault(), "javascript:protobufResult('%s')", hexStr);
//        Log.d(TAG, "wxBoundResult loaStr=" + loadStr);
//        webView.loadUrl(loadStr);
//        System.out.println(result);
//    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @MainThread
    private void customCallForMain(int event, String params) {
        switch (event) {
            case 1://关闭webview，去绑定微信页面
                if (webView.getCustomContext() instanceof InnerWebViewActivity) {
                    Log.d(TAG, "call customCallForMain, type is 1, webView.getCustomContext() is InnerWebViewActivity.");
                    InnerWebViewActivity activity = (InnerWebViewActivity) webView.getCustomContext();
                    if (!activity.isDestroyed()) {
                        if (nativeListener != null) {
                            nativeListener.event1(activity);
                        }
//                        activity.startActivity(new Intent(activity,AppActivity.class));
//                        activity.finish();
                        //调微信
                    } else {
                        Log.d(TAG, "call showAd, Activity is destroyed.");
                    }
                }
                break;

            case 2://
                if (webView.getCustomContext() instanceof InnerWebViewActivity2) {
                    Log.d(TAG, "call customCallForMain, type is 2, webView.getCustomContext() is InnerWebViewActivity2.");
                    InnerWebViewActivity2 activity = (InnerWebViewActivity2) webView.getCustomContext();
                    if (!activity.isDestroyed()) {
                        if (nativeListener != null) {
                            nativeListener.event2(activity);
                        }
                        activity.compat(activity, Color.parseColor(params));
                        //调微信
                    } else {
                        Log.d(TAG, "setting statusBar error, Activity is destroyed.");
                    }
                }
                break;

            case 3://下载apk
                try {
                    JSONObject jsonObject = new JSONObject(params);
                    String url = jsonObject.optString("url");
                    String pkg = jsonObject.optString("pkg");
                    download(url, pkg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 4://安装
                try {
                    JSONObject jsonObject = new JSONObject(params);
                    String pkg = jsonObject.optString("pkg");

                    startInstall(pkg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case 5://验证apk状态
                try {
                    JSONObject jsonObject = new JSONObject(params);
                    String pkg = jsonObject.optString("pkg");
                    validateApkState(webView.getContext().getApplicationContext(), pkg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case 6://调起app
                try {
                    JSONObject jsonObject = new JSONObject(params);
                    String pkg = jsonObject.optString("pkg");
                    Utils.startActivityForPackage(webView.getContext().getApplicationContext(), pkg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case 7://传送事件、比如关闭当前页面，并且传递标识值
                if (webView.getCustomContext() instanceof Activity) {
                    Log.d(TAG, "call customCallForMain, type is 1, webView.getCustomContext() is InnerWebViewActivity.");
                    Activity activity = (Activity) webView.getCustomContext();
                    if (!activity.isDestroyed()) {
                        if (nativeListener != null) {
                            nativeListener.event3(activity, params);
                        }
                    } else {
                        Log.d(TAG, "setting statusBar error, Activity is destroyed.");
                    }
                }
                break;

            case 8://绑定微信
                WxApi.registerWxResult(new WxApi.ResultListener() {
                    @Override
                    public void result(String json) {
                        wxBoundResult(json);
                    }
                });
                WxApi.send(params);
                break;

            case 9://deeplink启动app
                try {
                    JSONObject jsonObject = new JSONObject(params);
                    String pkg = jsonObject.optString("pkg");
                    String deeplink = jsonObject.optString("deeplink");
                    Utils.startDeeplink(webView.getContext().getApplicationContext(), pkg, deeplink);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case 10://支付宝登录
                try {
                    if (webView.getCustomContext() instanceof Activity) {
                        JSONObject jsonObject = new JSONObject(params);
                        String appId = jsonObject.optString("appId");
                        AliPayApi.registerAliPayResult(new AliPayApi.ResultListener() {
                            @Override
                            public void result(String authCode) {
                                aliPayBoundResult(authCode);
                            }
                        });
                        AliPayApi.openAuthScheme((Activity) webView.getCustomContext(), appId);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case 11://下载图片保存系统相册
                try {
                    JSONObject jsonObject = new JSONObject(params);
                    String imgUrl = jsonObject.optString("imgUrl");
                    ImagePreserve.downloadToSysPicture(imgUrl, new ImageResultListener() {
                        @Override
                        public void onSuccess() {
                            saveImgResult(1, true, "");
                        }

                        @Override
                        public void onFailure(String message) {
                            saveImgResult(1, false, message);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case 12://base64保存图片到系统相册
                try {
                    JSONObject jsonObject = new JSONObject(params);
                    String base64 = jsonObject.optString("base64");
                    ImagePreserve.base64ToSysPicture(base64, new ImageResultListener() {
                        @Override
                        public void onSuccess() {
                            saveImgResult(2, true, "");
                        }

                        @Override
                        public void onFailure(String message) {
                            saveImgResult(2, false, message);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case 13://获取剪切板
                String clipboard = Utils.getClipboardContent(webView.getContext().getApplicationContext());
                getClipboard(clipboard);
                break;

            case 14://唤起微信支付
                WxApi.registerPayWxResult(new WxApi.ResultPayListener() {
                    @Override
                    public void result(int code) {
                        wxPayResult(code);
                    }
                });
                WxApi.pay(params);
                break;

            case 15://设置状态栏颜色是否为黑色
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(params);
                    boolean isDark = jsonObject.optBoolean("isDark");
                    StatusBarUtils.setTextDark(webView.getCustomContext(), isDark);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    public void currentResult(String time) {
        String loadStr = String.format(Locale.getDefault(), "javascript:%s('%s')", "currentResult", time);
        Log.d(TAG, "saveImgResult loaStr=" + loadStr);
        webView.loadUrl(loadStr);
    }

    @JavascriptInterface
    public String getCurrentTime() {
        Long time = ThinkingAnalyticsSDK.getTimeFormat();
        return String.valueOf(time);
    }

    public void getClipboard(String clipboard) {
        String loadStr = String.format(Locale.getDefault(), "javascript:%s('%s')", "getClipboard", clipboard);
        Log.d(TAG, "saveImgResult loaStr=" + loadStr);
        webView.loadUrl(loadStr);
    }

    /**
     * @param type      1为下载，2为base64
     * @param isSuccess
     * @param message
     */
    public void saveImgResult(int type, boolean isSuccess, String message) {
        String loadStr = String.format(Locale.getDefault(), "javascript:%s(%d,%b,'%s')", "saveImgResult", type, isSuccess, message);
        Log.d(TAG, "saveImgResult loaStr=" + loadStr);
        webView.loadUrl(loadStr);
    }


    public void aliPayBoundResult(String authCode) {
        String loadStr = String.format(Locale.getDefault(), "javascript:%s('%s')", "aliPayBoundResult", authCode);
        Log.d(TAG, "aliPayBoundResult loaStr=" + loadStr);
        webView.loadUrl(loadStr);
    }

    public void wxBoundResult(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            String loadStr = String.format(Locale.getDefault(), "javascript:%s('%s')", "wxBoundResult", jsonObject.toString());
            Log.d(TAG, "wxBoundResult loaStr=" + loadStr);
            webView.loadUrl(loadStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void wxPayResult(int code) {
        String loadStr = String.format(Locale.getDefault(), "javascript:%s(%d)", "wxPayResult", code);
        Log.d(TAG, "wxPayResult loaStr=" + loadStr);
        webView.loadUrl(loadStr);
    }

    public void validateApkState(Context context, String pkg) {
        DownloadManager.cleanTimeOutFiles();
        boolean isInstall = false;
        try {
            PackageManager pm = context.getPackageManager();
            List<ApplicationInfo> list = pm.getInstalledApplications(0);
            for (ApplicationInfo applicationInfo : list) {

                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {   //用户应用
                    if (pkg.equals(applicationInfo.packageName)) {
                        isInstall = true;
                        break;
                    }
                }
            }
        } catch (Throwable e) {
            Log.d(TAG, "getInstalledApps::" + e.getMessage());
        }

        if (isInstall) {
            DownloadManager.deleteFile(pkg);
            notifyDownStated(pkg, DownloadState.INSTALLED, 0);
            return;
        }

        boolean isExists = DownloadManager.isFileExists(pkg);
        if (isExists) {
            notifyDownStated(pkg, DownloadState.DOWNLOADED, 0);
        } else {
            notifyDownStated(pkg, DownloadState.NO_DOWNLOAD, 0);
        }

    }


    public void startInstall(String pkg) {
        File apkFile = new File(DownloadManager.getPath(pkg));
        if (!apkFile.exists()) {
            return;
        }
        Context context = webView.getContext();
        Intent intent = new Intent(Intent.ACTION_VIEW);
//      安装完成后，启动app（源码中少了这句话）
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = BSFileProvider.getUriForFile(context,
                    context.getPackageName() + ".takePhotoFileProvider",
                    apkFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        } else {
            uri = Uri.fromFile(apkFile);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        }

        List<ResolveInfo> resolveLists = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        // 然后全部授权
        for (ResolveInfo resolveInfo : resolveLists) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        context.startActivity(intent);
    }

    public void download(String url, String pkg) {
        DownloadManager.download(webView.getCustomContext().getApplicationContext(), url, pkg, new LoadListener() {
            @Override
            public void onStart(FileInfo fileInfo) {
                notifyDownStated(fileInfo.getFileName().replace(".apk.temp", ""), DownloadState.START, 0);
            }

            @Override
            public void onUpdate(FileInfo fileInfo) {
                notifyDownStated(fileInfo.getFileName().replace(".apk.temp", ""), DownloadState.UPDATE, fileInfo.getFinished());
            }

            @Override
            public void onSuccess(FileInfo fileInfo) {
                notifyDownStated(fileInfo.getFileName().replace(".apk.temp", ""), DownloadState.SUCCESS, 0);
            }

            @Override
            public void onFailed(FileInfo fileInfo) {
                notifyDownStated(fileInfo.getFileName().replace(".apk.temp", ""), DownloadState.FAILED, 0);
            }
        });
    }

    @MainThread
    public void notifyDownStated(final String pkg, final DownloadState state, final long progress) {
        webView.post(new Runnable() {
            @Override
            public void run() {
                String loadStr = String.format(Locale.ENGLISH, "javascript:%s('%s',%d,%d)", "notifyDownStated", pkg, state.getState(), progress);
                Log.d(TAG, "notifyDownStated loaStr=" + loadStr);
                webView.loadUrl(loadStr);
            }
        });

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
        } else if (webView.getCustomContext() instanceof InnerWebViewActivity2) {
            ((InnerWebViewActivity2) webView.getCustomContext()).finish();
        }
    }

    @JavascriptInterface
    public void tracking(String name, String action) {
        try {
            JSONObject properties = new JSONObject();
            properties.put("action", action);
            if (YMBusinessService.mInstance != null) {
                YMBusinessService.mInstance.track(name, properties);
            }
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


    public void trackState(AdLogType adLogType) {
        trackState(adLogType, 0);
    }

    public void trackState(AdLogType adLogType, double ecpm) {
        if (adLogType == null) return;
        if (TextUtils.isEmpty(callback)) {
            callback = "callbackfun";
        }
        String loadStr = String.format(Locale.getDefault(), "javascript:%s('%s','%s',%.2f)", callback, AdVideoMediation.POS_ID, adLogType.getTypeId(), ecpm);
        Log.d(TAG, "trackState loaStr=" + loadStr);
        webView.loadUrl(loadStr);
    }

    public void trackStateInterstitial(AdLogType adLogType) {
        trackStateInterstitial(adLogType, 0);
    }

    public void trackStateInterstitial(AdLogType adLogType, double ecpm) {
        if (adLogType == null) return;
        if (TextUtils.isEmpty(callbackInterstitial)) {
            callbackInterstitial = "callbackInterstitial";
        }
        String loadStr = String.format(Locale.getDefault(), "javascript:%s('%s','%s',%.2f)", callbackInterstitial, AdInterstitialMediation.POS_ID, adLogType.getTypeId(), ecpm);
        Log.d(TAG, "trackStateInterstitial loaStr=" + loadStr);
        webView.loadUrl(loadStr);
    }


    @JavascriptInterface
    public String androidUserId() {
        String oaid = MDIDHandler.getMdid();
        String androidId = Utils.getAndroidId(webView.getContext());
        String imei = Utils.getIMEI(webView.getContext());
        Log.d(TAG, "androidUserId---oaid=" + oaid);
        Log.d(TAG, "androidUserId---imei=" + imei);
        Log.d(TAG, "androidUserId---androidId=" + androidId);

        String id = "";
        if (!TextUtils.isEmpty(imei)) {
            id = imei;
        } else if (!TextUtils.isEmpty(oaid)) {
            id = oaid;
        } else {
            id = androidId;
        }
        return id;
    }

    public void clear() {
        AliPayApi.registerAliPayResult(null);
        WxApi.registerWxResult(null);
        nativeListener = null;
    }


}