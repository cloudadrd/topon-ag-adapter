package com.test.ad.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.webkit.WebView;

import com.anythink.core.api.ATSDK;
import com.anythink.custom.adapter.OAIDHandler;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.activity.base.TTRewardVideoActivity;
import com.facebook.stetho.Stetho;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Z on 2018/1/10.
 */

public class DemoApplicaion extends MultiDexApplication {
    public static final String appid = "a6018fd6ba9165"; //"a6018fd6ba9165";//"a5ff2b6c1c6fc5";
    public static final String appKey = "be9b2e39d03dd60ed17870594123d7f4";
    public static final String mPlacementId_native_all = "b5fb222b3279b0";
    public static final String mPlacementId_native_mintegral = "b5aa1fa85b86d5";
    public static final String mPLacementId_native_automatic_rending_mintegral = "b5ee8aeb8f3458";
    public static final String mPlacementId_native_GDT = "b5ab8590d44f82";
    public static final String mPlacementId_native_toutiao = "b5c2c97629da0d";
    public static final String mPlacementId_native_toutiao_drawer = "b5c355d79ef9be";
    public static final String mPlacementId_native_baidu = "b60360e3ced743";
    public static final String mPlacementId_native_kuaishou = "b5e4105d4f21b6";
    public static final String mPlacementId_native_kuaishou_drawer = "b5e5dc4110310f";
    public static final String mPlacementId_native_oneway = "b5f22761b35766";
    public static final String mPlacementId_native_myoffer = "b5f33a12982b7f";
    public static final String mPlacementId_content_KS = "b600a40062eae1";
    public static final String mPlacementId_native_JD = "b601f9516ed70f";
    public static final String mPlacementId_native_IFLY = "b604887777333b";


    //RewardedVideo
    public static final String mPlacementId_rewardvideo_all = "b6018fd98a9b7e";
    public static final String mPlacementId_rewardvideo_mintegral = "b5b449f2f58cd7";
    public static final String mPlacementId_rewardvideo_GDT = "b5c2c880cb9d52";
    public static final String mPlacementId_rewardvideo_toutiao = "b5b728e7a08cd4";
    public static final String mPlacementId_rewardvideo_uniplay = "b5badef36435e7";
    public static final String mPlacementId_rewardvideo_oneway = "b5badf5b390201";
    public static final String mPlacementId_rewardvideo_ksyun = "b5bbd61d0aa571";
    public static final String mPlacementId_rewardvideo_baidu = "b5c2c800fb3a52";
    public static final String mPlacementId_rewardvideo_ks = "b5d67459a3e535";
    public static final String mPlacementId_rewardvideo_sigmob = "b5d7228c6c5d6a";
    public static final String mPlacementId_rewardvideo_myoffer = "b5db6c3764aea3";

    //Banner
    public static final String mPlacementId_banner_all = "b5baca4f74c3d8";
    public static final String mPlacementId_banner_mintegral = "b5dd388839bf5e";
    public static final String mPlacementId_banner_GDT = "b5baca43951901";
    public static final String mPlacementId_banner_toutiao = "b5baca45138428";
    public static final String mPlacementId_banner_uniplay = "b5baca4aebcb93";
    public static final String mPLacementId_banner_baidu = "b5c0508c4c073f";
    public static final String mPlacementId_banner_myoffer = "b5f33a1409b96b";
    public static final String mPlacementId_banner_IFLY = "b6048875baf056";

    //Interstitial
    public static final String mPlacementId_interstitial_all = "b609bb0238e690";
    public static final String mPlacementId_interstitial_mintegral = "b5bbdc725768fa";
    public static final String mPlacementId_interstitial_video_mintegral = "b5bbdc855a1506";
    public static final String mPlacementId_interstitial_GDT = "b5baca561bc100";
    public static final String mPlacementId_interstitial_toutiao = "b5baca585a8fef";
    public static final String mPlacementId_interstitial_video_toutiao = "b5baca599c7c61";
    public static final String mPlacementId_interstitial_uniplay = "b5baca5d16c597";
    public static final String mPlacementId_interstitial_oneway = "b5baca5e3d2b29";
    public static final String mPlacementId_interstitial_baidu = "b5c0508e2c84d4";
    public static final String mPlacementId_interstitial_kuaishou = "b5d6745b8133f2";
    public static final String mPlacementId_interstitial_sigmob = "b5d7614ab30695";
    public static final String mPlacementId_interstitial_myoffer = "b5db6c39aed9c5";
    public static final String mPlacementId_interstitial_AdsGreat = "b5fb222c36cb86";
    public static final String mPlacementId_interstitial_JD = "b601f94e7b83b4";

    //Splash
    public static final String mPlacementId_splash_all = "b5fb2229fdd388";
    public static final String mPlacementId_splash_gdt = "b5fb2229fdd388";
    public static final String mPlacementId_splash_toutiao = "b5fb2229fdd388";
    public static final String mPlacementId_splash_baidu = "b5fb2229fdd388";
    public static final String mPlacementId_splash_sigmob = "b5fb2229fdd388";
    public static final String mPlacementId_splash_mintegral = "b5fb2229fdd388";
    public static final String mPlacementId_splash_kuaishou = "b5fb2229fdd388";
    public static final String mPlacementId_splash_AdsGreat = "b5fb2229fdd388";
    public static final String mPlacementId_splash_myoffer = "b5fb2229fdd388";
    public static final String mPlacementId_splash_JD = "b601f94b030f4b";
    public static final String mPlacementId_splash_IFLY = "b604887461e0f4";

    @Override
    public void onCreate() {
        super.onCreate();
//        JacocoHelper.Builder builder = new JacocoHelper.Builder();
//        builder.setApplication(this).setDebuggable(true);
//        JacocoHelper.initialize(builder.build());

        //Android 9 or above must be set
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = getProcessName();
            if (!getPackageName().equals(processName)) {
                WebView.setDataDirectorySuffix(processName);
            }
        }

        Stetho.initializeWithDefaults(getApplicationContext());
        ATSDK.setNetworkLogDebug(true);
        ATSDK.integrationChecking(getApplicationContext());

        Map<String, Object> custommap = new HashMap<String, Object>();
        custommap.put("key1", "initCustomMap1");
        custommap.put("key2", "initCustomMap2");
        ATSDK.initCustomMap(custommap);

        Map<String, Object> subcustommap = new HashMap<String, Object>();
        subcustommap.put("key1", "initPlacementCustomMap1");
        subcustommap.put("key2", "initPlacementCustomMap2");
        ATSDK.initPlacementCustomMap("b5aa1fa4165ea3", subcustommap);//native  facebook

        ATSDK.setChannel("testChannle");
        ATSDK.setSubChannel("testSubChannle");

//        ATSDK.init(this, appid, appKey);


//        ATSDK.init(this, "a5ff2b6c1c6fc5", "be9b2e39d03dd60ed17870594123d7f4");
        ATSDK.init(this, appid, appKey);


        OAIDHandler.init(this);

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                Log.e("DemoApplicaion", "onActivityCreated activity=" + activity.getComponentName());
                if (activity instanceof TTRewardVideoActivity) {

                    Intent intent = activity.getIntent();
                    StringBuilder sb = new StringBuilder();
                    if (intent != null) {
                        String aP = intent.getStringExtra("reward_name");
                        int aQ = intent.getIntExtra("reward_amount", 0);
                        String aR = intent.getStringExtra("media_extra");
                        String aS = intent.getStringExtra("user_id");
                        boolean v = intent.getBooleanExtra("show_download_bar", true);
                        String x = intent.getStringExtra("video_cache_url");
                        int y = intent.getIntExtra("orientation", 2);
                        String ab = intent.getStringExtra("rit_scene");

                        String stringExtra = intent.getStringExtra(TTAdConstant.MULTI_PROCESS_MATERIALMETA);

                        sb.append("reward_name=").append(aP).append(",");
                        sb.append("reward_amount=").append(aQ).append(",");
                        sb.append("media_extra=").append(aR).append(",");
                        sb.append("user_id=").append(aS).append(",");
                        sb.append("show_download_bar=").append(v).append(",");
                        sb.append("video_cache_url=").append(x).append(",");
                        sb.append("orientation=").append(y).append(",");
                        sb.append("rit_scene=").append(ab).append(",");
                        sb.append("stringExtra=").append(stringExtra);
                    }
                    String bundleStr = "";
                    if (savedInstanceState != null) {
                        bundleStr = savedInstanceState.toString();
                    }

                    Object c1 = com.bytedance.sdk.openadsdk.core.t.a().c();
                    String str1 = "";
                    if (c1 != null) {
                        str1 = com.bytedance.sdk.openadsdk.core.t.a().c().aG().toString();
                    }
                    Log.e("DemoApplicaion", "onActivityCreated  is TTRewardVideoActivity stringExtra" + bundleStr + "\n bundleStr=" + sb.toString() + "\n str1=" + str1);
                }
            }


            public void onActivityPreCreated(@NonNull Activity activity,
                                              @Nullable Bundle savedInstanceState) {
                if (activity instanceof TTRewardVideoActivity) {

                    Intent intent = activity.getIntent();
                    StringBuilder sb = new StringBuilder();
                    if (intent != null) {
                        String aP = intent.getStringExtra("reward_name");
                        int aQ = intent.getIntExtra("reward_amount", 0);
                        String aR = intent.getStringExtra("media_extra");
                        String aS = intent.getStringExtra("user_id");
                        boolean v = intent.getBooleanExtra("show_download_bar", true);
                        String x = intent.getStringExtra("video_cache_url");
                        int y = intent.getIntExtra("orientation", 2);
                        String ab = intent.getStringExtra("rit_scene");

                        String stringExtra = intent.getStringExtra(TTAdConstant.MULTI_PROCESS_MATERIALMETA);

                        sb.append("reward_name=").append(aP).append(",");
                        sb.append("reward_amount=").append(aQ).append(",");
                        sb.append("media_extra=").append(aR).append(",");
                        sb.append("user_id=").append(aS).append(",");
                        sb.append("show_download_bar=").append(v).append(",");
                        sb.append("video_cache_url=").append(x).append(",");
                        sb.append("orientation=").append(y).append(",");
                        sb.append("rit_scene=").append(ab).append(",");
                        sb.append("stringExtra=").append(stringExtra);
                    }
                    String bundleStr = "";
                    if (savedInstanceState != null) {
                        bundleStr = savedInstanceState.toString();
                    }

                    Object c1 = com.bytedance.sdk.openadsdk.core.t.a().c();
                    String str1 = "";
                    if (c1 != null) {
                        str1 = com.bytedance.sdk.openadsdk.core.t.a().c().aG().toString();
                    }
                    Log.e("DemoApplicaion", "onActivityPreCreated  is TTRewardVideoActivity stringExtra" + bundleStr + "\n bundleStr=" + sb.toString() + "\n str1=" + str1);
                }
            }
            @Override
            public void onActivityPostCreated(@NonNull Activity activity,
                                              @Nullable Bundle savedInstanceState) {
                if (activity instanceof TTRewardVideoActivity) {

                    Intent intent = activity.getIntent();
                    StringBuilder sb = new StringBuilder();
                    if (intent != null) {
                        String aP = intent.getStringExtra("reward_name");
                        int aQ = intent.getIntExtra("reward_amount", 0);
                        String aR = intent.getStringExtra("media_extra");
                        String aS = intent.getStringExtra("user_id");
                        boolean v = intent.getBooleanExtra("show_download_bar", true);
                        String x = intent.getStringExtra("video_cache_url");
                        int y = intent.getIntExtra("orientation", 2);
                        String ab = intent.getStringExtra("rit_scene");

                        String stringExtra = intent.getStringExtra(TTAdConstant.MULTI_PROCESS_MATERIALMETA);

                        sb.append("reward_name=").append(aP).append(",");
                        sb.append("reward_amount=").append(aQ).append(",");
                        sb.append("media_extra=").append(aR).append(",");
                        sb.append("user_id=").append(aS).append(",");
                        sb.append("show_download_bar=").append(v).append(",");
                        sb.append("video_cache_url=").append(x).append(",");
                        sb.append("orientation=").append(y).append(",");
                        sb.append("rit_scene=").append(ab).append(",");
                        sb.append("stringExtra=").append(stringExtra);
                    }
                    String bundleStr = "";
                    if (savedInstanceState != null) {
                        bundleStr = savedInstanceState.toString();
                    }

                    Object c1 = com.bytedance.sdk.openadsdk.core.t.a().c();
                    String str1 = "";
                    if (c1 != null) {
                        str1 = com.bytedance.sdk.openadsdk.core.t.a().c().aG().toString();
                    }
                    Log.e("DemoApplicaion", "onActivityPostCreated  is TTRewardVideoActivity stringExtra" + bundleStr + "\n bundleStr=" + sb.toString() + "\n str1=" + str1);
                }
            }


            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                Log.e("DemoApplicaion", "onActivityStarted ");
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                Log.e("DemoApplicaion", "onActivityResumed ");
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                Log.e("DemoApplicaion", "onActivityPaused ");
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                Log.e("DemoApplicaion", "onActivityStopped ");
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
                Log.e("DemoApplicaion", "onActivitySaveInstanceState ");
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                Log.e("DemoApplicaion", "onActivityDestroyed ");
            }

        });


    }

}
