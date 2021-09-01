package com.business.support.http;


import android.content.Context;

import com.business.support.utils.SLog;
import com.business.support.utils.ThreadPoolProxy;
import com.business.support.utils.Utils;

public class HttpRequester {


    public interface Listener {
        void onSuccess(byte[] data, String url);

        void onFailure(String msg, String url);
    }


    public static void requestByGet(Context context, String urlStr, Listener listener) {
        final String userAgentStr = Utils.getUserAgentStr(context, false);
        SLog.d("ad request url=" + urlStr);
        Runnable runnable = new HttpRunnable(urlStr, listener, userAgentStr, RequestMethod.GET, null);
        ThreadPoolProxy.getInstance().execute(runnable);
    }


    public static void requestByPost(Context context, String urlStr, String requestBody, Listener listener) {
        final String userAgentStr = Utils.getUserAgentStr(context, false);
        SLog.d("ad request url=" + urlStr);
        Runnable runnable = new HttpRunnable(urlStr, listener, userAgentStr, RequestMethod.POST, requestBody);
        ThreadPoolProxy.getInstance().execute(runnable);
    }

}
