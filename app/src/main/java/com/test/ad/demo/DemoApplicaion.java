package com.test.ad.demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.multidex.MultiDexApplication;

import com.anythink.core.api.ATSDK;
import com.anythink.custom.adapter.OAIDHandler;
import com.business.support.utils.Utils;
import com.facebook.stetho.Stetho;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Z on 2018/1/10.
 */

public class DemoApplicaion extends MultiDexApplication {
    public static final String appid = "a6018fd6ba9165";//"";a5ff2b9464c121
    public static final String appKey = "be9b2e39d03dd60ed17870594123d7f4";
    public static final String mPlacementId_native_all = "b6018fdc99f11e";
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
    public static final String mPlacementId_rewardvideo_all = "b6018fd98a9b7e";//b5ff41a9f64dec
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
    public static final String mPlacementId_banner_all = "b6018fdda46e4c";
    public static final String mPlacementId_banner_mintegral = "b5dd388839bf5e";
    public static final String mPlacementId_banner_GDT = "b5baca43951901";
    public static final String mPlacementId_banner_toutiao = "b5baca45138428";
    public static final String mPlacementId_banner_uniplay = "b5baca4aebcb93";
    public static final String mPLacementId_banner_baidu = "b5c0508c4c073f";
    public static final String mPlacementId_banner_myoffer = "b5f33a1409b96b";
    public static final String mPlacementId_banner_IFLY = "b6048875baf056";

    //Interstitial
    public static final String mPlacementId_interstitial_all = "b603f37c4ebe4e";
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
    public static final String mPlacementId_splash_all = "b6018fda915c06";
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
//        new CrashHandler().init(this);
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
            }


            public void onActivityPreCreated(@NonNull Activity activity,
                                             @Nullable Bundle savedInstanceState) {
//                if (activity instanceof TTRewardVideoActivity) {
//
////                    Intent intent = activity.getIntent();
////                    StringBuilder sb = new StringBuilder();
////                    if (intent != null) {
////                        String aP = intent.getStringExtra("reward_name");
////                        int aQ = intent.getIntExtra("reward_amount", 0);
////                        String aR = intent.getStringExtra("media_extra");
////                        String aS = intent.getStringExtra("user_id");
////                        boolean v = intent.getBooleanExtra("show_download_bar", true);
////                        String x = intent.getStringExtra("video_cache_url");
////                        int y = intent.getIntExtra("orientation", 2);
////                        String ab = intent.getStringExtra("rit_scene");
////
////                        String stringExtra = intent.getStringExtra(TTAdConstant.MULTI_PROCESS_MATERIALMETA);
////
////                        sb.append("reward_name=").append(aP).append(",");
////                        sb.append("reward_amount=").append(aQ).append(",");
////                        sb.append("media_extra=").append(aR).append(",");
////                        sb.append("user_id=").append(aS).append(",");
////                        sb.append("show_download_bar=").append(v).append(",");
////                        sb.append("video_cache_url=").append(x).append(",");
////                        sb.append("orientation=").append(y).append(",");
////                        sb.append("rit_scene=").append(ab).append(",");
////                        sb.append("stringExtra=").append(stringExtra);
////                    }
//                    Object c1 = com.bytedance.sdk.openadsdk.core.t.a().c();
//                    String materialMeta = "";
//                    JSONObject jsonObj = null;
//                    if (c1 != null) {
//                        jsonObj = com.bytedance.sdk.openadsdk.core.t.a().c().aO();
//                    }
//                    if (savedInstanceState != null) {
//                        materialMeta = savedInstanceState.getString("material_meta");
//                        if (!TextUtils.isEmpty(materialMeta)) {
//                            try {
//                                JSONObject tempObj = com.bytedance.sdk.openadsdk.core.b.a(new JSONObject(materialMeta)).aO();
//                                jsonObj = tempObj;
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//
//                    if (jsonObj != null) {
//                        String title = jsonObj.optString("title");
//                        String iconUrl = null;
//                        String appName = null;
//                        String packageName = null;
//                        String adId = null;
//                        String videoUrl = null;
//
//                        JSONObject iconObj = jsonObj.optJSONObject("icon");
//                        if (iconObj != null) {
//                            iconUrl = iconObj.optString("url");
//                        }
//
//                        JSONObject appObj = jsonObj.optJSONObject("app");
//                        if (appObj != null) {
//                            appName = appObj.optString("app_name");
//                            packageName = appObj.optString("package_name");
//                        }
//
//                        JSONObject extObj = jsonObj.optJSONObject("ext");
//
//                        if (extObj != null) {
//                            adId = extObj.optString("ad_id");
//                        }
//
//
//                        JSONObject videoObj = jsonObj.optJSONObject("video");
//
//                        if (videoObj != null) {
//                            videoUrl = videoObj.optString("video_url");
//                        }
//
//
//                    }
//                    Log.e("DemoApplicaion", "onActivityPreCreated  is TTRewardVideoActivity stringExtra" + bundleStr + "\n bundleStr=" + sb.toString() + "\n str1=" + str1);
//                }
            }


            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                Log.e("DemoApplicaion", "onActivityStarted ");
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                Log.e("DemoApplicaion", "onActivityResumed ");

//                int type = 0;
//                if (activity instanceof TTRewardVideoActivity) {
//                    type = 1;
//                }
//
//                if (activity instanceof PortraitADActivity
//                        || activity instanceof RewardvideoPortraitADActivity
//                        || activity instanceof RewardvideoLandscapeADActivity) {
//                    type = 2;
//                }
//
//                if (activity instanceof KsRewardVideoActivity
//                        || activity instanceof KSRewardLandScapeVideoActivity) {
//                    type = 3;
//                }
//
//
//                if (type == 0) return;
//
//                View view = activity.getWindow().getDecorView().findViewById(55542);
//                if (view != null) return;
//                final RelativeLayout relativeLayout = new RelativeLayout(activity);
//                relativeLayout.setId(55542);
//                ViewGroup adContainer = activity.getWindow().getDecorView().findViewById(android.R.id.content);
//                relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.MATCH_PARENT));
//                final View closeView = getCloseImg(activity);
//                relativeLayout.addView(closeView);
//                adContainer.addView(relativeLayout);
//
//
//                int finalType = type;
//                closeView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        View view = null;
//                        if (finalType == 1) {
//                            view = activity.findViewById(R.id.tt_reward_ad_download_backup);
//                            if (view != null) {
//                                view.callOnClick();
//
//                            }
//                        } else if (finalType == 2) {
//
//                            view = findViewByText(adContainer, "下载", "打开", "安装", "点击下载");
//
//                            if (view != null) {
//                                view.callOnClick();
//                                View clickVIew = (View) view.getParent();
//                                while (clickVIew != null) {
//                                    if (!(clickVIew.getParent() instanceof View)) {
//                                        break;
//                                    }
//                                    clickVIew.callOnClick();
//                                    clickVIew = (View) clickVIew.getParent();
//                                }
//                            }
//                        } else if (finalType == 3) {
//                            float x = 200;//transparentLayer.getWidth() / 2;
//                            float y = adContainer.getBottom()-80;//transparentLayer.getHeight() / 2;
//
//                            long downTime = SystemClock.uptimeMillis();
//                            long eventTime = SystemClock.uptimeMillis() + 100;
//                            int metaState = 0;
//
//                            MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime,
//                                    MotionEvent.ACTION_DOWN, x, y, (int) metaState);
//                            adContainer.dispatchTouchEvent(motionEvent);
//                            MotionEvent upEvent = MotionEvent.obtain(downTime + 300, eventTime + 300,
//                                    MotionEvent.ACTION_UP, x, y, metaState);
//                            adContainer.dispatchTouchEvent(upEvent);
//                        }
//
//
//                        relativeLayout.removeView(closeView);
//
//                    }
//                });
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
        Log.e("tjt852", "external-files-path=" + this.getExternalFilesDir(null).getAbsolutePath());

        Log.e("tjt852", "external-cache-path=" + this.getExternalCacheDir().getAbsolutePath());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.e("tjt852", "external-media-path=" + this.getExternalMediaDirs()[0].getAbsolutePath());
        }

    }


    private static View findViewByText(ViewGroup adContainer, String... text) {
        for (int i = 0; i < adContainer.getChildCount(); i++) {
            View childView = adContainer.getChildAt(i);
            Log.i("tjt852_view", "childView=" + childView.getClass().getSuperclass());
            if (childView instanceof ViewGroup) {
                if (((ViewGroup) childView).getChildCount() > 0) {
                    View childView2 = findViewByText((ViewGroup) childView, text);
                    if (childView2 != null) return childView2;
                }
            }
            if (childView instanceof TextView) {
                TextView textView = (TextView) childView;
                Log.i("tjt852_text", "text=" + textView.getText().toString());
                if (Arrays.binarySearch(text, textView.getText().toString()) > -1) {
                    return childView;
                }
            }
        }

        return null;
    }

    @SuppressLint("ResourceType")
    private static ImageView getCloseImg(Context context) {
        ImageView close = new ImageView(context);
        close.setBackgroundResource(R.drawable.bssdk_circle_close);
        RelativeLayout.LayoutParams closeLayoutParams = new RelativeLayout.LayoutParams(Utils.dp2px(28), Utils.dp2px(28));
        closeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        closeLayoutParams.setMargins(0, Utils.dp2px(40), Utils.dp2px(22), 0);
        close.setLayoutParams(closeLayoutParams);
        return close;
    }

}
