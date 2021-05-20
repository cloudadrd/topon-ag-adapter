package com.business.support;

import android.content.Context;

import com.business.support.reallycheck.EmulatorCheck;
import com.business.support.reallycheck.HookCheck;
import com.business.support.reallycheck.ResultData;
import com.business.support.reallycheck.RootCheck;
import com.business.support.reallycheck.WireSharkCheck;
import com.business.support.shuzilm.SIDListener;
import com.business.support.shuzilm.SdkMain;
import com.business.support.utils.SLog;
import com.business.support.utils.Utils;

import org.json.JSONObject;

public class YMBusinessService {


    private static final String TAG = "YMBusinessService";


    public static void init(final Context context, String shumApiKey, final SIDListener listener) {
        SdkMain.init(context.getApplicationContext(), shumApiKey, new SIDListener() {
            @Override
            public void onSuccess(int score, String data) {
                composeNativeValid(context.getApplicationContext(), score, data, listener);
            }

            @Override
            public void onFailure(String msg) {
                SLog.w(TAG, "error msg=" + msg);
                composeNativeValid(context.getApplicationContext(), 0, "{}", listener);
            }
        });
    }

    private static void composeNativeValid(Context context, int score, String data, SIDListener listener) {

        ResultData emulatorResult = EmulatorCheck.validCheck(context);

        ResultData rootResult = RootCheck.validCheck(context);

        ResultData hookResult = HookCheck.validCheck(context);

        ResultData wireSharkResult = WireSharkCheck.validCheck(context);

        if (emulatorResult.isError()) {
            score += 30;
        }

        if (rootResult.isError()) {
            score += 25;
        }

        if (hookResult.isError()) {
            score += 40;
        }

        if (wireSharkResult.isError()) {
            score += 10;
        }

        try {
            JSONObject jsonObject = new JSONObject(data);
            jsonObject.put("Emulator", emulatorResult.isError());
            jsonObject.put("EmulatorMsg", emulatorResult.getErrorMessage());
            jsonObject.put("Hook", hookResult.isError());
            jsonObject.put("WireShark", wireSharkResult.isError());
            jsonObject.put("Root", rootResult.isError());

            if (listener != null) {
                listener.onSuccess(score, jsonObject.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onFailure(e.getMessage());
            }
        }

    }

    public static String getAndroidID(Context context) {
        return Utils.getAndroidId(context);
    }


    public static boolean isOperator(Context context) {
        return Utils.isOperator(context);
    }

    public static String getAppVersion(Context context) {
        return Utils.getAppVersion(context);

    }

    public static String getSystem() {

        return "Android";
    }

    public static String getNetworkType(Context context) {
        return Utils.getNetworkType(context);
    }
}
