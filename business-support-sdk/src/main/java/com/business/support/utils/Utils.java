package com.business.support.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.webkit.WebSettings;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

public class Utils {

    private static String userAgentStr;

    /**
     * 获取useragent
     */
    public static String getUserAgentStr(Context context, boolean isBase64) {
        if (TextUtils.isEmpty(userAgentStr)) {
            try {
                userAgentStr = getUserAgent(context);
            } catch (Throwable e) {
                SLog.i("getUserAgentStr >>" + e.getMessage());
            }
        }
        if (!TextUtils.isEmpty(userAgentStr)) {
            return isBase64
                    ? Base64.encodeToString(userAgentStr.getBytes(), Base64.DEFAULT)
                    : userAgentStr;
        }
        return "";
    }

    private static String getUserAgent(Context context) {
        String userAgent = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                userAgent = WebSettings.getDefaultUserAgent(context);
            } catch (Exception e) {
                userAgent = System.getProperty("http.agent");
            }
        } else {
            userAgent = System.getProperty("http.agent");
        }
        StringBuilder sb = new StringBuilder();
        if (userAgent == null) return "";

        for (int i = 0, length = userAgent.length(); i < length; i++) {
            char c = userAgent.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static void appendUrlParameter(StringBuilder stringBuilder, Map<String, String> params, boolean isFirstParams) {
        Set<String> keys = params.keySet();
        for (String key : keys) {
            String value = params.get(key);
            if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value) || value.equals("null")) {
                continue;
            }

            if (isFirstParams) {
                isFirstParams = false;
                stringBuilder.append("?");
            } else {
                stringBuilder.append("&");
            }
            stringBuilder.append(urlEncodeUTF8(key));
            stringBuilder.append("=");
            stringBuilder.append(urlEncodeUTF8(value));
        }

    }

    public static void appendUrlParameter(StringBuilder stringBuilder, Map<String, String> params) {
        appendUrlParameter(stringBuilder, params, true);
    }

    public static String urlEncodeUTF8(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public static String optStringHelper(JSONObject json, String... keys) {
        String res = optStringHelperInternal(json, keys);
        if (res == null) {
            res = "";
        }
        return res;
    }

    private static String optStringHelperInternal(JSONObject json, String... keys) {
        if (json == null) {
            return null;
        } else {
            for (int i = 0; i < keys.length - 1; i++) {
                json = json.optJSONObject(keys[i]);
                if (json == null) {
                    return null;
                }
            }

            if (json == null) {
                return null;
            } else {
                return json.optString(keys[keys.length - 1]);
            }
        }
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
        return "NULL";
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

    /**
     * 获取Android Id
     */
    public static String getAndroidId(Context context) {
        String androidId = "";
        try {
            androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            //ZCLog.d(String.format("[msg=get AndroidId][result=success][androidId=%s]", androidId));
        } catch (Exception e) {
            SLog.e(String.format("[msg=get AndroidId][result=fail]"));
        }
        return androidId;
    }

}
