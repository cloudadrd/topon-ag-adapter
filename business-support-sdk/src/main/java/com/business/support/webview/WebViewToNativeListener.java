package com.business.support.webview;

import org.json.JSONObject;

public interface WebViewToNativeListener {

    void event1(InnerWebViewActivity activity);

    void event2(InnerWebViewActivity2 activity);

    void tracking(String name, JSONObject properties);
}
