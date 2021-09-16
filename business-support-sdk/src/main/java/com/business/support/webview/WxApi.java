package com.business.support.webview;

public class WxApi {

    public interface SendListener {
        void send(String json);
    }

    public interface ResultListener {
        void result(String json);
    }

    static SendListener sendListener;

    static ResultListener resultListener;

    public static void registerWxSend(SendListener sendListener) {
        WxApi.sendListener = sendListener;
    }

    public static void registerWxResult(ResultListener resultListener) {
        WxApi.resultListener = resultListener;
    }

    public static void send(String json) {
        if (sendListener != null) {
            sendListener.send(json);
        }
    }

    public static void result(String json) {
        if (sendListener != null) {
            resultListener.result(json);
        }
    }

}
