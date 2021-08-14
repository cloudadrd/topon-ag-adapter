package com.business.support.utils;

import static com.bun.miitmdid.core.ErrorCode.INIT_ERROR_DEVICE_NOSUPPORT;
import static com.bun.miitmdid.core.ErrorCode.INIT_ERROR_LOAD_CONFIGFILE;
import static com.bun.miitmdid.core.ErrorCode.INIT_ERROR_MANUFACTURER_NOSUPPORT;
import static com.bun.miitmdid.core.ErrorCode.INIT_ERROR_RESULT_DELAY;
import static com.bun.miitmdid.core.ErrorCode.INIT_HELPER_CALL_ERROR;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.bun.miitmdid.core.MdidSdkHelper;
import com.bun.miitmdid.interfaces.IIdentifierListener;
import com.bun.miitmdid.interfaces.IdSupplier;


/**
 * Created by jiantao.tu on 2020/3/23.
 */
public class MDIDHandler {

    public static String MDID = null;

    public static void init(Context context) {

        if (Build.VERSION.SDK_INT < 21) {
            return;
        }
        Log.d("MDID----oaid-----", "init");
        try {
            int code = MdidSdkHelper.InitSdk(context, true, new IIdentifierListener() {
                @Override
                public void OnSupport(boolean b, IdSupplier idSupplier) {
                    if (idSupplier == null) {
                        return;
                    }
                    String oaid = idSupplier.getOAID();
                    Log.d("MDID---oaid---", oaid);
                    if (oaid.equals("NO") || TextUtils.isEmpty(oaid)) {
                        oaid = "";
                    }
                    MDID = oaid;
                    Log.d("MDID---oaid---", MDID);
                }
            });
            switch (code) {
                case INIT_ERROR_DEVICE_NOSUPPORT://不支持的设备
//                    DeveloperLog.LogD("mdid 不支持的设备");
                    break;
                case INIT_ERROR_LOAD_CONFIGFILE://加载配置文件出错
//                    DeveloperLog.LogD("mdid 加载配置文件出错");
                    break;
                case INIT_ERROR_MANUFACTURER_NOSUPPORT://不支持的设备厂商
//                    DeveloperLog.LogD("mdid 不支持的设备厂商");
                    break;
                case INIT_ERROR_RESULT_DELAY://获取接口是异步的，结果会在回调中返回，回调执行的回调可能在工作线程
//                    DeveloperLog.LogD("mdid 获取接口是异步的，结果会在回调中返回，回调执行的回调可能在工作线程");
                    break;
                case INIT_HELPER_CALL_ERROR://反射调用出错
//                    DeveloperLog.LogD("mdid 反射调用出错");
                    break;
            }
        } catch (Throwable e) {
//            DeveloperLog.LogE("MDIDHandler", e);
        }
    }

    public static String getMdid() {
        return !TextUtils.isEmpty(MDID) ? MDID : "";
    }

}
