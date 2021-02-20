package com.anythink.custom.adapter;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.bun.miitmdid.core.MdidSdkHelper;
import com.bun.miitmdid.interfaces.IIdentifierListener;
import com.bun.miitmdid.interfaces.IdSupplier;
import com.jd.ad.sdk.JadYunSdk;
import com.jd.ad.sdk.JadYunSdkConfig;
import com.jd.ad.sdk.widget.JadCustomController;
import android.telephony.TelephonyManager;
import static android.Manifest.permission.READ_PHONE_STATE;


import java.lang.reflect.Method;

public class JDUtils {
    public static Application mApplication;

    public static void init(Application application){
        mApplication = application;
    }

    public static Application getApplication(){
        if (mApplication == null) {
            mApplication = getApplicationInner();
        }
        return mApplication;
    }

    public static Application getApplicationInner() {
        try {
            Class<?> activityThread = Class.forName("android.app.ActivityThread");

            Method currentApplication = activityThread.getDeclaredMethod("currentApplication");
            Method currentActivityThread = activityThread.getDeclaredMethod("currentActivityThread");

            Object current = currentActivityThread.invoke((Object)null);
            Object app = currentApplication.invoke(current);

            return (Application)app;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / (scale <= 0 ? 1 : scale) + 0.5f);
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static void JDSDKInit(String appid, Context context){
        JadYunSdkConfig config = new JadYunSdkConfig
                .Builder()
                .setAppId(appid)
                .setEnableLog(true)
                .build();
//        Log.d("aid:", getAndroidId(context));
//        Log.d("IMEI:", getIMEI(context));
        Application application =  (Application)context.getApplicationContext();
        JadYunSdk.init(application, config);
        MdidSdkHelper.InitSdk(application, true, new IIdentifierListener() {
            @Override
            public void OnSupport(boolean b, final IdSupplier idSupplier) {
                if (idSupplier != null && idSupplier.isSupported()) {
                    JadYunSdk.setCustomController(new JadCustomController() {
                        @Override
                        public String getOaid() {

                            return idSupplier.getOAID();
                        }
                    });
                }
            }
        });
    }

    /**
     * 获取Android Id
     */
    public static String getAndroidId(Context context) {
        String androidId = "";
        try {
            androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            //SLog.d(String.format("[msg=get AndroidId][result=success][androidId=%s]", androidId));
        } catch (Exception e) {

        }
        return androidId;
    }

    public static boolean isPermissionGranted(final Context context, final String permission) {
        if (null == context || TextUtils.isEmpty(permission)) {
            return false;
        }

        //之前的方法,对版本有要求,必须是23以上
        return context.checkPermission(permission, android.os.Process.myPid(), android.os.Process.myUid()) == PackageManager.PERMISSION_GRANTED;
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
        } catch (Exception e) {
            Log.d("getIMEI",e.getMessage());
        }
        return imei;
    }
}
