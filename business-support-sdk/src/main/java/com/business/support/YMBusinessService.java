package com.business.support;

import android.content.Context;
import android.util.Log;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class YMBusinessService {
    private static final String TAG = "YMBusinessService";
    private static long mAppInstallTime = 0;
    private static long mDays = 0;
    private static int mNumberOfTimes = 0;

    //    private static String mUrlStr = "http://172.31.4.170:8080/v1/strategy/check";
    //    private static String mUrlStr = "http://172.31.5.40:8080/v1/strategy/check";
    private static String mUrlStr = "http://deapi.adsgreat.cn/v1/strategy/check";
    private static Context mContext = null;
    private static StrategyInfoListener mListener = null;
    private static  String mAppid = "";


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

    public static void setFirstInstallTime(long timestamp) {
        mAppInstallTime = timestamp;
        long currentTimestamp =  System.currentTimeMillis();
        mDays =  (currentTimestamp - timestamp)/(24*60*60*1000);
    }
//
//    public static void setRewardedVideoTimes(int playedTimes) {
//        mNumberOfTimes = playedTimes;
//    }
//
//    public static void requestRewaredConfig(Context context, String appid, StrategyInfoListener listener) {
//        mContext = context;
//        mListener = listener;
//        mAppid = appid;
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                int sim = isOperator(mContext) ? 1 : 0;
//                String urlStr = mUrlStr + "?" +
//                        "androidid=" + getAndroidID(mContext) +
//                        "&sim=" + sim +
//                        "&system=" + getSystem() +
//                        "&network=" + getNetworkType(mContext) +
//                        "&appversion=" + getAppVersion(mContext) +
//                        "&installtime=" + mAppInstallTime +
//                        "&days=" + mDays +
//                        "&playedtimes=" + mNumberOfTimes+
//                        "&appid=" + mAppid;
////                Log.i(TAG,urlStr);
//
//                try {
//                    URL url = new URL(urlStr);
////                    Log.i(TAG,url.toString());
//                    //得到connection对象。
//                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                    //设置请求方式
//                    connection.setRequestMethod("GET");
//                    //连接
//                    connection.connect();
//                    //得到响应码
//                    int responseCode = connection.getResponseCode();
//                    if (responseCode != HttpURLConnection.HTTP_OK) {
//                        mListener.isActive(false);
//                        return;
//                    }
//
//                    //得到响应流
//                    InputStream inputStream = connection.getInputStream();
//                    //将响应流转换成字符串
//                    String result = is2String(inputStream);//将流转换为字符串。
//                    JSONObject respObj = new JSONObject(result);
//                    if (null == respObj) {
//                        mListener.isActive(false);
//                        return;
//                    }
//                    int retCode = respObj.getInt("code");
//                    if (10000 != retCode) {
//                        mListener.isActive(false);
//                        return;
//                    }
//
//                    JSONObject acObj = respObj.getJSONObject("data");
//                    if (null == acObj) {
//                        mListener.isActive(false);
//                        return;
//                    }
//
//                    boolean ac = acObj.getBoolean("status");
//                    mListener.isActive(ac);
//
//                } catch(Exception e) {
//                    mListener.isActive(false);
//                    Log.i(TAG, e.getMessage());
//                    e.printStackTrace();
//                }
//                mContext = null;
//                mListener = null;
//                mAppid = null;
//            }
//        }).start();
//    }
//
//    private static String is2String(InputStream is) throws IOException {
//
//        //连接后，创建一个输入流来读取response
//        BufferedReader bufferedReader = new BufferedReader(new
//                InputStreamReader(is,"utf-8"));
//        String line = "";
//        StringBuilder stringBuilder = new StringBuilder();
//        String response = "";
//        //每次读取一行，若非空则添加至 stringBuilder
//        while((line = bufferedReader.readLine()) != null){
//            stringBuilder.append(line);
//        }
//        //读取所有的数据后，赋值给 response
//        response = stringBuilder.toString().trim();
//        return response;
//
//    }
}