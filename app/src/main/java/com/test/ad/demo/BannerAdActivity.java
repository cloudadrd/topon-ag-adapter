/*
 * Copyright © 2018-2020 TopOn. All rights reserved.
 * https://www.toponad.com
 * Licensed under the TopOn SDK License Agreement
 * https://github.com/toponteam/TopOn-Android-SDK/blob/master/LICENSE
 */

package com.test.ad.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.anythink.banner.api.ATBannerExListener;
import com.anythink.banner.api.ATBannerView;
import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.AdError;
import com.business.support.YMBusinessService;
import com.business.support.utils.Utils;

public class BannerAdActivity extends Activity {

    private static final String TAG = BannerAdActivity.class.getSimpleName();

    String placementIds[] = new String[]{
            DemoApplicaion.mPlacementId_banner_all
            , DemoApplicaion.mPlacementId_banner_GDT
            , DemoApplicaion.mPlacementId_banner_toutiao
            , DemoApplicaion.mPlacementId_banner_mintegral
            , DemoApplicaion.mPLacementId_banner_baidu
            , DemoApplicaion.mPlacementId_banner_myoffer
            , DemoApplicaion.mPlacementId_banner_IFLY
    };

    String unitGroupName[] = new String[]{
            "All",
            "GDT",
            "Toutiao",
            "Mintegral",
            "Baidu",
            "MyOffer",
            "IFLY"
    };

    ATBannerView mBannerView;

    int mCurrentSelectIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_banner);

        Spinner spinner = (Spinner) findViewById(R.id.banner_spinner);
        final FrameLayout frameLayout = findViewById(R.id.adview_container);
        mBannerView = new ATBannerView(this);
        mBannerView.setPlacementId(placementIds[mCurrentSelectIndex]);

        FrameLayout bannerStyleLayout = YMBusinessService.getBannerViewByStyle();

        if (bannerStyleLayout != null) {
            bannerStyleLayout.addView(mBannerView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
            frameLayout.addView(bannerStyleLayout, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        } else {
            frameLayout.addView(mBannerView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        }

        mBannerView.setBannerAdListener(new ATBannerExListener() {

            @Override
            public void onDeeplinkCallback(boolean isRefresh, ATAdInfo adInfo, boolean isSuccess) {
                Log.i(TAG, "onDeeplinkCallback:" + adInfo.toString() + "--status:" + isSuccess);
            }

            @Override
            public void onBannerLoaded() {
                Log.i(TAG, "onBannerLoaded");
                Toast.makeText(BannerAdActivity.this,
                        "onBannerLoaded",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBannerFailed(AdError adError) {
                Log.i(TAG, "onBannerFailed: " + adError.getFullErrorInfo());
                Toast.makeText(BannerAdActivity.this,
                        "onBannerFailed: " + adError.getFullErrorInfo(),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBannerClicked(ATAdInfo entity) {
                Log.i(TAG, "onBannerClicked:" + entity.toString());
                Toast.makeText(BannerAdActivity.this,
                        "onBannerClicked",
                        Toast.LENGTH_SHORT).show();

                //点击日志code start

                //上报日志时需要带上的数据
                String customBannerStyle = YMBusinessService.getCustomBannerStyle();
                //上报日志时的逻辑
                if (customBannerStyle != null) {
                    //jsonObject.put("state", customBannerStyle);
                }

                //点击日志code end
            }

            @Override
            public void onBannerShow(ATAdInfo entity) {
                Log.i(TAG, "onBannerShow:" + entity.toString());
                Toast.makeText(BannerAdActivity.this,
                        "onBannerShow",
                        Toast.LENGTH_SHORT).show();
                //展示日志code start

                //上报日志时需要带上的数据
                String customBannerStyle = YMBusinessService.getCustomBannerStyle();
                //上报日志时的逻辑
                if (customBannerStyle != null) {
                    //jsonObject.put("state", customBannerStyle);
                }

                //展示日志code end
            }

            @Override
            public void onBannerClose(ATAdInfo entity) {
                Log.i(TAG, "onBannerClose:" + entity.toString());
                Toast.makeText(BannerAdActivity.this,
                        "onBannerClose",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBannerAutoRefreshed(ATAdInfo entity) {
                Log.i(TAG, "onBannerAutoRefreshed:" + entity.toString());
            }

            @Override
            public void onBannerAutoRefreshFail(AdError adError) {
                Log.i(TAG, "onBannerAutoRefreshFail: " + adError.getFullErrorInfo());

            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                BannerAdActivity.this, android.R.layout.simple_spinner_dropdown_item,
                unitGroupName);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Toast.makeText(BannerAdActivity.this,
                        parent.getItemAtPosition(position).toString(),
                        Toast.LENGTH_SHORT).show();
                mCurrentSelectIndex = position;
                mBannerView.setPlacementId(placementIds[mCurrentSelectIndex]);
                mBannerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        findViewById(R.id.loadAd_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBannerView.setPlacementId(placementIds[mCurrentSelectIndex]);
                mBannerView.loadAd();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBannerView != null) {
            mBannerView.destroy();
        }
    }

    public int dip2px(float dipValue) {
        float scale = this.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
