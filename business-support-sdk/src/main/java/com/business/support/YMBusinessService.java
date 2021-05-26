package com.business.support;

import android.content.Context;

import com.business.support.http.HttpRequester;
import com.business.support.reallycheck.DebugCheck;
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
    private static long mAppInstallTime = 0;
    private static long mDays = 0;
    private static int mNumberOfTimes = 0;

    //    private static String mUrlStr = "http://172.31.4.170:8080/v1/strategy/check";
//        private static String mUrlStr = "http://172.31.5.40:8080/v1/strategy/check";
    private static String mUrlStr = "http://deapi.adsgreat.cn/v1/strategy/check";
    private static Context mContext = null;
    private static StrategyInfoListener mListener = null;

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

        ResultData debugResult = DebugCheck.validCheck(context);

        if (emulatorResult.isError()) {
            score += emulatorResult.getScore();
        }

        if (rootResult.isError()) {
            score += rootResult.getScore();
            ;
        }

        if (hookResult.isError()) {
            score += hookResult.getScore();
        }

        if (wireSharkResult.isError()) {
            score += wireSharkResult.getScore();
        }

        if (debugResult.isError()) {
            score += debugResult.getScore();
        }


        try {
            JSONObject jsonObject = new JSONObject(data);
            jsonObject.put("Emulator", emulatorResult.isError());
            jsonObject.put("EmulatorMsg", emulatorResult.getErrorMessage());
            jsonObject.put("Hook", hookResult.isError());
            jsonObject.put("WireShark", wireSharkResult.isError());
            jsonObject.put("Root", rootResult.isError());
            jsonObject.put("Debug", debugResult.isError());
            jsonObject.put("DebugMsg", debugResult.getErrorMessage());

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

    public static void setFirstInstallTime(long timestamp) {
        mAppInstallTime = timestamp;
        long currentTimestamp = System.currentTimeMillis();
        mDays = (currentTimestamp - timestamp) / (24 * 60 * 60 * 1000);
    }

    public static void setRewardedVideoTimes(int playedTimes) {
        mNumberOfTimes = playedTimes;
    }

    public static void requestRewaredConfig(Context context, String appid, StrategyInfoListener listener) {
        mListener = listener;
        mContext = context.getApplicationContext();
        int sim = isOperator(mContext) ? 1 : 0;
        String urlStr = mUrlStr + "?" +
                "androidid=" + getAndroidID(mContext) +
                "&sim=" + sim +
                "&system=" + getSystem() +
                "&network=" + getNetworkType(mContext) +
                "&appversion=" + getAppVersion(mContext) +
                "&installtime=" + mAppInstallTime +
                "&days=" + mDays +
                "&playedtimes=" + mNumberOfTimes +
                "&appid=" + appid;
//        SLog.i(TAG,"requestRewaredConfig");
        HttpRequester.requestByGet(context, urlStr, new HttpRequester.Listener() {
            @Override
            public void onSuccess(byte[] data, String url) {
                SLog.i(TAG, "onSuccess");
                try {
                    String result = new String(data);
                    JSONObject respObj = new JSONObject(result);
                    if (null == respObj) {
                        mListener.isActive(false);
                        return;
                    }
                    int retCode = respObj.getInt("code");
                    if (10000 != retCode) {
                        mListener.isActive(false);
                        return;
                    }

                    JSONObject acObj = respObj.getJSONObject("data");
                    if (null == acObj) {
                        mListener.isActive(false);
                        return;
                    }

                    boolean ac = acObj.getBoolean("status");
                    mListener.isActive(ac);
                } catch (Exception e) {
                    mListener.isActive(false);
                    e.printStackTrace();
                }

                mListener = null;
                mContext = null;
            }

            @Override
            public void onFailure(String msg, String url) {
                SLog.i(TAG, "onFailure");
                mListener.isActive(false);
                mListener = null;
                mContext = null;

            }
        });

    }

}