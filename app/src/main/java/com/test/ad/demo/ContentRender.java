package com.test.ad.demo;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.anythink.nativead.api.ATNativeAdRenderer;
import com.anythink.nativead.api.ATNativeImageView;
import com.anythink.nativead.unitgroup.api.CustomNativeAd;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */

public class ContentRender implements ATNativeAdRenderer<CustomNativeAd> {
    private View mediaView;
    View mDevelopView;
    int mNetworkType;

    @Override
    public View createView(Context context, int networkType) {

        if (mDevelopView == null) {
            mDevelopView = LayoutInflater.from(context).inflate(R.layout.content_ad, null);
        }
        mNetworkType = networkType;
        if (mDevelopView.getParent() != null) {
            ((ViewGroup) mDevelopView.getParent()).removeView(mDevelopView);
        }
        return mDevelopView;
    }

    @Override
    public void renderAdView(View view, CustomNativeAd ad) {
        mediaView = ad.getAdMediaView(new Object[]{null});
    }

    public Fragment getContentFragment() {
        if (null != mediaView) {
            Fragment fragment = (Fragment) mediaView.getTag();
            mediaView.setTag(null);
            return fragment;
        }
        return null;
    }

    public void destoryRender() {
        if (null == mediaView) {
            mediaView.setTag(null);
            mediaView = null;
        }
        if (null == mDevelopView) {
            mDevelopView = null;
        }
    }

}
