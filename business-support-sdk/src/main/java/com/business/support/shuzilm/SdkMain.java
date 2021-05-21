package com.business.support.shuzilm;

import android.content.Context;

import com.business.support.config.Const;
import com.business.support.http.HttpRequester;
import com.business.support.utils.SLog;
import com.business.support.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.shuzilm.core.Listener;
import cn.shuzilm.core.Main;

public class SdkMain {

    private static final String TAG = "SdkMain";

    public static void init(final Context context, String apiKey, final SIDListener listener) {
        Main.init(context, apiKey);
        Const.HANDLER.postDelayed(new Runnable() {
            @Override
            public void run() {
                Main.getQueryID(context, "channel", "message", 1, new
                        Listener() {
                            @Override
                            public void handler(String s) {
                                requestQuery(context, s, listener);
                            }
                        });
            }
        }, 100);
    }

    private static void requestQuery(Context context, String did, final SIDListener listener) {
        Map<String, String> params = new HashMap<>();
        params.put("protocol", "2");
        params.put("pkg", context.getPackageName());
        params.put("did", did);
        params.put("ver", Utils.getAppVersion(context));
        StringBuilder stringBuilder = new StringBuilder("https://ddi2.shuzilm.cn/q");
        Utils.appendUrlParameter(stringBuilder, params);
        HttpRequester.requestByGet(context, stringBuilder.toString(), new HttpRequester.Listener() {
            @Override
            public void onSuccess(byte[] data, String url) {
                resultHandler(data, listener);
            }

            @Override
            public void onFailure(String msg, String url) {
                SLog.i(TAG, "requestQuery-onFailure msg=" + msg);
                if (listener != null) {
                    listener.onFailure(msg);
                }
            }
        });
    }


    private static void resultHandler(byte[] data, SIDListener listener) {
        try {
            String strData = new String(data);
            SLog.i(TAG, "AdResponse==" + strData);

            JSONObject jsonObject = new JSONObject(strData);
            String errCode = Utils.optStringHelper(jsonObject, "err");
            String device_type = Utils.optStringHelper(jsonObject, "device_type");
//            String normal_times = Utils.optStringHelper(jsonObject, "normal_times");
//            String duplicate_times = Utils.optStringHelper(jsonObject, "duplicate_times");
//            String update_times = Utils.optStringHelper(jsonObject, "update_times");
//            String recall_times = Utils.optStringHelper(jsonObject, "recall_times");
            jsonObject.remove("protocol");
            int score = 0;
            if ("0".equals(errCode)) {
                if ("1".equals(device_type)) {
                    score = 75;
                } else if ("2".equals(device_type)) {
                    score = 25;
                }
            }
            if (listener != null) {
                listener.onSuccess(score, jsonObject.toString());
            }

        } catch (JSONException e) {
            SLog.e(e);
            if (listener != null) {
                listener.onFailure(e.getMessage());
            }
        }
    }
}
