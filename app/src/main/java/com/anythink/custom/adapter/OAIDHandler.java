package com.anythink.custom.adapter;

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
public class OAIDHandler {


    public static String OA_ID = null;

    private final static String TAG = "OAIDHandler";

    public static void init(Context context) {
        if (Build.VERSION.SDK_INT < 21) {
            return;
        }
        try {
            MdidSdkHelper.InitSdk(context, true, new IIdentifierListener() {
                @Override
                public void OnSupport(boolean b, IdSupplier idSupplier) {
                    if (idSupplier == null) {
                        return;
                    }
                    String oaid = idSupplier.getOAID();
                    if (oaid.equals("NO") || TextUtils.isEmpty(oaid)) {
                        oaid = "";
                    }
                    OA_ID = oaid;
                    Log.d(TAG, "mdid: value=" + OA_ID);
                }
            });
        } catch (Throwable e) {
            Log.d(TAG, e.getMessage(), e);
        }
    }

    public static String getOAID() {
        return OA_ID;
    }
}
