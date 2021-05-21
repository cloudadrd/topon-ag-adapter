package com.business.support.http;


import com.business.support.config.Const;

import java.net.HttpURLConnection;

/**
 * Created by Vincent
 * Email:jingwei.zhang@yeahmobi.com
 */
public class HttpRunnable implements Runnable {

    private static final String TAG = "HttpRunnable";

    private String urlStr;

    private HttpRequester.Listener listener;

    private String userAgentStr;


    public HttpRunnable(String urlStr, HttpRequester.Listener listener, String userAgentStr) {
        this.urlStr = urlStr;
        this.listener = listener;
        this.userAgentStr = userAgentStr;
    }


    @Override
    public void run() {
        try {
            HttpURLConnection connection = HttpUtils.handleConnection(urlStr, userAgentStr);
            final byte[] bytes = HttpUtils.handleSuccess(connection);
            Const.HANDLER.post(new Runnable() {
                @Override
                public void run() {
                    listener.onSuccess(bytes, urlStr);
                }
            });
        } catch (final Exception e) {
//            YeLog.e(e);
            Const.HANDLER.post(new Runnable() {
                @Override
                public void run() {
                    listener.onFailure(e.getMessage(), urlStr);
                }
            });
        }
    }


}
