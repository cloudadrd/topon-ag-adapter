package com.anythink.custom.adapter;

import android.content.Context;
import android.text.TextUtils;

import com.adsgreat.base.config.Const;
import com.anythink.nativead.unitgroup.api.CustomNativeAdapter;
import com.bun.miitmdid.core.MdidSdkHelper;
import com.bun.miitmdid.interfaces.IIdentifierListener;
import com.bun.miitmdid.interfaces.IdSupplier;
import com.shu.priory.IFLYAdSDK;
import com.shu.priory.IFLYNativeAd;
import com.shu.priory.config.AdError;
import com.shu.priory.config.AdKeys;
import com.shu.priory.conn.NativeDataRef;
import com.shu.priory.listener.IFLYNativeListener;

import java.util.Map;

public class IFLYNativeAdapter extends CustomNativeAdapter {

    private static String TAG = "OM-AG-Native:";

    private String slotId;

    private IFLYNative nativeExpressAd;

    private String oaid = "";

    @Override
    public void loadCustomNetworkAd(final Context context, Map<String, Object> serverExtra, final Map<String, Object> localExtra) {

        String appId = (String) serverExtra.get("app_id");
        slotId = (String) serverExtra.get("slot_id");

        if (TextUtils.isEmpty(appId) || TextUtils.isEmpty(slotId)) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError(TAG, "app_id or slot_id is empty!");
            }
            return;
        }
        MdidSdkHelper.InitSdk(context.getApplicationContext(), true, new IIdentifierListener() {
            @Override
            public void OnSupport(boolean b, final IdSupplier idSupplier) {
                if (idSupplier != null && idSupplier.isSupported()) {
                    oaid = idSupplier.getOAID();
                }
            }
        });
        //进行SDK初始化
        IFLYAdSDK.init(context.getApplicationContext());

        loadNativeAd(context, slotId);

    }

    private void loadNativeAd(Context context, String codeId) {
        //创建信息流广告：adUnitId：开发者在讯飞AI营销云平台(http://www.voiceads.cn/)申请的信息流广告位ID
        IFLYNativeAd nativeAd = new IFLYNativeAd(context, codeId, mListener);

//设置oaid，获取方式见 -> 4.常见问题
        nativeAd.setParameter(AdKeys.OAID, oaid);

//请求广告
        nativeAd.loadAd();


    }

    IFLYNativeListener mListener = new IFLYNativeListener() {
        @Override
        public void onAdFailed(AdError error) {
            // 广告请求失败
            String msg = null;
            if (error != null) {
                msg = error.getErrorDescription();
            }
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError(TAG, msg);
            }
        }

        @Override
        public void onAdLoaded(NativeDataRef dataRef) {
            if (dataRef == null) {
                return;
            }
            // 广告请求成功,后续操作参考Demo
            nativeExpressAd = new IFLYNative(dataRef);
            if (mLoadListener != null) {
                mLoadListener.onAdCacheLoaded(nativeExpressAd);
            }
        }

        @Override
        public void onCancel() {
            // 下载类广告，下载提示框 “取消”
        }

        @Override
        public void onConfirm() {
            // 下载类广告，下载提示框 “确认”
        }
    };


    @Override
    public String getNetworkName() {
        return "AdsGreat Custom";
    }

    @Override
    public void destory() {

    }


    @Override
    public String getNetworkPlacementId() {
        return slotId;
    }

    @Override
    public String getNetworkSDKVersion() {
        return Const.getVersionNumber();
    }
}
