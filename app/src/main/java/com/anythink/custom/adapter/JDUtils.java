package com.anythink.custom.adapter;

import android.app.Application;
import android.content.Context;

import com.bun.miitmdid.core.MdidSdkHelper;
import com.bun.miitmdid.interfaces.IIdentifierListener;
import com.bun.miitmdid.interfaces.IdSupplier;
import com.jd.ad.sdk.JadYunSdk;
import com.jd.ad.sdk.JadYunSdkConfig;
import com.jd.ad.sdk.widget.JadCustomController;

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

    public static void JDSDKInit(String appid){
        JadYunSdkConfig config = new JadYunSdkConfig
                .Builder()
                .setAppId(appid)
                .setEnableLog(true)
                .build();
        JadYunSdk.init(JDUtils.getApplicationInner(), config);
        MdidSdkHelper.InitSdk(JDUtils.getApplicationInner(), true, new IIdentifierListener() {
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
}
