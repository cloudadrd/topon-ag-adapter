package com.business.support.webview;

import org.json.JSONException;

public class WxApi {

    public interface SendListener {
        void send(String json);
    }

    public interface PayListener {
        void payStart(String json) throws JSONException;
    }

    public interface ResultListener {
        void result(String json);
    }

    public interface ResultPayListener {
        void result(int code);
    }

    static SendListener sendListener;

    static ResultListener resultListener;

    static PayListener payListener;


    static ResultPayListener resultPayListener;

    public static void registerPayWxResult(ResultPayListener payResultListener) {
        WxApi.resultPayListener = payResultListener;
    }

    public static void registerWxSend(SendListener sendListener) {
        WxApi.sendListener = sendListener;
    }

    public static void registerWxResult(ResultListener resultListener) {
        WxApi.resultListener = resultListener;
    }


    public static void registerPay(PayListener payListener) {
        WxApi.payListener = payListener;
    }


    public static void send(String json) {
        if (sendListener != null) {
            sendListener.send(json);
        }
    }

    public static void pay(String json) {
        if (payListener != null) {
            try {
                payListener.payStart(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void result(String json) {
        if (resultListener != null) {
            resultListener.result(json);
        }
    }

    public static void resultPay(int code) {
        if (resultPayListener != null) {
            resultPayListener.result(code);
        }
    }

}
