package com.business.support;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

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


    public static String getAndroidID(Context context) {
        String ANDROID_ID = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return ANDROID_ID != null ? ANDROID_ID : "";
    }

    public static boolean isOperator(Context context) {
        TelephonyManager telMgr = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telMgr.getSimState();
        boolean result = true;
        switch (simState) {
            case TelephonyManager.SIM_STATE_ABSENT:
            case TelephonyManager.SIM_STATE_UNKNOWN:
                result = false; // 没有SIM卡
                break;
        }
        return result;
    }

    public static String getAppVersion(Context context) {
        String verName = null;
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;

    }

    public static String getSystem() {

        return "Android";
    }

    public static String getNetworkType(Context context) {
        // Wifi
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
             NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
                return "WIFI";
            }
        }

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context
                .TELEPHONY_SERVICE);

        if (null != telephonyManager) {
            int networkType = telephonyManager.getNetworkType();

            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return "2G";
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return "3G";
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return "4G";
                case TelephonyManager.NETWORK_TYPE_NR:
                    return "5G";
            }
        }
        return "null";
    }

    public static void setFirstInstallTime(long timestamp) {
        mAppInstallTime = timestamp;
        long currentTimestamp =  System.currentTimeMillis();
        mDays =  (currentTimestamp - timestamp)/(24*60*60*1000);
    }

    public static void setRewardedVideoTimes(int playedTimes) {
        mNumberOfTimes = playedTimes;
    }

    public static void requestRewaredConfig(Context context, String appid, StrategyInfoListener listener) {
        mContext = context;
        mListener = listener;
        mAppid = appid;
        new Thread(new Runnable() {
            @Override
            public void run() {
                int sim = isOperator(mContext) ? 1 : 0;
                String urlStr = mUrlStr + "?" +
                        "androidid=" + getAndroidID(mContext) +
                        "&sim=" + sim +
                        "&system=" + getSystem() +
                        "&network=" + getNetworkType(mContext) +
                        "&appversion=" + getAppVersion(mContext) +
                        "&installtime=" + mAppInstallTime +
                        "&days=" + mDays +
                        "&playedtimes=" + mNumberOfTimes+
                        "&appid=" + mAppid;
//                Log.i(TAG,urlStr);

                try {
                    URL url = new URL(urlStr);
//                    Log.i(TAG,url.toString());
                    //得到connection对象。
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    //设置请求方式
                    connection.setRequestMethod("GET");
                    //连接
                    connection.connect();
                    //得到响应码
                    int responseCode = connection.getResponseCode();
                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        mListener.isActive(false);
                        return;
                    }

                    //得到响应流
                    InputStream inputStream = connection.getInputStream();
                    //将响应流转换成字符串
                    String result = is2String(inputStream);//将流转换为字符串。
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

                } catch(Exception e) {
                    mListener.isActive(false);
                    Log.i(TAG, e.getMessage());
                    e.printStackTrace();
                }
                mContext = null;
                mListener = null;
                mAppid = null;
            }
        }).start();
    }

    private static String is2String(InputStream is) throws IOException {

        //连接后，创建一个输入流来读取response
        BufferedReader bufferedReader = new BufferedReader(new
                InputStreamReader(is,"utf-8"));
        String line = "";
        StringBuilder stringBuilder = new StringBuilder();
        String response = "";
        //每次读取一行，若非空则添加至 stringBuilder
        while((line = bufferedReader.readLine()) != null){
            stringBuilder.append(line);
        }
        //读取所有的数据后，赋值给 response
        response = stringBuilder.toString().trim();
        return response;

    }
}