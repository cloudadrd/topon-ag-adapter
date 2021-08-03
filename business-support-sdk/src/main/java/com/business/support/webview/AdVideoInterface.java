package com.business.support.webview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.webkit.JavascriptInterface;


import androidx.annotation.MainThread;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.zcoup.multidownload.entitis.FileInfo;
import com.zcoup.multidownload.service.LoadListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Locale;

/**
 * s
 * Created by jiantao.tu on 12/4/20.
 */
public class AdVideoInterface {

    public final CacheWebView webView;

    private static final String TAG = "AdVideoInterface";

    private final AdVideoMediation mAdVideoMediationHelper;

    public static WebViewToNativeListener nativeListener = null;

    private String callback;

    public AdVideoInterface(CacheWebView webView, AdVideoMediation adVideoMediationHelper) {
        if (webView == null) {
            throw new IllegalArgumentException("webView null");
        }
        this.webView = webView;
        mAdVideoMediationHelper = adVideoMediationHelper;
    }

    @JavascriptInterface
    public void showAd(final String callback) {
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
    public void customCall(final int event, final String params) {
        webView.post(new Runnable() {
            @Override
            public void run() {
                customCallForMain(event, params);
            }
        });
    }


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
                    Log.d(TAG, "call customCallForMain, type is 2, webView.getCustomContext() is InnerWebViewActivity.");
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
            case 5://验证apk状态
                try {
                    JSONObject jsonObject = new JSONObject(params);
                    String pkg = jsonObject.optString("pkg");
                    validateApkState(webView.getContext().getApplicationContext(), pkg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

        }
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


//    public void startInstall(String pkg) {
//        File apkFile = new File(DownloadManager.getPath(pkg));
//        if (!apkFile.exists()) {
//            return;
//        }
//        Intent intent = new Intent(Intent.ACTION_VIEW);
////      安装完成后，启动app（源码中少了这句话）
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        Context context = webView.getContext();
//        Uri photoURI = FileProvider.getUriForFile(context,
//                context.getPackageName() + ".takePhotoFileProvider",
//                apkFile);
//        intent.setDataAndType(photoURI, "application/vnd.android.package-archive");
//        webView.getCustomContext().startActivity(intent);
//    }


    public void startInstall(String pkg) {
        File apkFile = new File(DownloadManager.getPath(pkg));
        if (!apkFile.exists()) {
            return;
        }
        Context context = webView.getContext();
        Intent intent = new Intent(Intent.ACTION_VIEW);
//      安装完成后，启动app（源码中少了这句话）
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri photoURI = FileProvider.getUriForFile(context,
                    context.getPackageName() + ".takePhotoFileProvider",
                    apkFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(photoURI, "application/vnd.android.package-archive");
        } else {
            Uri uri = Uri.fromFile(apkFile);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        }
        webView.getCustomContext().startActivity(intent);
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


    @MainThread
    private void showInterfaceForMain(String callback) {
        this.callback = callback;
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
        } else if (webView.getCustomContext() instanceof InnerWebViewActivity2) {
            ((InnerWebViewActivity2) webView.getCustomContext()).finish();
        }
    }

    @JavascriptInterface
    public void tracking(String name, String action) {
        try {
            JSONObject properties = new JSONObject();
            properties.put("action", action);
            if (nativeListener != null) {
                nativeListener.tracking(name, properties);
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
        trackState(adLogType, 0);
    }

    public void trackState(AdLogType adLogType, double ecpm) {
        if (adLogType == null) return;
        String loadStr = String.format(Locale.getDefault(), "javascript:%s('%s','%s',%.2f)", "callbackfun", AdVideoMediation.POS_ID, adLogType.getTypeId(), ecpm);
        Log.d(TAG, "trackState loaStr=" + loadStr);
        webView.loadUrl(loadStr);
    }

}