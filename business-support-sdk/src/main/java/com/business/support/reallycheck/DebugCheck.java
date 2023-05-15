package com.business.support.reallycheck;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class  DebugCheck {


    private static final String TAG = "DebugCheck";

    public static ResultData validCheck(Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        int score = 0;
        if (isOpenDebug(context)) {
//            Log.e(TAG, "开启了调试模式");
            stringBuilder.append("1");
            score += 30;
        }

        if (debugVersionCheck(context)) {
//            Log.e(TAG, "判斷是debug版本");
            stringBuilder.append(",2");
            score += 30;
        }

        if (connectedCheck()) {
//            Log.e(TAG, "正在调试");
            stringBuilder.append(",3");
            score += 30;
        }

        return new ResultData(!TextUtils.isEmpty(stringBuilder), stringBuilder.toString(), score);
    }


    /**
     * 开启了调试模式
     *
     * @param context
     * @return
     */
    public static boolean isOpenDebug(Context context) {
        try {
            return (Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ADB_ENABLED, 0) > 0);
        } catch (Exception e) {
            //忽略异常
        }
        return false;
    }

    /**
     * 判斷是debug版本
     *
     * @param context
     * @return
     */
    public static boolean debugVersionCheck(Context context) {
        try {
            return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            //忽略异常
        }
        return false;
    }

    /**
     * 是否正在调试
     *
     * @return
     */
    public static boolean connectedCheck() {
        try {
            return android.os.Debug.isDebuggerConnected();
        } catch (Exception e) {
            //忽略异常
        }
        return false;
    }
}