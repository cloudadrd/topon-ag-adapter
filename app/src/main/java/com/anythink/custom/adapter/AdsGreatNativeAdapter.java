package com.anythink.custom.adapter;

import android.content.Context;
import android.text.TextUtils;

import com.adsgreat.base.callback.EmptyAdEventListener;
import com.adsgreat.base.config.Const;
import com.adsgreat.base.core.AGNative;
import com.adsgreat.base.core.AdsgreatSDK;
import com.adsgreat.base.core.AdsgreatSDKInternal;
import com.adsgreat.base.core.AdvanceNative;
import com.anythink.nativead.unitgroup.api.CustomNativeAdapter;

import java.util.Map;

public class AdsGreatNativeAdapter extends CustomNativeAdapter {

    private static String TAG = "OM-AG-Native:";

    private String slotId;

    private AdsGreatNativeExpressAd nativeExpressAd;

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

        Object customIdObj = serverExtra.get("custom_id");
        if (customIdObj instanceof String) {
            if (!TextUtils.isEmpty((String) customIdObj)) {
                AdsgreatSDKInternal.setUserId((String) customIdObj);
            }
        }
        AdsgreatSDK.initialize(context, appId);
        loadNativeAd(context, slotId);

    }

    private void loadNativeAd(Context context, String codeId) {
        AdsgreatSDK.getNativeAd(codeId, context, new NativeListener());
    }

    public class NativeListener extends EmptyAdEventListener {

        @Override
        public void onReceiveAdSucceed(AGNative agNative) {
            if (!(agNative instanceof AdvanceNative)) {
                return;
            }
            AdvanceNative advanceNative = (AdvanceNative) agNative;
            nativeExpressAd = new AdsGreatNativeExpressAd(advanceNative);
            if (mLoadListener != null) {
                mLoadListener.onAdCacheLoaded(nativeExpressAd);
            }
        }

        @Override
        public void onReceiveAdFailed(AGNative agNative) {
            String msg = null;
            if (agNative != null) {
                msg = agNative.getErrorsMsg();
            }
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError(TAG, msg);
            }
        }

        @Override
        public void onAdClicked(AGNative agNative) {
            if (nativeExpressAd != null) {
                nativeExpressAd.onClick();
            }
        }
    }


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
