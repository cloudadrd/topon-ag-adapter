package com.anythink.custom.adapter;

import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.anythink.nativead.unitgroup.api.CustomNativeAd;
import com.baidu.mobads.sdk.api.FeedNativeView;
import com.baidu.mobads.sdk.api.NativeResponse;

import java.util.List;

public class BaiduNativeAd extends CustomNativeAd {

    private FeedNativeView mView;

    private NativeResponse mNativeAd;
    private final static String TAG = "BaiduNativeAd";

    public BaiduNativeAd(FeedNativeView view, NativeResponse nativeAd) {
        this.mView = view;
        this.mNativeAd = nativeAd;
    }

    @Override
    public boolean isNativeExpress() {
        return true;
    }

    @Override
    public View getAdMediaView(Object... objects) {
        return mView;
    }

    @Override
    public View getAdIconView() {
        return super.getAdIconView();
    }

    @Override
    public void prepare(View view, FrameLayout.LayoutParams layoutParams) {
        super.prepare(view, layoutParams);
        prepare(view);
    }

    @Override
    public void prepare(View view, List<View> list, FrameLayout.LayoutParams layoutParams) {
        super.prepare(view, list, layoutParams);
        prepare(view);
    }

    public void prepare(View view) {

        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNativeAd.handleClick(v);
            }
        });
        mNativeAd.registerViewForInteraction(view, new NativeResponse.AdInteractionListener() {
            @Override
            public void onAdClick() {
                Log.d(TAG, "onAdClick");
                notifyAdClicked();
            }

            @Override
            public void onADExposed() {
                Log.d(TAG, "onADExposed");
                if (null != mNativeAd) {
                    mNativeAd.recordImpression(view);
                }
                notifyAdImpression();
            }

            @Override
            public void onADExposureFailed(int i) {

            }

            @Override
            public void onADStatusChanged() {
                Log.d(TAG, "onADStatusChanged");
            }

            @Override
            public void onAdUnionClick() {

            }
        });

        mNativeAd.setAdPrivacyListener(new NativeResponse.AdPrivacyListener() {
            @Override
            public void onADPrivacyClick() {
                Log.d(TAG, "onADPrivacyClick");
            }

            @Override
            public void onADPermissionShow() {
                Log.d(TAG, "onADPermissionShow");
            }

            @Override
            public void onADPermissionClose() {
                Log.d(TAG, "onADPermissionClose");
            }

        });
    }

    @Override
    public void destroy() {
        super.destroy();
    }


    public void onAdClicked() {
        notifyAdClicked();
    }

    public void onAdExposure() {
        notifyAdImpression();
    }

    public void onAdDismissed() {
        notifyAdDislikeClick();
    }
}
