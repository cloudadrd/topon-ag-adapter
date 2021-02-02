/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.test.ad.demo;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.anythink.nativead.api.ATNativeAdRenderer;
import com.anythink.nativead.api.ATNativeImageView;
import com.anythink.nativead.unitgroup.api.CustomNativeAd;

import java.util.ArrayList;
import java.util.List;


public class NativeDemoRender implements ATNativeAdRenderer<CustomNativeAd> {

    Context mContext;
    List<View> mClickView = new ArrayList<>();

    public NativeDemoRender(Context context) {
        mContext = context;
    }

    View mDevelopView;

    int mNetworkType;

    @Override
    public View createView(Context context, int networkType) {
        if (mDevelopView == null) {
            mDevelopView = LayoutInflater.from(context).inflate(R.layout.native_ad_item, null);
        }
        mNetworkType = networkType;
        if (mDevelopView.getParent() != null) {
            ((ViewGroup) mDevelopView.getParent()).removeView(mDevelopView);
        }
        return mDevelopView;
    }

    @Override
    public void renderAdView(View view, CustomNativeAd ad) {
        mClickView.clear();
        TextView titleView = (TextView) view.findViewById(R.id.native_ad_title);
        TextView descView = (TextView) view.findViewById(R.id.native_ad_desc);
        TextView ctaView = (TextView) view.findViewById(R.id.native_ad_install_btn);
        TextView adFromView = (TextView) view.findViewById(R.id.native_ad_from);
        FrameLayout contentArea = (FrameLayout) view.findViewById(R.id.native_ad_content_image_area);
        FrameLayout iconArea = (FrameLayout) view.findViewById(R.id.native_ad_image);
        final ATNativeImageView logoView = (ATNativeImageView) view.findViewById(R.id.native_ad_logo);

        titleView.setText("");
        descView.setText("");
        ctaView.setText("");
        adFromView.setText("");
        titleView.setText("");
        contentArea.removeAllViews();
        iconArea.removeAllViews();
        logoView.setImageDrawable(null);

        View mediaView = ad.getAdMediaView(contentArea, contentArea.getWidth());

        if (ad.isNativeExpress()) {//是 个性化模板
            titleView.setVisibility(View.GONE);
            descView.setVisibility(View.GONE);
            ctaView.setVisibility(View.GONE);
            logoView.setVisibility(View.GONE);
            iconArea.setVisibility(View.GONE);
            if (mediaView.getParent() != null) {
                ((ViewGroup) mediaView.getParent()).removeView(mediaView);
            }

            contentArea.addView(mediaView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
            return;
        }


        titleView.setVisibility(View.VISIBLE);
        descView.setVisibility(View.VISIBLE);
        ctaView.setVisibility(View.VISIBLE);
        logoView.setVisibility(View.VISIBLE);
        iconArea.setVisibility(View.VISIBLE);


        View adiconView = ad.getAdIconView();


        final ATNativeImageView iconView = new ATNativeImageView(mContext);
        if (adiconView == null) {
            iconArea.addView(iconView);
            iconView.setImage(ad.getIconImageUrl());
            mClickView.add(iconView);
        } else {
            iconArea.addView(adiconView);
        }


        if (!TextUtils.isEmpty(ad.getAdChoiceIconUrl())) {
            logoView.setImage(ad.getAdChoiceIconUrl());
        } else {
//            logoView.setImageResource(R.drawable.ad_logo);
        }


        if (mediaView != null) {
            if (mediaView.getParent() != null) {
                ((ViewGroup) mediaView.getParent()).removeView(mediaView);
            }

            contentArea.addView(mediaView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        } else {

            ATNativeImageView imageView = new ATNativeImageView(mContext);
            imageView.setImage(ad.getMainImageUrl());

            ViewGroup.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(params);
            contentArea.addView(imageView, params);

            mClickView.add(imageView);
        }

        titleView.setText(ad.getTitle());
        descView.setText(ad.getDescriptionText());

        if (!TextUtils.isEmpty(ad.getCallToActionText())) {
            ctaView.setVisibility(View.VISIBLE);
            ctaView.setText(ad.getCallToActionText());
            mClickView.add(ctaView);
        } else {
            ctaView.setVisibility(View.GONE);
        }


        if (!TextUtils.isEmpty(ad.getAdFrom())) {
            adFromView.setText(ad.getAdFrom() != null ? ad.getAdFrom() : "");
            adFromView.setVisibility(View.VISIBLE);
        } else {
            adFromView.setVisibility(View.GONE);
        }

        mClickView.add(titleView);
        mClickView.add(descView);

    }

    public List<View> getClickView() {
        return mClickView;
    }
}
