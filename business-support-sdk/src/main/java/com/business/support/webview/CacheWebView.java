package com.business.support.webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import static android.content.Context.MODE_PRIVATE;

import com.business.support.config.Const;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CacheWebView extends WebView {

    private AdVideoMediation mediationHelper = null;

    private AdVideoInterface adVideoInterface = null;

    public Context getCustomContext() {
        return context;
    }

    private Context context;

    private boolean is302 = false;

    public boolean isLoadFinish() {
        return isLoadFinish;
    }

    private long startTime;

    public void setLoadFinish(boolean loadFinish) {
        isLoadFinish = loadFinish;
    }

    private boolean isLoadFinish = false;

    public CacheWebView(Context context) {
        super(context);
        init(context);
    }

    public void setContext(Context context) {
        this.context = context;
    }


    @SuppressLint({"SetJavaScriptEnabled", "ObsoleteSdkInt"})
    public void init(Context context) {
        setOverScrollMode(View.OVER_SCROLL_NEVER);

        // 得到浏览器的设置对象
        WebSettings ws = getSettings();
        // 设置浏览器是否缓存数据.true表示缓存,false表示不缓存
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setDatabaseEnabled(true);
        ws.setAppCacheMaxSize(1024 * 1024 * 8);//设置缓冲大小，设的是8M
        String appCacheDir = context.getApplicationContext().getDir("cache", MODE_PRIVATE).getPath();
        ws.setAppCachePath(appCacheDir);
        ws.setAllowFileAccess(true);
        ws.setAppCacheEnabled(true);
        ws.setCacheMode(WebSettings.LOAD_DEFAULT);
        //允许加载http与https混合内容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            ws.setMediaPlaybackRequiresUserGesture(false);
        }
        // api 11以上有个漏洞，要remove
        removeJavascriptInterface("searchBoxJavaBredge_");
        setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        try {
            if (Build.VERSION.SDK_INT >= 16) {
                Class clazz = getSettings().getClass();
                Method method = clazz.getMethod("setAllowUniversalAccessFromFileURLs", boolean.class);
                if (method != null) {
                    method.invoke(getSettings(), true);
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        super.setWebViewClient(webViewClient);
        super.setWebChromeClient(webChromeClient);

        mediationHelper = AdVideoMediation.getInstance();

        adVideoInterface = new AdVideoInterface(this, mediationHelper);
        mediationHelper.setContext(context);
        mediationHelper.addAdVideoInterface(adVideoInterface);
        mediationHelper.loadVideo();
        addJavascriptInterface(adVideoInterface, "android");
    }

    public void notifyDownStated(String pkg, DownloadState state, long progress) {
        if (adVideoInterface != null) {
            adVideoInterface.notifyDownStated(pkg, state, progress);
        }
    }

    public void tracking(String name, String action) {
        if (adVideoInterface != null) {
            adVideoInterface.tracking(name, action);
        }
    }

    @Override
    public void setWebViewClient(WebViewClient client) {
        if (client == null) {
            super.setWebViewClient(webViewClient);
        } else {
            super.setWebViewClient(client);
        }
    }

    WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {

            Const.HANDLER.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (is302) return;
                    Log.e("CacheWebView", "onPageFinished times=" + (System.currentTimeMillis() - startTime));
                    setLoadFinish(true);
                }
            }, 1000);

            super.onPageFinished(view, url);
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url.startsWith("http:") || url.startsWith("https:")) {
                WebView.HitTestResult hit = getHitTestResult();
                int hitType = hit.getType();
                Log.e("CacheWebView", "shouldOverrideUrlLoading times=" + (System.currentTimeMillis() - startTime));
                if (hitType == WebView.HitTestResult.SRC_ANCHOR_TYPE) {//点击超链接
                }
                if (hitType == 0 && !is302) {
                    is302 = true;
                }
                return url.contains(".apk");
            } else {
                return true;
            }
        }
    };


    WebChromeClient webChromeClient = new WebChromeClient() {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            //动态在标题栏显示进度条
            if (newProgress == 100) {  //加载完成，进度条消失
                Log.e("CacheWebView", "onProgressChanged times=" + (System.currentTimeMillis() - startTime));
                setLoadFinish(true);
            }
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }

    };

    @Override
    public void loadUrl(String url) {
        startTime = System.currentTimeMillis();
        super.loadUrl(url);
    }

    @Override
    public void setWebChromeClient(WebChromeClient client) {
        if (client == null) {
            super.setWebChromeClient(webChromeClient);
        } else {
            super.setWebChromeClient(client);
        }
    }

    @Override
    public void reload() {
        setLoadFinish(false);
        startTime = System.currentTimeMillis();
        super.reload();
    }

    @Override
    public void destroy() {
        super.destroy();
        mediationHelper.removeAdVideoInterface(adVideoInterface);
        mediationHelper = null;
        adVideoInterface = null;
    }
}
