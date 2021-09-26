package com.business.support.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.webkit.WebSettings;

import androidx.annotation.Keep;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static android.Manifest.permission.READ_PHONE_STATE;

public class Utils {


    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

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

    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess
                : mActivityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }


    /**
     * 将srcJObjStr和addJObjStr合并，如果有重复字段，以addJObjStr为准
     *
     * @param srcJObjStr 原jsonObject字符串
     * @param addJObjStr 需要加入的jsonObject字符串
     * @return srcJObjStr
     */
    public static String combineJson(String srcJObjStr, String addJObjStr) {
        if (addJObjStr == null || addJObjStr.isEmpty()) {
            return "";
        }
        if (srcJObjStr == null || srcJObjStr.isEmpty()) {
            return "";
        }

        try {
            JSONObject srcJObj = new JSONObject(srcJObjStr);
            JSONObject addJObj = new JSONObject(addJObjStr);
            return combineJson(srcJObj, addJObj).toString();
        } catch (JSONException e) {
            SLog.e(e);
        }

        return "";
    }

    public static JSONObject combineJson(JSONObject srcObj, JSONObject addObj) throws JSONException {

        Iterator<String> itKeys1 = addObj.keys();
        String key, value;
        while (itKeys1.hasNext()) {
            key = itKeys1.next();
            value = addObj.optString(key);

            srcObj.put(key, value);
        }
        return srcObj;
    }

    public static int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, Resources.getSystem().getDisplayMetrics());
    }

    public static Drawable getDrawable(int resId) {
        return ContextHolder.getGlobalAppContext().getResources().getDrawable(resId);
    }

    /**
     * This value will not collide with ID values generated at build time by aapt for R.id.
     * 用于代码实现布局时为view设置ID
     *
     * @return a generated ID value
     */
    @Keep
    public static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }


    public static boolean startActivityForPackage(Context context, String packName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packName);
        if (intent == null) return false;
        ComponentName componentName = intent.resolveActivity(context.getPackageManager());

        if (componentName != null) {    //已经安装该应用
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        }
        return false;

    }

    public static void startDeeplink(Context context, String pkg, String deeplink) {
        Uri uri = Uri.parse(deeplink);
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        if (!TextUtils.isEmpty(pkg)) {
            it.setPackage(pkg);
        }
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ComponentName componentName = it.resolveActivity(context.getPackageManager());

        if (componentName != null) {    //已经安装该应用
            context.startActivity(it);
        }
    }


    /**
     * @return null may be returned if the specified process not found
     */
    @Keep
    public static String getProcessName(Context context, int pid) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

    /**
     * @return 获取手机IMEI
     */
    @SuppressLint({"HardwareIds", "MissingPermission"})
    public static String getIMEI(final Context context) {
        if (!isPermissionGranted(context, READ_PHONE_STATE)) {
            return null;
        }
        String imei = "";
        try {
            TelephonyManager mTelephony =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephony == null) return null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (mTelephony.getPhoneCount() == 2) {
                        imei = mTelephony.getImei(0);
                    } else {
                        imei = mTelephony.getImei();
                    }
                } else {
                    if (mTelephony.getPhoneCount() == 2) {
                        imei = mTelephony.getDeviceId(0);
                    } else {
                        imei = mTelephony.getDeviceId();
                    }
                }
            } else {
                imei = mTelephony.getDeviceId();
            }
            Log.d("getIMEI", imei);
        } catch (Exception e) {
            Log.d("getIMEI", e.getMessage());
        }
        return imei;
    }

    public static boolean isPermissionGranted(final Context context, final String permission) {
        if (null == context || TextUtils.isEmpty(permission)) {
            return false;
        }

        //之前的方法,对版本有要求,必须是23以上
        return context.checkPermission(permission, android.os.Process.myPid(), android.os.Process.myUid()) == PackageManager.PERMISSION_GRANTED;
    }

    public static long getAvailMemory(Context c) {
        ActivityManager am = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        if (am != null) {
            am.getMemoryInfo(mi);
        }
        return mi.availMem;
    }

    public static long getTotalMemory(Context c) {
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        ActivityManager am = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            am.getMemoryInfo(memInfo);
        }
        return memInfo.totalMem;
    }

    public static String getNetworkOperatorName(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(
                Context.TELEPHONY_SERVICE);
        return telephonyManager.getNetworkOperatorName();
    }

    /**
     * 判断是否包含SIM卡
     *
     * @return 状态
     */
    public static boolean hasSimCard(Context context) {
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
}
