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
import android.widget.FrameLayout;
import android.widget.Toast;

import com.anythink.core.api.ATAdInfo;
import com.anythink.nativead.splash.api.ATNativeSplash;
import com.anythink.nativead.splash.api.ATNativeSplashListener;
import com.anythink.network.toutiao.TTATConst;

import java.util.HashMap;
import java.util.Map;

public class NativeSplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_native_splash);
        FrameLayout splashView = findViewById(R.id.native_splash_view);
        Map<String, Object> localMap = new HashMap<>();
        //For Pangle
        localMap.put(TTATConst.NATIVE_AD_IMAGE_WIDTH, getResources().getDisplayMetrics().widthPixels - dip2px(20));
        localMap.put(TTATConst.NATIVE_AD_IMAGE_HEIGHT, dip2px(200));

        ATNativeSplash splash = new ATNativeSplash(this, splashView, null, DemoApplicaion.mPlacementId_native_all, localMap, new ATNativeSplashListener() {
            @Override
            public void onAdLoaded() {
                Log.i("SplashActivity", "Develop callback loaded");
            }

            @Override
            public void onNoAdError(String msg) {
                Log.i("NativeSplashActivity", "Develop callback onNoAdError :" + msg);
                Toast.makeText(NativeSplashActivity.this, "load ad error: " + msg, Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onAdShow(ATAdInfo entity) {
                Log.i("NativeSplashActivity", "Develop callback onAdShow:" + entity.toString());
            }

            @Override
            public void onAdClick(ATAdInfo entity) {
                Log.i("NativeSplashActivity", "Develop callback onAdClick:" + entity.toString());
            }

            @Override
            public void onAdSkip() {
                Log.i("NativeSplashActivity", "Develop callback onAdSkip");
                finish();
            }

            @Override
            public void onAdTimeOver() {
                Log.i("NativeSplashActivity", "Develop callback onAdTimeOver");
                finish();
            }

            @Override
            public void onAdTick(long millisUtilFinished) {
                Log.i("NativeSplashActivity", "Develop callback onAdTick:" + millisUtilFinished);
            }
        });

    }

    public int dip2px(float dipValue) {
        float scale = this.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
