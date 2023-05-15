package com.anythink.custom.adapter;

import android.content.Context;
import android.text.TextUtils;

import com.anythink.nativead.unitgroup.api.CustomNativeAdapter;
import com.growstarry.kern.callback.EmptyAdEventListener;
import com.growstarry.kern.config.Const;
import com.growstarry.kern.core.GTAdvanceNative;
import com.growstarry.kern.core.GTNative;
import com.growstarry.kern.core.GrowsTarryInternal;
import com.growstarry.kern.core.GrowsTarrySDK;

import java.util.Map;

public class GrowStarryNativeAdapter extends CustomNativeAdapter {

    private static String TAG = "OM-AG-Native:";

    private String slotId;

    private GrowStarryNativeAd nativeExpressAd;

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
                GrowsTarryInternal.setUserId((String) customIdObj);
            }
        }
        GrowsTarrySDK.initialize(context, appId);
        loadNativeAd(context, slotId);

    }

    private void loadNativeAd(Context context, String codeId) {
        GrowsTarrySDK.getNativeAd(codeId, context, new NativeListener());
    }

    public class NativeListener extends EmptyAdEventListener {

        @Override
        public void onReceiveAdSucceed(GTNative agNative) {
            if (!(agNative instanceof GTAdvanceNative)) {
                return;
            }
            GTAdvanceNative advanceNative = (GTAdvanceNative) agNative;
            nativeExpressAd = new GrowStarryNativeAd(advanceNative);
            if (mLoadListener != null) {
                mLoadListener.onAdCacheLoaded(nativeExpressAd);
            }
        }

        @Override
        public void onReceiveAdFailed(GTNative agNative) {
            String msg = null;
            if (agNative != null) {
                msg = agNative.getErrorsMsg();
            }
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError(TAG, msg);
            }
        }

        @Override
        public void onAdClicked(GTNative agNative) {
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
