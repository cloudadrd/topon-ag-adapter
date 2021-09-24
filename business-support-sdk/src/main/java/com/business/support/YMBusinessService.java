package com.business.support;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.business.support.adinfo.BSAdType;
import com.business.support.adinfo.TKCreator;
import com.business.support.ascribe.InstallListener;
import com.business.support.ascribe.InstallStateMonitor;
import com.business.support.ascribe.NativeDataManager;
import com.business.support.ascribe.RewardTaskInfo;
import com.business.support.attract.DataParse;
import com.business.support.attract.PolicyData;
import com.business.support.calendar.CalendarOperate;
import com.business.support.calendar.CalendarPara;
import com.business.support.captcha.CaptchaActivity;
import com.business.support.captcha.CaptchaListener;
import com.business.support.compose.SIDListener;
import com.business.support.compose.SdkTaskManager;
import com.business.support.compose.TaskResult;
import com.business.support.compose.ZipSidListener;
import com.business.support.config.Const;
import com.business.support.http.HttpRequester;
import com.business.support.reallycheck.DebugCheck;
import com.business.support.reallycheck.EmulatorCheck;
import com.business.support.reallycheck.HookCheck;
import com.business.support.reallycheck.ResultData;
import com.business.support.reallycheck.RootCheck;
import com.business.support.reallycheck.VirtualAppCheck;
import com.business.support.reallycheck.WireSharkCheck;
import com.business.support.shuzilm.ShuzilmImpl;
import com.business.support.utils.BSInterstitialListener;
import com.business.support.utils.ContextHolder;
import com.business.support.utils.SLog;
import com.business.support.utils.Utils;
import com.business.support.webview.AdInterstitialMediation;
import com.business.support.webview.AdVideoInterface;
import com.business.support.webview.AdVideoMediation;
import com.business.support.webview.BSRewardVideoListener;
import com.business.support.webview.CacheWebView;
import com.business.support.webview.InnerWebViewActivity;
import com.business.support.webview.InnerWebViewActivity2;
import com.business.support.webview.WebViewToNativeListener;
import com.business.support.widget.ContinueFrameLayout;
import com.business.support.widget.FingerFrameLayout;
import com.business.support.widget.HairFrameParentLayout;
import com.bytedance.sdk.openadsdk.activity.base.TTRewardVideoActivity;
import com.kwad.sdk.api.proxy.app.KSRewardLandScapeVideoActivity;
import com.kwad.sdk.api.proxy.app.KsRewardVideoActivity;
import com.mbridge.msdk.reward.player.MBRewardVideoActivity;
import com.qq.e.ads.ADActivity;
import com.qq.e.ads.PortraitADActivity;
import com.qq.e.ads.RewardvideoLandscapeADActivity;
import com.qq.e.ads.RewardvideoPortraitADActivity;
import com.qq.e.comm.managers.GDTADManager;
import com.qq.e.comm.pi.POFactory;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.thinkingdata.android.ThinkingAnalyticsSDK;


public class YMBusinessService {
    private static final String TAG = "YMBusinessService";
    private static long mAppInstallTime = 0;
    private static long mDays = 0;
    private static int mNumberOfTimes = 0;


    public static double mEcpm = -1;

    private static int customBannerStyle = 0;

    private static int customNativeStyle = 0;

    private static int random = -1;

    private static JSONObject jsonPangleObj = null;

    private static JSONObject jsonGdtObj = null;

    private static JSONObject jsonKsObj = null;

    private static JSONObject jsonMvObj = null;

    public static ThinkingAnalyticsSDK mInstance = null;
    private static final int PERMISSION_REQUEST = 1;
    private static boolean rvClickStop = false;
    private static String tarPath = "";

    public static void init(final Context context, ThinkingAnalyticsSDK instance, String shuMengApiKey, final SIDListener listener) {
        ContextHolder.init(context);
        mInstance = instance;
        final Context localContext = ContextHolder.getGlobalAppContext();
        SdkTaskManager.getInstance()
                .add(new ShuzilmImpl(), 100, 20000, shuMengApiKey)
//                .add(new SmeiImpl(), 2000, 3000, "JVjHfrQd0LwfAFnND60C", "OfJKRbsUQIunw1xzb2SU", "MIIDLzCCAhegAwIBAgIBMDANBgkqhkiG9w0BAQUFADAyMQswCQYDVQQGEwJDTjELMAkGA1UECwwCU00xFjAUBgNVBAMMDWUuaXNodW1laS5jb20wHhcNMjEwNTA2MDMzMDEwWhcNNDEwNTAxMDMzMDEwWjAyMQswCQYDVQQGEwJDTjELMAkGA1UECwwCU00xFjAUBgNVBAMMDWUuaXNodW1laS5jb20wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCETlLQHou1ywPznJ9VeLwals2/FwyDzqrlr34h9kIc/O3C1pkXsICHE7z+DoLvI59FLUxFLDwaf2ywSylfv5m4arUxku/YBQoq85c4iucJonhv7mlg/KIdl94Kd4ajlsB0ZYFRUiIu/A1yePJmAvaGX9Z3AMw3ZoAV71RY5tVIH8KuzH/J6lnagIknN8OB5OglUEzDRhGtQEZD54SCz/it4AJ6M/vKSUdjALMpw4zKyBe3qR9gftOYI6J2S6wHT8Nc6u59X2G8nvTL0f+s9TyXdvy0jvrP3961eAebUGxwthr3ny+WrJASHymMG70rvK2wvS2TfxdtctP8KCFIEBmBAgMBAAGjUDBOMB0GA1UdDgQWBBQ3fMAEBSTHQflJgXBVqrC4JZXWSjAfBgNVHSMEGDAWgBQ3fMAEBSTHQflJgXBVqrC4JZXWSjAMBgNVHRMEBTADAQH/MA0GCSqGSIb3DQEBBQUAA4IBAQAJPorB5hV1JTo4WzTD0/5iLenV+VWF4j2HXp9OzEryDlJ19ax94QCxvCL2XSEqkNKviKvZksTz221q32V1xdTJPC3AqNd15Gn2msyu3VK8/efLxItmjvxH69//Obh3GZu5XHcLPwlt3/UHd3vBvCNXmZgyo0EHTeSXpr3P4utZVx6IBFM1gifcYTK8p3fVWbNf4RngMKmKleOzLhJwrussv+VZSudebMxclvNAgO1rRLXPKrwSoih2F4SUlHjahSopeMfyDTStdZ5oezOzb+y2ibmtCgf5SF9Dxqbyi8Kyx/ZS63ey63b2CchiK2iJCyDSWOVHysKsOhpI1TrbExKd")
                .zip(localContext, new ZipSidListener() {
                    @Override
                    public void result(Collection<TaskResult> taskResults) {
                        int score = 0;
                        String data = null;
                        for (TaskResult taskResult : taskResults) {
                            String taskData;

                            if (taskResult.isError) {
                                taskData = "{\"did\":\"errorCode=" + taskResult.getErrorType() + "\"}";
                            } else {
                                taskData = taskResult.getData();
                            }
                            score += taskResult.getScore();
                            if (data != null) {
                                data = Utils.combineJson(data, taskData);
                            } else {
                                data = taskData;
                            }
                        }
                        if (TextUtils.isEmpty(data)) {
                            data = "{}";
                        }
                        composeNativeValid(localContext, score, data, listener);
                    }
                });
    }

    public static void enableAdTrace(InstallListener listener) {
        final Context localContext = ContextHolder.getGlobalAppContext();
        optimizeAdInfo();
        InstallStateMonitor.register(localContext, new MyInstallListener(listener));
    }

    public static void enableAdTrace(ThinkingAnalyticsSDK instance, InstallListener listener) {
        if (instance != null)
            mInstance = instance;
        final Context localContext = ContextHolder.getGlobalAppContext();
        optimizeAdInfo();
        InstallStateMonitor.register(localContext, new MyInstallListener(listener));
    }

    public static final class MyInstallListener implements InstallListener {

        InstallListener mListener;

        MyInstallListener(InstallListener listener) {
            mListener = listener;
        }

        @Override
        public void installedHit(String pkg, String appName, BSAdType bsAdType, String sceneId) {
            SLog.i(TAG, "installedHit pkg=" + pkg);
            try {

                List<RewardTaskInfo> list = RewardTaskInfo.getRewardTasksForPkg(pkg);
                //start 判断是否是安装试玩的广告，是则持久化任务状态
                for (RewardTaskInfo taskInfo : list) {
                    taskInfo.infoState = 0;
                    taskInfo.bsAdType = bsAdType;
                    taskInfo.startTaskAppTime = 0;
                    NativeDataManager.writeFileForTaskInfo2(taskInfo);
                    RewardTaskInfo.revealAdPackages.get(taskInfo.sceneId).remove(pkg);
                    if (RewardTaskInfo.revealAdPackages.get(taskInfo.sceneId).size() <= 0) {
                        RewardTaskInfo.revealAdPackages.remove(taskInfo.sceneId);
                    }
                    if (mListener != null) {
                        mListener.installedHit(taskInfo.currentInstallPkg, taskInfo.appName, taskInfo.bsAdType, taskInfo.sceneId);
                    }
                }


                //end
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("ad_channel", bsAdType.getName());
                jsonObject.put("pkg_name", pkg);
                jsonObject.put("app_name", appName);
                if (mInstance != null) {
                    mInstance.track("ad_install", jsonObject);
                    mInstance.flush();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }


        }
    }


    public static void setH5RewardPlacementId(String placementId) {
        AdVideoMediation.setPosId(placementId);
    }

    public static void setH5InterstitialPlacementId(String placementId) {
        AdInterstitialMediation.setPosId(placementId);
    }


    public static void setH5RewardListener(BSRewardVideoListener listener) {
        AdVideoMediation.getInstance().setRewardVideoListener(listener);
    }

    public static void setH5InterstitialListener(BSInterstitialListener listener) {
        AdInterstitialMediation.getInstance().setInterstitialListener(listener);
    }

    /**
     * 带缓存的webview，可以提前创建cacheWebView并且加载
     */
    public static void startCacheWebViewPage(Context context, CacheWebView cacheWebView, WebViewToNativeListener listener) {
        startCacheWebViewPage(context, cacheWebView, listener, false);
    }

    /**
     * //不带缓存的webview
     */
    public static void startWebViewPage(Context context, String linkUrl, WebViewToNativeListener listener) {
        startWebViewPage(context, linkUrl, listener, false);
    }

    public static void startCaptcha(CaptchaListener listener) {
        CaptchaActivity.launch(listener);
    }

    /**
     * 带缓存的webview，可以提前创建cacheWebView并且加载
     */
    public static void startCacheWebViewPage(Context context, CacheWebView cacheWebView, WebViewToNativeListener listener, boolean isLoadBarHide) {
        if (cacheWebView.getParent() != null && cacheWebView.getParent() instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) cacheWebView.getParent();
            viewGroup.removeView(cacheWebView);
        }
        AdVideoInterface.nativeListener = listener;
        //带缓存的webview，可以提前创建cacheWebView并且加载
        InnerWebViewActivity.launch(context, cacheWebView, isLoadBarHide);
    }

    /**
     * //不带缓存的webview
     */
    public static void startWebViewPage(Context context, String linkUrl, WebViewToNativeListener listener, boolean isLoadBarHide) {
        AdVideoInterface.nativeListener = listener;
        InnerWebViewActivity2.launch(context, linkUrl, isLoadBarHide);
    }


    private static void composeNativeValid(Context context, int score, String data, SIDListener listener) {

        ResultData emulatorResult = EmulatorCheck.validCheck(context);

        ResultData rootResult = RootCheck.validCheck(context);

        ResultData hookResult = HookCheck.validCheck(context);

        ResultData wireSharkResult = WireSharkCheck.validCheck(context);

        ResultData debugResult = DebugCheck.validCheck(context);

        ResultData moreOpenResult = VirtualAppCheck.validCheck(context);

        if (emulatorResult.isError()) {
            score += emulatorResult.getScore();
        }

        if (rootResult.isError()) {
            score += rootResult.getScore();
        }

        if (hookResult.isError()) {
            score += hookResult.getScore();
        }

        if (wireSharkResult.isError()) {
            score += wireSharkResult.getScore();
        }

        if (debugResult.isError()) {
            score += debugResult.getScore();
        }

        if (moreOpenResult.isError()) {
            score += moreOpenResult.getScore();
        }


        try {
            JSONObject jsonObject = new JSONObject(data);
            jsonObject.put("Emulator", emulatorResult.isError());
            jsonObject.put("EmulatorMsg", emulatorResult.getErrorMessage());
            jsonObject.put("Hook", hookResult.isError());
            jsonObject.put("WireShark", wireSharkResult.isError());
            jsonObject.put("Root", rootResult.isError());
            jsonObject.put("Debug", debugResult.isError());
            jsonObject.put("DebugMsg", debugResult.getErrorMessage());
            jsonObject.put("VirtualApp", moreOpenResult.isError());
            jsonObject.put("VirtualAppMsg", moreOpenResult.getErrorMessage());

            if (listener != null) {
                listener.onSuccess(score, jsonObject.toString());
            }

            JSONObject properties = new JSONObject();
            properties.put("score", score);

            //自研
            properties.put("EmulatorCheck", jsonObject.get("Emulator"));
            properties.put("RootCheck", jsonObject.get("Root"));
            properties.put("HookCheck", jsonObject.get("Hook"));
            properties.put("WireSharkCheck", jsonObject.get("WireShark"));
            properties.put("debug", jsonObject.get("Debug"));

            //数盟
            if (jsonObject.has("device_type"))
                properties.put("shumeng", jsonObject.get("device_type"));
            if (jsonObject.has("did"))
                properties.put("shumengid", jsonObject.get("did"));
            if (jsonObject.has("cheat_type"))
                properties.put("cheat_type", jsonObject.get("cheat_type"));

            //数美
            if (jsonObject.has("riskLevel"))
                properties.put("riskLevel", jsonObject.get("riskLevel"));

            if (jsonObject.has("description"))
                properties.put("description", jsonObject.get("description"));

            if (jsonObject.has("model"))
                properties.put("model", jsonObject.get("model"));

            if (jsonObject.has("riskType"))
                properties.put("riskType", jsonObject.get("riskType"));

            if (jsonObject.has("shuMeiDid"))
                properties.put("shuMeiDid", jsonObject.get("shuMeiDid"));

            mInstance.track("Phonecheck", properties);
            mInstance.flush();

        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onFailure(e.getMessage());
            }
        }

    }

    public static String getAndroidID(Context context) {
        return Utils.getAndroidId(context);
    }


    public static boolean isOperator(Context context) {
        return Utils.isOperator(context);
    }

    public static String getAppVersion(Context context) {
        return Utils.getAppVersion(context);

    }

    public static String getSystem() {

        return "Android";
    }

    public static String getNetworkType(Context context) {
        return Utils.getNetworkType(context);
    }

    public static void setFirstInstallTime(long timestamp) {
        mAppInstallTime = timestamp;
        long currentTimestamp = System.currentTimeMillis();
        mDays = (currentTimestamp - timestamp) / (24 * 60 * 60 * 1000);
    }

    public static void setRewardedVideoTimes(int playedTimes) {
        mNumberOfTimes = playedTimes;
    }

    public static void requestRewaredConfig(final Context context, String appid, final StrategyInfoListener listener) {
        int sim = isOperator(context) ? 1 : 0;
        String urlStr = Const.STRATEGY_CHECK_URL + "?" +
                "androidid=" + getAndroidID(context) +
                "&sim=" + sim +
                "&system=" + getSystem() +
                "&network=" + getNetworkType(context) +
                "&appversion=" + getAppVersion(context) +
                "&installtime=" + mAppInstallTime +
                "&days=" + mDays +
                "&playedtimes=" + mNumberOfTimes +
                "&appid=" + appid;
//        SLog.i(TAG,"requestRewaredConfig");
        HttpRequester.requestByGet(context, urlStr, new HttpRequester.Listener() {
            @Override
            public void onSuccess(byte[] data, String url) {
                try {
                    String result = new String(data);
                    SLog.i(TAG, "onSuccess url" + url + ",result=" + result);
                    JSONObject respObj = new JSONObject(result);
                    int retCode = respObj.optInt("code");
                    if (10000 != retCode) {
                        listener.isActive(false, false);
                        return;
                    }

                    JSONObject acObj = respObj.optJSONObject("data");
                    if (null == acObj) {
                        listener.isActive(false, false);
                        return;
                    }

                    boolean ac = acObj.optBoolean("status");


                    boolean install = acObj.optBoolean("install");

                    listener.isActive(ac, install);


                    DataParse.jsonParse(acObj);
                } catch (Exception e) {
                    listener.isActive(false, false);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String msg, String url) {
                SLog.i(TAG, "onFailure url=" + url);
                listener.isActive(false, false);

            }
        });

    }

    /**
     * DeepLink方式打开，根据parseClickUrl
     */
    public static boolean openDeepLink(String deeplink, @Nullable String packageName) {
        try {
            Context context = ContextHolder.getGlobalAppContext();

            Uri uri = Uri.parse(deeplink);
            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (packageName != null) {
                it.setPackage(packageName);
            }

            ComponentName componentName = it.resolveActivity(context.getPackageManager());

            if (componentName != null) {    //已经安装该应用
                context.startActivity(it);
                return true;
            }
        } catch (Exception e) {
            SLog.d(TAG, "openDeepLink failed::" + e.getMessage());
        }

        return false;
    }


    private static Application.ActivityLifecycleCallbacks activityLifecycleCallbacks = null;

    public static void optimizeAdInfo() {
        if (activityLifecycleCallbacks != null) {
            return;
        }

        activityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {

            @Override
            public void onActivityPreCreated(Activity activity, Bundle savedInstanceState) {
                SLog.d(TAG, "onActivityPreCreated activity=" + activity.getComponentName());
//                pangelDataHandler(activity, savedInstanceState);
////                gdtDataHandler(activity, savedInstanceState);
            }

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                SLog.d(TAG, "onActivityCreated activity=" + activity.getComponentName());
                pangelDataHandler(activity, savedInstanceState);
                gdtDataHandler(activity, savedInstanceState);
                ksDataHandler(activity, savedInstanceState);
                mvDataHandler(activity, savedInstanceState);
            }


            @Override
            public void onActivityStarted(Activity activity) {
                SLog.d(TAG, "onActivityStarted activity=" + activity.getComponentName());
            }


            @Override
            public void onActivityResumed(final Activity activity) {

                SLog.d(TAG, "onActivityResumed activity=" + activity.getComponentName());

                Const.HANDLER.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            handlerActivityResume(activity);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                }, 4500);

            }

            @Override
            public void onActivityPaused(Activity activity) {
                SLog.d(TAG, "onActivityPaused activity=" + activity.getComponentName());

            }

            @Override
            public void onActivityStopped(Activity activity) {
                SLog.d(TAG, "onActivityStopped activity=" + activity.getComponentName());

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                SLog.d(TAG, "onActivitySaveInstanceState activity=" + activity.getComponentName());

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                SLog.d(TAG, "onActivityDestroyed activity=" + activity.getComponentName());

            }
        };
        ((Application) ContextHolder.getGlobalAppContext().getApplicationContext()).registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

    private static final int CLOSE_VIEW_ID = Utils.generateViewId();

    public static void handlerActivityResume(final Activity activity) {
        SLog.i(TAG, "handlerActivityResume mEcpm =" + mEcpm);
        boolean isStop = isCustomRvStyle();

        if (isStop) {
            SLog.i(TAG, "handlerActivityResume stop it. mEcpm=" + mEcpm + ",random=" + random);
            return;
        }

        int type = 0;

        if (activity instanceof TTRewardVideoActivity) {
            type = 1;
        }

        if (activity instanceof PortraitADActivity
                || activity instanceof RewardvideoPortraitADActivity
                || activity instanceof RewardvideoLandscapeADActivity) {
            type = 2;
        }

        if (activity instanceof KsRewardVideoActivity
                || activity instanceof KSRewardLandScapeVideoActivity) {
            type = 3;
        }

        if (activity instanceof MBRewardVideoActivity) {
            type = 4;
        }

        if (type == 0) return;

        View view = activity.getWindow().getDecorView().findViewById(CLOSE_VIEW_ID);
        if (view != null) return;
        final RelativeLayout relativeLayout = new RelativeLayout(activity);
        relativeLayout.setId(CLOSE_VIEW_ID);
        final ViewGroup adContainer = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        final View closeView = getCloseImg(activity);
        relativeLayout.addView(closeView);
        Const.HANDLER.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!activity.isDestroyed() && closeView.getParent() != null) {
                    relativeLayout.removeView(closeView);
                }
            }
        }, 20000);

        adContainer.addView(relativeLayout);

        closeView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final float x = 200;//transparentLayer.getWidth() / 2;
                float y = adContainer.getBottom() - Utils.dp2px(48);//transparentLayer.getHeight() / 2;

                clickPenetrate(adContainer, x, y);


                Const.HANDLER.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int y = adContainer.getBottom() - Utils.dp2px(120);//transparentLayer.getHeight() / 2;

                        clickPenetrate(adContainer, x, y);
                    }
                }, 500);


                Const.HANDLER.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int y = adContainer.getBottom() - Utils.dp2px(160);//transparentLayer.getHeight() / 2;
                        clickPenetrate(adContainer, x, y);
                    }
                }, 500);

                Const.HANDLER.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int x = adContainer.getRight() - 160;
                        int y = adContainer.getBottom() - Utils.dp2px(34);//transparentLayer.getHeight() / 2;
                        clickPenetrate(adContainer, x, y);
                    }
                }, 500);


                relativeLayout.removeView(closeView);
            }
        });
    }

    private static void clickPenetrate(View view, float x, float y) {
        if (rvClickStop) return;
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis() + 100;
        int metaState = 0;
        MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime,
                MotionEvent.ACTION_DOWN, x, y, (int) metaState);
        view.dispatchTouchEvent(motionEvent);
        MotionEvent upEvent = MotionEvent.obtain(downTime + 300, eventTime + 300,
                MotionEvent.ACTION_UP, x, y, metaState);
        view.dispatchTouchEvent(upEvent);

        motionEvent.recycle();
        upEvent.recycle();
    }

    public static void setAdClick(int firmId) {
        //先上报点击
        if (rvClickStop == false) {
            BSAdType adType = null;
            if (firmId == 8) {
                adType = BSAdType.GDT;
            } else if (firmId == 15) {
                adType = BSAdType.PANGLE;
            } else if (firmId == 28) {
                adType = BSAdType.KS;
            } else if (firmId == 6) {
                adType = BSAdType.MV;
            } else {
                mEcpm = -1;//其它广告，用ecpm排除
                return;
            }

            JSONObject jsonObject = null;

            if (BSAdType.PANGLE == adType) {
                jsonObject = jsonPangleObj;
            } else if (BSAdType.GDT == adType) {
                jsonObject = jsonGdtObj;
            } else if (BSAdType.KS == adType) {
                jsonObject = jsonKsObj;
            } else if (BSAdType.MV == adType) {
                jsonObject = jsonMvObj;
            }

            if (jsonObject == null) {
                SLog.e(TAG, "setAdInfo jsonObject is null, end report.");
                return;
            }

            //播放过的广告加入到列表里
            String pkgName = jsonObject.optString("pkg_name");
            String appName = jsonObject.optString("app_name");
            if (!TextUtils.isEmpty(pkgName))
                RewardTaskInfo.adPackages.put(pkgName, new RewardTaskInfo(pkgName, adType, appName));

            try {
                jsonObject.put("ad_channel", adType.getName());
                jsonObject.put("app_name", appName);
                mInstance.track("ad_collection_click", jsonObject);
                mInstance.flush();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        rvClickStop = true;
        Const.HANDLER.postDelayed(new Runnable() {
            @Override
            public void run() {
                rvClickStop = false;
            }
        }, 3000);
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

    private static void pangelDataHandler(Activity activity, Bundle savedInstanceState) {
        if (!(activity instanceof TTRewardVideoActivity)) {
            return;
        }
        String materialMeta = "";
        JSONObject tempObj = null;
        try {
            Object c1 = com.bytedance.sdk.openadsdk.core.t.a().c();
            if (c1 != null) {
                tempObj = com.bytedance.sdk.openadsdk.core.t.a().c().aO();
            }
        } catch (Throwable e) {
            e.printStackTrace();
            return;
        }
        if (savedInstanceState != null) {
            materialMeta = savedInstanceState.getString("material_meta");
            if (!TextUtils.isEmpty(materialMeta)) {
                try {
                    tempObj = com.bytedance.sdk.openadsdk.core.b.a(new JSONObject(materialMeta)).aO();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

        if (tempObj != null) {
//            SLog.i(TAG, "pangelDataHandler resultStr=" + tempObj.toString());
            String iconUrl = null;
            String appName = null;
            String packageName = null;
            String downloadUrl = null;
            String adId = null;
            String videoUrl = null;

            String targetUrl = tempObj.optString("target_url");

            JSONObject iconObj = tempObj.optJSONObject("icon");
            if (iconObj != null) {
                iconUrl = iconObj.optString("url");
            }

            JSONObject appObj = tempObj.optJSONObject("app");
            if (appObj != null) {
                appName = appObj.optString("app_name");
                packageName = appObj.optString("package_name");
                downloadUrl = appObj.optString("download_url");
            }

            String extStr = tempObj.optString("ext");
            if (!TextUtils.isEmpty(extStr)) {
                try {
                    adId = new JSONObject(extStr).optString("ad_id");
                } catch (JSONException ignored) {
                }
            }

            JSONObject videoObj = tempObj.optJSONObject("video");
            if (videoObj != null) {
                videoUrl = videoObj.optString("video_url");
            }

            try {
                jsonPangleObj = new JSONObject();

                if (targetUrl != null) {
                    jsonPangleObj.put("target_url", targetUrl);
                }

                if (adId != null) {
                    jsonPangleObj.put("ad_id", adId);
                }

                if (appName != null) {
                    jsonPangleObj.put("app_name", appName);
                }

                if (iconUrl != null) {
                    jsonPangleObj.put("icon_url", iconUrl);
                }

                if (videoUrl != null) {
                    jsonPangleObj.put("video_url", videoUrl);
                }

                if (packageName != null) {
                    jsonPangleObj.put("pkg_name", packageName);
                }

                if (downloadUrl != null) {
                    jsonPangleObj.put("download_url", downloadUrl);
                }
                SLog.i(TAG, "pangelDataHandler jsonStr=" + jsonPangleObj.toString());
            } catch (JSONException e) {
                SLog.e(TAG, "pangelDataHandler 2 error=" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static void gdtDataHandler(Activity activity, Bundle savedInstanceState) {
        if (activity instanceof ADActivity) {
            POFactory pOFactory = null;
            try {
                pOFactory = GDTADManager.getInstance().getPM().getPOFactory();
            } catch (com.qq.e.comm.managers.plugin.c c) {
//                SLog.e(TAG, "gdtDataHandler getPOFactory error=" + c.getMessage());
                c.printStackTrace();
                return;
            }
            Intent intent = activity.getIntent();
            if (intent == null) {
                SLog.e(TAG, "gdtDataHandler intent is null");
                return;
            }
            intent.setExtrasClassLoader(pOFactory.getClass().getClassLoader());
            Bundle extras = intent.getExtras();
            if (extras == null) {
                SLog.e(TAG, "gdtDataHandler extras is null");
                return;
            }
            String appName = null;
            String packageName = null;
            String downloadUrl = null;

            String adId = null;

            String iconUrl = null;
            String videoUrl = null;

            try {
                Object parcelable = extras.getParcelable("admodel");
                ClassLoader classLoader = pOFactory.getClass().getClassLoader();
                Class<?> classData = classLoader.loadClass("com.qq.e.comm.plugin.model.BaseAdInfo");
                Field fieldJson = classData.getDeclaredField("aM");
                fieldJson.setAccessible(true);
                String jsonStr = fieldJson.get(parcelable).toString();
                JSONObject jsonObject = new JSONObject(jsonStr);
                adId = jsonObject.optString("ad_industry_id");

                iconUrl = jsonObject.optString("corporate_logo");
                videoUrl = jsonObject.optString("video");
                JSONObject ext = jsonObject.optJSONObject("ext");

                SLog.i(TAG, "gdtDataHandler resultStr=" + jsonStr);
                if (ext != null) {
                    appName = ext.optString("appname");
                    packageName = ext.optString("packagename");
                    downloadUrl = ext.optString("pkgurl");
                }

                jsonGdtObj = new JSONObject();

                jsonGdtObj.put("ad_id", adId);

                if (appName != null) {
                    jsonGdtObj.put("app_name", appName);
                }

                jsonGdtObj.put("icon_url", iconUrl);

                jsonGdtObj.put("video_url", videoUrl);

                if (packageName != null) {
                    jsonGdtObj.put("pkg_name", packageName);
                }

                if (downloadUrl != null) {
                    jsonGdtObj.put("download_url", downloadUrl);
                }
                SLog.i(TAG, "gdtDataHandler jsonStr=" + jsonGdtObj.toString());
            } catch (Throwable e) {
                SLog.e(TAG, "gdtDataHandler 2 error=" + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    private static void ksDataHandler(Activity activity, Bundle savedInstanceState) {
        if (activity instanceof KsRewardVideoActivity || activity instanceof KSRewardLandScapeVideoActivity) {
            Intent intent = activity.getIntent();
            if (intent == null) {
                return;
            }

            String key_template_json = intent.getStringExtra("key_template_json");
//            KsVideoPlayConfig key_video_play_config = (KsVideoPlayConfig) intent.getSerializableExtra("key_video_play_config");
            int key_template_reward_type = intent.getIntExtra("key_template_reward_type", -1);
            SLog.d(TAG, "ksDataHandler key_template_json=" + key_template_json);
//            SLog.d(TAG, "ksDataHandler key_video_play_config=" + key_video_play_config);
            SLog.d(TAG, "ksDataHandler key_template_reward_type=" + key_template_reward_type);
            String appName = null;
            String packageName = null;
            String downloadUrl = null;

            String adId = null;

            String iconUrl = null;
            String videoUrl = null;

            try {
                JSONObject jsonObject = new JSONObject(key_template_json);
                adId = jsonObject.optString("posId");

                JSONObject adInfo = jsonObject.optJSONArray("adInfo").getJSONObject(0);
                SLog.d(TAG, "ksDataHandler adInfo=" + adInfo.toString());
                JSONObject adBaseInfo = adInfo.optJSONObject("adBaseInfo");
                if (adBaseInfo != null) {
                    appName = adBaseInfo.optString("appName");
                    if (TextUtils.isEmpty(appName)) {
                        appName = adBaseInfo.optString("productName");
                    }
                }

                if (adBaseInfo != null) {
                    iconUrl = adBaseInfo.optString("appIconUrl");
                }
                if (adBaseInfo != null) {
                    packageName = adBaseInfo.optString("appPackageName");
                }
                videoUrl = Objects.requireNonNull(Objects.requireNonNull(adInfo.optJSONObject("adMaterialInfo")).optJSONArray("materialFeature")).optJSONObject(0).optString("materialUrl");
                JSONObject ext = jsonObject.optJSONObject("ext");

                downloadUrl = adInfo.optJSONObject("adConversionInfo").optString("appDownloadUrl");

                jsonKsObj = new JSONObject();

                jsonKsObj.put("ad_id", adId);

                jsonKsObj.put("app_name", appName);

                jsonKsObj.put("icon_url", iconUrl);

                jsonKsObj.put("video_url", videoUrl);

                jsonKsObj.put("pkg_name", packageName);

                jsonKsObj.put("download_url", downloadUrl);
                SLog.i(TAG, "ksDataHandler jsonStr=" + jsonKsObj.toString());
            } catch (Throwable e) {
                SLog.e(TAG, "ksDataHandler 2 error=" + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    private static void mvDataHandler(Activity activity, Bundle savedInstanceState) {
        if (!(activity instanceof MBRewardVideoActivity)) {
            return;
        }

        try {
            String unitId = activity.getIntent().getStringExtra("unitId");
            List<com.mbridge.msdk.videocommon.download.a> list = com.mbridge.msdk.videocommon.download.c.getInstance().b(unitId);
            if (list.size() <= 0) {
                return;
            }
            com.mbridge.msdk.videocommon.download.a a = list.get(0);
            com.mbridge.msdk.foundation.entity.CampaignEx campaignEx = null;
            campaignEx = a.g();
            if (campaignEx == null) {
                return;
            }
            String adId = campaignEx.getId();
            String appName = campaignEx.getAppName();
            String iconUrl = campaignEx.getIconUrl();
            String videoUrl = campaignEx.getVideoUrlEncode();
            String packageName = campaignEx.getPackageName();
            jsonMvObj = new JSONObject();

            jsonMvObj.put("ad_id", adId);

            jsonMvObj.put("app_name", appName);

            jsonMvObj.put("icon_url", iconUrl);

            jsonMvObj.put("video_url", videoUrl);

            jsonMvObj.put("pkg_name", packageName);

            jsonMvObj.put("download_url", "");
            SLog.i(TAG, "mvDataHandler jsonStr=" + jsonMvObj.toString());
        } catch (Throwable e) {
            SLog.e(TAG, "mvDataHandler 2 error=" + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void setAdInfo(double ecpm, int firmId) {
        mEcpm = ecpm;
        BSAdType adType = null;
        if (firmId == 8) {
            adType = BSAdType.GDT;
        } else if (firmId == 15) {
            adType = BSAdType.PANGLE;
        } else if (firmId == 28) {
            adType = BSAdType.KS;
        } else if (firmId == 6) {
            adType = BSAdType.MV;
        } else {
            mEcpm = -1;//其它广告，用ecpm排除
            return;
        }

        random = (int) (Math.random() * 100 + 1);
        if (mInstance == null)
            return;

        JSONObject jsonObject = null;

        if (BSAdType.PANGLE == adType) {
            jsonObject = jsonPangleObj;
        } else if (BSAdType.GDT == adType) {
            jsonObject = jsonGdtObj;
        } else if (BSAdType.KS == adType) {
            jsonObject = jsonKsObj;
        } else if (BSAdType.MV == adType) {
            jsonObject = jsonMvObj;
        }

        if (jsonObject == null) {
            SLog.e(TAG, "setAdInfo jsonObject is null, end report.");
            return;
        }

        //播放过的广告加入到列表里
        String pkgName = jsonObject.optString("pkg_name");
        String appName = jsonObject.optString("app_name");
        if (!TextUtils.isEmpty(pkgName))
            RewardTaskInfo.adPackages.put(pkgName, new RewardTaskInfo(pkgName, adType, appName));

        String strData = null;
        try {
            jsonObject.put("ad_channel", adType.getName());
            jsonObject.put("ecpm", ecpm);
            mInstance.track("ad_collection", jsonObject);
            mInstance.flush();
            strData = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SLog.e(TAG, "setAdInfo post json=" + strData);
    }


    public static void traceInstall(int firmId, String sceneId) {
        JSONObject jsonObject = null;
        BSAdType adType = null;
        if (firmId == 8) {
            adType = BSAdType.GDT;
        } else if (firmId == 15) {
            adType = BSAdType.PANGLE;
        } else if (firmId == 28) {
            adType = BSAdType.KS;
        } else if (firmId == 6) {
            adType = BSAdType.MV;
        } else {
            return;
        }

        if (BSAdType.PANGLE == adType) {
            jsonObject = jsonPangleObj;
        } else if (BSAdType.GDT == adType) {
            jsonObject = jsonGdtObj;
        } else if (BSAdType.KS == adType) {
            jsonObject = jsonKsObj;
        } else if (BSAdType.MV == adType) {
            jsonObject = jsonMvObj;
        }

        if (jsonObject == null) return;

        String pkgName = jsonObject.optString("pkg_name");
        String appName = jsonObject.optString("app_name");
        if (!TextUtils.isEmpty(pkgName) && !TextUtils.isEmpty(appName))
            RewardTaskInfo.putRevelPackage(sceneId, pkgName, appName, adType);
    }


    public static FrameLayout getBannerViewByStyle() {
        int sumChance = 0;
        PolicyData.Banner selectBanner = null;
        if (DataParse.policyData != null) {
            int random = (int) (Math.random() * 100 + 1);
            for (PolicyData.Banner banner : DataParse.policyData.banners) {
                SLog.d(TAG, "getBannerViewByStyle random=" + random + ",sumChance =" + sumChance + ",=chance=" + banner.chance);
                if (random <= banner.chance + sumChance) {
                    selectBanner = banner;
                    break;
                }
                sumChance += banner.chance;
            }
        }
        Context context = ContextHolder.getGlobalAppContext();
        if (selectBanner != null) {
            SLog.d(TAG, "getBannerViewByStyle select is ok. styleType=" + selectBanner.styleType.name() + ",chance=" + selectBanner.chance);
            if (selectBanner.styleType == PolicyData.BannerStyleType.HAIR) {
                customBannerStyle = 1;
                return new HairFrameParentLayout(context);
            }
            if (selectBanner.styleType == PolicyData.BannerStyleType.FINGER) {
                customBannerStyle = 2;
                return new FingerFrameLayout(context);
            }
        }
        customBannerStyle = 0;
        return null;
    }

    public static ContinueFrameLayout getNativeViewByStyle() {
        int random = (int) (Math.random() * 100 + 1);
        Context context = ContextHolder.getGlobalAppContext();
        if (DataParse.policyData != null) {
            SLog.d(TAG, "getNativeViewByStyle select is ok. nativeChance=" + DataParse.policyData.nativeChance + ",random=" + random);
            if (random <= DataParse.policyData.nativeChance) {
                customNativeStyle = 1;
                return new ContinueFrameLayout(context);
            }
        }
        customNativeStyle = 0;
        return null;
    }

    public static boolean isCustomRvStyle() {
        if (mEcpm == -1) {
            return true;
        }

        if (DataParse.policyData == null) {
            SLog.i(TAG, "handlerActivityResume policyData is null");
            return true;
        }

        boolean isStop = true;
        for (PolicyData.RV rv : DataParse.policyData.rvs) {
            if (mEcpm >= rv.startRange && mEcpm <= rv.endRange) {
                SLog.i(TAG, "handlerActivityResume into section chance=" + rv.chance + ",random=" + random);
                if (rv.chance >= random) {
                    isStop = false;
                }
                break;
            }
        }
        return isStop;
    }

    public static String getCustomRvStyle() {
        if (isCustomRvStyle()) {
            return "normal";
        } else {
            return "style1";
        }
    }


    public static String getCustomBannerStyle() {
        if (customBannerStyle == 0) {
            return "normal";
        } else if (customBannerStyle == 1) {
            return "style1";
        } else if (customBannerStyle == 2) {
            return "style2";
        }
        return null;
    }


    public static String getCustomNativeStyle() {
        if (customNativeStyle == 0) {
            return "normal";
        } else if (customNativeStyle == 1) {
            return "style1";
        }

        return null;
    }

    static TaskMonitorRunnable taskMonitorRunnable = null;


    static class TaskMonitorRunnable implements Runnable {
        RewardTaskInfo rewardTaskInfo;

        TaskMonitorRunnable(RewardTaskInfo rewardTaskInfo) {
            this.rewardTaskInfo = rewardTaskInfo;
        }

        @Override
        public void run() {
            installMission(rewardTaskInfo);
        }
    }

    static TaskMonitorListener mTaskMonitorListener;

    static boolean isStartAdApp = false;

    public static boolean startCurrentAdApp(String sceneId) {
        RewardTaskInfo taskInfo = NativeDataManager.getTaskInfoForSceneId(sceneId);
        if (taskInfo == null) return false;
        if (TextUtils.isEmpty(taskInfo.currentInstallPkg)) return false;
        final Context context = ContextHolder.getGlobalAppContext();
        boolean result = Utils.startActivityForPackage(context, taskInfo.currentInstallPkg);
        if (result) {
            isStartAdApp = true;
            if (taskMonitorRunnable != null) {
                Const.HANDLER.removeCallbacks(taskMonitorRunnable);
            }
            taskMonitorRunnable = new TaskMonitorRunnable(taskInfo);
            Const.HANDLER.postDelayed(taskMonitorRunnable, 30000);
            taskInfo.infoState = 1;
            taskInfo.startTaskAppTime = System.currentTimeMillis();
            NativeDataManager.writeFileForTaskInfo2(taskInfo);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startService(new Intent(context, WhiteService.class));
        } else {
            context.startService(new Intent(context, WhiteService.class));
        }
        return result;
    }

    public static void setAndRefreshTaskMonitor(TaskMonitorListener taskMonitorListener) {
        mTaskMonitorListener = taskMonitorListener;
        RewardTaskInfo[] taskInfoArray = NativeDataManager.getTaskInfoAll();
        if (taskInfoArray == null || taskInfoArray.length == 0) return;
        for (RewardTaskInfo taskInfo : taskInfoArray) {
            if (taskInfo == null) continue;
            if (taskInfo.infoState == 1) {
                if (System.currentTimeMillis() - taskInfo.startTaskAppTime > 30000) {
                    SLog.d(TAG, "setAndRefreshTaskMonitor ok");
                    installMission(taskInfo);
                }
            }
        }
    }

    private static void installMission(RewardTaskInfo rewardTaskInfo) {
        SLog.i(TAG, "taskMonitorRunnable pkg=" + rewardTaskInfo.currentInstallPkg);
        final Context context = ContextHolder.getGlobalAppContext();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("pkg_name", rewardTaskInfo.currentInstallPkg);
            jsonObject.put("ad_channel", rewardTaskInfo.bsAdType.getName());
            jsonObject.put("ad_scene", rewardTaskInfo.sceneId);
            jsonObject.put("app_name", rewardTaskInfo.appName);
            if (mInstance != null) {
                mInstance.track("ad_installMission", jsonObject);
                mInstance.flush();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (mTaskMonitorListener != null) {
            mTaskMonitorListener.over(rewardTaskInfo.sceneId);
        }
        NativeDataManager.removeForSceneId(rewardTaskInfo.sceneId);
        context.stopService(new Intent(context, WhiteService.class));
    }


    public static void stopTaskMonitor() {
        if (!isStartAdApp) return;
        isStartAdApp = false;
        final Context context = ContextHolder.getGlobalAppContext();
        context.stopService(new Intent(context, WhiteService.class));
        if (taskMonitorRunnable != null) {
            Const.HANDLER.removeCallbacks(taskMonitorRunnable);
            taskMonitorRunnable = null;
        }
    }

    //日历
    public static void checkAndAddCalendarPermission(Context context) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_CALENDAR)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_CALENDAR)
                == PackageManager.PERMISSION_GRANTED) {
        } else {
            // 如果没有授权，就请求用户授权
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_CALENDAR,
                    Manifest.permission.READ_CALENDAR}, PERMISSION_REQUEST);
        }
    }


    public static void insertCalendar(final Context context, final String appName, final String appid, final CalendarPara para) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_CALENDAR)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_CALENDAR)
                == PackageManager.PERMISSION_GRANTED) {
            CalendarOperate.insertCalendar(context, appName, appid, para);
        } else {
            // 如果没有授权，就请求用户授权
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_CALENDAR,
                    Manifest.permission.READ_CALENDAR}, PERMISSION_REQUEST);
        }
    }

    public static void batchInsertCalendar(final Context context, final String appName, final String appid, final CalendarPara para) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_CALENDAR)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_CALENDAR)
                == PackageManager.PERMISSION_GRANTED) {
            CalendarOperate.batchInsertCalendar(context, appName, appid, para);
        } else {
            // 如果没有授权，就请求用户授权
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_CALENDAR,
                    Manifest.permission.READ_CALENDAR}, PERMISSION_REQUEST);
        }
    }

    public static void updateCalendar(final Context context, final String appName, final String appid, final CalendarPara para) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_CALENDAR)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_CALENDAR)
                == PackageManager.PERMISSION_GRANTED) {
            CalendarOperate.updateCalendar(context, appName, appid, para);
        } else {
            // 如果没有授权，就请求用户授权
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_CALENDAR,
                    Manifest.permission.READ_CALENDAR}, PERMISSION_REQUEST);
        }
    }

    public static void batchUpdateCalendar(final Context context, final String appName, final String appid, final CalendarPara para) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_CALENDAR)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_CALENDAR)
                == PackageManager.PERMISSION_GRANTED) {
            CalendarOperate.batchUpdateCalendar(context, appName, appid, para);
        } else {
            // 如果没有授权，就请求用户授权
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_CALENDAR,
                    Manifest.permission.READ_CALENDAR}, PERMISSION_REQUEST);
        }
    }

    public static void deleteCalendar(final Context context, final long eventId) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_CALENDAR)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_CALENDAR)
                == PackageManager.PERMISSION_GRANTED) {
            CalendarOperate.deleteCalendar(context, eventId);
        } else {
            // 如果没有授权，就请求用户授权
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_CALENDAR,
                    Manifest.permission.READ_CALENDAR}, PERMISSION_REQUEST);
        }
    }

    public static boolean searchCalendar(final Context context, final long eventId) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_CALENDAR)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_CALENDAR)
                == PackageManager.PERMISSION_GRANTED) {
            return CalendarOperate.searchCalendar(context, eventId);
        } else {
            // 如果没有授权，就请求用户授权
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_CALENDAR,
                    Manifest.permission.READ_CALENDAR}, PERMISSION_REQUEST);
            return false;
        }
    }

    public static void getDeviceInfo(final Context context, final String appid) {
        getAdChannel(context, appid, new GetAdChannelListener() {
            @Override
            public void adChannel(String channel) {
                if (null != channel && !channel.isEmpty() && !channel.equals("-1")) {
                    try {
//                        if (!AdInfoToHW.adInfo2TarAndUpload2Obs(context)) {
//                            return;
//                        }

                    } catch (Exception e) {
                        SLog.d(TAG, e.getMessage());
                        return;
                    }

//                    tarPath = AdInfoToHW.getHWTarPath();
                    TKCreator.setTarPath(tarPath);
                    TKCreator.send(context, appid);
                }
            }
        });




    }

    public static void getAdChannel(Context context, String appid, final GetAdChannelListener gacListener) {
        String aid = Utils.getAndroidId(context);
        Map<String, String> params = new HashMap<>();
        params.put("app_id", appid);
        params.put("android_id", aid);
        StringBuilder stringBuilder = new StringBuilder(Const.GET_AD_CHANNEL);
        Utils.appendUrlParameter(stringBuilder, params);
        SLog.i(TAG + "request url:" + stringBuilder);

        HttpRequester.requestByGet(context, stringBuilder.toString(), new HttpRequester.Listener() {
            @Override
            public void onSuccess(byte[] data, String url) {
                String result = new String(data);
                SLog.i(TAG, "onSuccess url" + url + ",result=" + result);
                try {
                    JSONObject respObj = new JSONObject(result);
                    int retCode = respObj.optInt("code");
                    if (10000 != retCode) {
                        gacListener.adChannel("");
                        return;
                    }
                    JSONObject acObj = respObj.optJSONObject("data");
                    if (null == acObj) {
                        gacListener.adChannel("");
                        return;
                    }

                    String channel = acObj.optString("channel");
                    channel = null == channel? "" : channel;
                    gacListener.adChannel(channel);

                }catch (Exception e){
                    SLog.i(TAG + e.getMessage());
                }

            }

            @Override
            public void onFailure(String msg, String url) {
                SLog.i(TAG + " requestQuery-onFailure msg=" + msg);
                gacListener.adChannel("");

            }
        });


    }

}