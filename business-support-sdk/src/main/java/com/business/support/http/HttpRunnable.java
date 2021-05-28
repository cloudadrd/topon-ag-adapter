package com.business.support.http;


import com.business.support.config.Const;

import java.net.HttpURLConnection;

/**
 * Created by Vincent
 * Email:jingwei.zhang@yeahmobi.com
 */
public class HttpRunnable implements Runnable {

    private static final String TAG = "HttpRunnable";

    private final String urlStr;

    private final HttpRequester.Listener listener;

    private final String userAgentStr;

    private final RequestMethod method;

    private final String requestBody;


    public HttpRunnable(String urlStr, HttpRequester.Listener listener, String userAgentStr, RequestMethod method,
                        String requestBody) {
        this.urlStr = urlStr;
        this.listener = listener;
        this.userAgentStr = userAgentStr;
        this.method = method;
        this.requestBody = requestBody;
    }


    @Override
    public void run() {
        try {
            HttpURLConnection connection = HttpUtils.handleConnection(urlStr, userAgentStr, method, requestBody);
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
