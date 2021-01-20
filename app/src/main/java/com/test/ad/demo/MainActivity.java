package com.test.ad.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends Activity {

    CacheWebView cacheWebView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cacheWebView = new CacheWebView(this);
        cacheWebView.loadUrl("file:///android_asset/test.html");
        findViewById(R.id.nativeAdBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NativeAdActivity.class));
            }
        });

        findViewById(R.id.rewardedVideoBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RewardVideoAdActivity.class));
            }
        });

        findViewById(R.id.interstitialBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, InterstitialAdActivity.class));
            }
        });

        findViewById(R.id.bannerBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, BannerAdActivity.class));
            }
        });

        findViewById(R.id.splashBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SplashAdActivity.class));
            }
        });

        findViewById(R.id.nativeBannerAdBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NativeBannerActivity.class));
            }
        });

        findViewById(R.id.nativeSplashAdBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NativeSplashActivity.class));
            }
        });

        findViewById(R.id.nativeListBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NativeListActivity.class));
            }
        });

        findViewById(R.id.webviewBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cacheWebView.getParent() != null && cacheWebView.getParent() instanceof ViewGroup) {
                    ViewGroup viewGroup = (ViewGroup) cacheWebView.getParent();
                    viewGroup.removeView(cacheWebView);
                }
                InnerWebViewActivity.launch(MainActivity.this, cacheWebView);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cacheWebView.destroy();
    }
}
