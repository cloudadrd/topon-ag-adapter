package com.anythink.custom.adapter;

import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.adsgreat.base.core.AdvanceNative;
import com.anythink.nativead.unitgroup.api.CustomNativeAd;
import com.jd.ad.sdk.imp.feed.FeedAd;

import java.util.List;

public class JDNativeAd extends CustomNativeAd {

    private View view;

    public JDNativeAd(View view) {
        this.view = view;
    }

    @Override
    public boolean isNativeExpress() {
        return true;
    }

    @Override
    public View getAdMediaView(Object... objects) {
        return view;
    }

    @Override
    public View getAdIconView() {
        return super.getAdIconView();
    }

    @Override
    public void prepare(View view, FrameLayout.LayoutParams layoutParams) {
        super.prepare(view, layoutParams);
    }

    @Override
    public void prepare(View view, List<View> list, FrameLayout.LayoutParams layoutParams) {
        super.prepare(view, list, layoutParams);
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
