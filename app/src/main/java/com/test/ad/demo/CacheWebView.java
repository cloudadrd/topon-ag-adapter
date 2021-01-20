package com.test.ad.demo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import static android.content.Context.MODE_PRIVATE;

public class CacheWebView extends WebView {

    private AdVideoMediation mediationHelper = null;

    public Context getCustomContext() {
        return context;
    }

    private Context context;

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
        mediationHelper = AdVideoMediation.getInstance();

        AdVideoInterface adVideoInterface = new AdVideoInterface(this, mediationHelper);
        mediationHelper.setContext(context);
        mediationHelper.setAdVideoInterface(adVideoInterface);
        mediationHelper.loadVideo();
        addJavascriptInterface(adVideoInterface, "android");
    }

    @Override
    public void destroy() {
        super.destroy();
        mediationHelper.setAdVideoInterface(null);
    }
}
