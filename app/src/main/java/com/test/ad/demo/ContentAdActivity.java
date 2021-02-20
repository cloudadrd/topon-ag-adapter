package com.test.ad.demo;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.anythink.core.api.AdError;
import com.anythink.nativead.api.ATNative;
import com.anythink.nativead.api.ATNativeAdView;
import com.anythink.nativead.api.ATNativeNetworkListener;
import com.anythink.nativead.api.NativeAd;

public class ContentAdActivity extends AppCompatActivity {

    private static String TAG = "ContentAdActivity";
    ATNativeAdView anyThinkNativeAdView;
    ATNative ATContentAd;
    NativeAd mNativeAd;
    private FragmentTransaction fragmentTransaction;
    private ContentRender anyThinkRender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        requestContentAd();

    }


    private void  requestContentAd(){
        if (null != ATContentAd && null != ATContentAd.getNativeAd()) {
            ATContentAd.getNativeAd().destory();
        }

        ATContentAd = new ATNative(this,DemoApplicaion. mPlacementId_content_KS, new ATNativeNetworkListener() {
            @Override
            public void onNativeAdLoaded() {
                if(null != anyThinkRender) {
                    anyThinkRender.destoryRender();
                    anyThinkRender =null;
                }
                if (null != mNativeAd){
                    mNativeAd.destory();
                }

                anyThinkRender = new ContentRender();
                mNativeAd = ATContentAd.getNativeAd();
                if (null != mNativeAd) {
                    anyThinkNativeAdView = new ATNativeAdView(ContentAdActivity.this);
                    mNativeAd.renderAdView(anyThinkNativeAdView,anyThinkRender);
                }
                Fragment contentFragemet = anyThinkRender.getContentFragment();
                if (null != contentFragemet) {
                    fragmentTransaction = ContentAdActivity.this.getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.ad_media, contentFragemet).commit();
                }
                Log.i(TAG, "onNativeAdLoaded");
                Toast.makeText(ContentAdActivity.this, "load success..."
                        , Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNativeAdLoadFail(AdError adError) {
                Log.i(TAG, "onNativeAdLoadFail, " + adError.printStackTrace());
                Toast.makeText(ContentAdActivity.this, "load fail...：" + adError.printStackTrace(), Toast.LENGTH_LONG).show();

            }
        });

        ATContentAd.makeAdRequest();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null!= anyThinkNativeAdView){
            anyThinkNativeAdView = null;
        }
        if (null!= ATContentAd){
            ATContentAd = null;
        }
        if (null!= mNativeAd){
            mNativeAd = null;
        }
        if (null!= fragmentTransaction){
            fragmentTransaction = null;
        }

        if (null!= anyThinkRender){
            anyThinkRender = null;
        }
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public int dip2px(float dipValue) {
        float scale = this.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
