package com.business.support.webview;

import android.app.Activity;

public interface WebViewToNativeListener {

    void event1(InnerWebViewActivity activity);

    void event2(InnerWebViewActivity2 activity);

    void event3(Activity activity, String params);

}
