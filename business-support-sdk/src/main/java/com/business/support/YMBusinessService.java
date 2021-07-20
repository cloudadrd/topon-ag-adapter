package com.business.support;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.business.support.adinfo.BSAdType;
import com.business.support.ascribe.InstallListener;
import com.business.support.ascribe.InstallStateMonitor;
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
import com.business.support.smsdk.SmeiImpl;
import com.business.support.utils.ContextHolder;
import com.business.support.utils.SLog;
import com.business.support.utils.Utils;
import com.business.support.webview.AdVideoInterface;
import com.business.support.webview.CacheWebView;
import com.business.support.webview.InnerWebViewActivity;
import com.business.support.webview.InnerWebViewActivity2;
import com.business.support.webview.WebViewToNativeListener;
import com.bytedance.sdk.openadsdk.activity.base.TTRewardVideoActivity;
import com.qq.e.ads.ADActivity;
import com.qq.e.comm.managers.GDTADManager;
import com.qq.e.comm.pi.POFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Collection;

import cn.thinkingdata.android.ThinkingAnalyticsSDK;

public class YMBusinessService {
    private static final String TAG = "YMBusinessService";
    private static long mAppInstallTime = 0;
    private static long mDays = 0;
    private static int mNumberOfTimes = 0;
    private static JSONObject jsonPangleObj = null;

    private static JSONObject jsonGdtObj = null;

    private static ThinkingAnalyticsSDK mInstance = null;

    public static void init(final Context context, ThinkingAnalyticsSDK instance, String shuMengApiKey, final SIDListener listener) {
        ContextHolder.init(context);
        mInstance = instance;
        final Context localContext = ContextHolder.getGlobalAppContext();
        SdkTaskManager.getInstance()
                .add(new ShuzilmImpl(), 100, 3000, shuMengApiKey)
                .add(new SmeiImpl(), 2000, 3000, "JVjHfrQd0LwfAFnND60C", "OfJKRbsUQIunw1xzb2SU", "MIIDLzCCAhegAwIBAgIBMDANBgkqhkiG9w0BAQUFADAyMQswCQYDVQQGEwJDTjELMAkGA1UECwwCU00xFjAUBgNVBAMMDWUuaXNodW1laS5jb20wHhcNMjEwNTA2MDMzMDEwWhcNNDEwNTAxMDMzMDEwWjAyMQswCQYDVQQGEwJDTjELMAkGA1UECwwCU00xFjAUBgNVBAMMDWUuaXNodW1laS5jb20wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCETlLQHou1ywPznJ9VeLwals2/FwyDzqrlr34h9kIc/O3C1pkXsICHE7z+DoLvI59FLUxFLDwaf2ywSylfv5m4arUxku/YBQoq85c4iucJonhv7mlg/KIdl94Kd4ajlsB0ZYFRUiIu/A1yePJmAvaGX9Z3AMw3ZoAV71RY5tVIH8KuzH/J6lnagIknN8OB5OglUEzDRhGtQEZD54SCz/it4AJ6M/vKSUdjALMpw4zKyBe3qR9gftOYI6J2S6wHT8Nc6u59X2G8nvTL0f+s9TyXdvy0jvrP3961eAebUGxwthr3ny+WrJASHymMG70rvK2wvS2TfxdtctP8KCFIEBmBAgMBAAGjUDBOMB0GA1UdDgQWBBQ3fMAEBSTHQflJgXBVqrC4JZXWSjAfBgNVHSMEGDAWgBQ3fMAEBSTHQflJgXBVqrC4JZXWSjAMBgNVHRMEBTADAQH/MA0GCSqGSIb3DQEBBQUAA4IBAQAJPorB5hV1JTo4WzTD0/5iLenV+VWF4j2HXp9OzEryDlJ19ax94QCxvCL2XSEqkNKviKvZksTz221q32V1xdTJPC3AqNd15Gn2msyu3VK8/efLxItmjvxH69//Obh3GZu5XHcLPwlt3/UHd3vBvCNXmZgyo0EHTeSXpr3P4utZVx6IBFM1gifcYTK8p3fVWbNf4RngMKmKleOzLhJwrussv+VZSudebMxclvNAgO1rRLXPKrwSoih2F4SUlHjahSopeMfyDTStdZ5oezOzb+y2ibmtCgf5SF9Dxqbyi8Kyx/ZS63ey63b2CchiK2iJCyDSWOVHysKsOhpI1TrbExKd")
                .zip(localContext, new ZipSidListener() {
                    @Override
                    public void result(Collection<TaskResult> taskResults) {
                        int score = 0;
                        String data = null;
                        for (TaskResult taskResult : taskResults) {
                            if (taskResult.isError) continue;
                            score += taskResult.getScore();
                            if (data != null) {
                                data = Utils.combineJson(data, taskResult.getData());
                            } else {
                                data = taskResult.getData();
                            }
                        }
                        if (TextUtils.isEmpty(data)) {
                            data = "{}";
                        }
                        composeNativeValid(localContext, score, data, listener);
                    }
                });

        optimizeAdInfo();
        InstallStateMonitor.register(localContext, new MyInstallListener());
    }

    public static final class MyInstallListener implements InstallListener {

        @Override
        public void installedHit(String pkg) {
            SLog.i(TAG, "installedHit pkg=" + pkg);
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("ad_channel", "tt");
                jsonObject.put("pkg_name", pkg);
                if (mInstance != null) {
                    mInstance.track("ad_install", jsonObject);
                    mInstance.flush();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    /**
     * 带缓存的webview，可以提前创建cacheWebView并且加载
     */
    public static void startCacheWebViewPage(Context context, CacheWebView cacheWebView, WebViewToNativeListener listener) {
        if (cacheWebView.getParent() != null && cacheWebView.getParent() instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) cacheWebView.getParent();
            viewGroup.removeView(cacheWebView);
        }
        AdVideoInterface.nativeListener = listener;
        //带缓存的webview，可以提前创建cacheWebView并且加载
        InnerWebViewActivity.launch(context, cacheWebView);
    }

    /**
     * //不带缓存的webview
     */
    public static void startWebViewPage(Context context, String linkUrl, WebViewToNativeListener listener) {
        AdVideoInterface.nativeListener = listener;
        InnerWebViewActivity2.launch(context, linkUrl);
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
                JSONObject properties = new JSONObject();
                properties.put("score", score);

                //自研
                properties.put("EmulatorCheck", jsonObject.get("Emulator"));
                properties.put("RootCheck", jsonObject.get("Root"));
                properties.put("HookCheck", jsonObject.get("Hook"));
                properties.put("WireSharkCheck", jsonObject.get("WireShark"));
                properties.put("debug", jsonObject.get("Debug"));

                //数盟
                if(jsonObject.has("device_type"))
                    properties.put("shumeng", jsonObject.get("device_type"));
                if(jsonObject.has("did"))
                    properties.put("shumengid", jsonObject.get("did"));

                //数美
                if(jsonObject.has("riskLevel"))
                    properties.put("riskLevel", jsonObject.get("riskLevel"));

                if(jsonObject.has("description"))
                    properties.put("description", jsonObject.get("description"));

                if(jsonObject.has("model"))
                    properties.put("model", jsonObject.get("model"));

                if(jsonObject.has("riskType"))
                    properties.put("riskType", jsonObject.get("riskType"));

                if(jsonObject.has("shuMeiDid"))
                    properties.put("shuMeiDid", jsonObject.get("shuMeiDid"));


                mInstance.track("Phonecheck", properties);
                mInstance.flush();
            }
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
                SLog.i(TAG, "onSuccess");
                try {
                    String result = new String(data);
                    JSONObject respObj = new JSONObject(result);
                    int retCode = respObj.optInt("code");
                    if (10000 != retCode) {
                        listener.isActive(false);
                        return;
                    }

                    JSONObject acObj = respObj.optJSONObject("data");
                    if (null == acObj) {
                        listener.isActive(false);
                        return;
                    }

                    boolean ac = acObj.getBoolean("status");
                    listener.isActive(ac);
                } catch (Exception e) {
                    listener.isActive(false);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String msg, String url) {
                SLog.i(TAG, "onFailure");
                listener.isActive(false);

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
                SLog.e(TAG, "onActivityPreCreated activity=" + activity.getComponentName());
//                pangelDataHandler(activity, savedInstanceState);
////                gdtDataHandler(activity, savedInstanceState);
            }

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                SLog.e(TAG, "onActivityCreated activity=" + activity.getComponentName());
                pangelDataHandler(activity, savedInstanceState);
                gdtDataHandler(activity, savedInstanceState);
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        };
        ((Application) ContextHolder.getGlobalAppContext().getApplicationContext()).registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

    private static void pangelDataHandler(Activity activity, Bundle savedInstanceState) {
        try{
            if (!(activity instanceof TTRewardVideoActivity)) {
                return;
            }

            Object c1 = com.bytedance.sdk.openadsdk.core.t.a().c();
            String materialMeta = "";
            JSONObject tempObj = null;
            if (c1 != null) {
                tempObj = com.bytedance.sdk.openadsdk.core.t.a().c().aO();
            }
            if (savedInstanceState != null) {
                materialMeta = savedInstanceState.getString("material_meta");
                if (!TextUtils.isEmpty(materialMeta)) {
                    tempObj = com.bytedance.sdk.openadsdk.core.b.a(new JSONObject(materialMeta)).aO();
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

                jsonPangleObj = new JSONObject();

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

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private static void gdtDataHandler(Activity activity, Bundle savedInstanceState) {
        try{
            if (activity instanceof ADActivity) {
                POFactory pOFactory = null;
                pOFactory = GDTADManager.getInstance().getPM().getPOFactory();

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

                Object parcelable = extras.getParcelable("admodel");
                ClassLoader classLoader = pOFactory.getClass().getClassLoader();
                Class<?> classData = classLoader.loadClass("com.qq.e.comm.plugin.model.BaseAdInfo");
                Field fieldJson = classData.getDeclaredField("ap");
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
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void setAdInfo(double ecpm, BSAdType
            adType) {
        if (mInstance == null)
            return;

        JSONObject jsonObject = null;

        if (BSAdType.PANGLE == adType) {
            jsonObject = jsonPangleObj;
            jsonPangleObj = null;
        } else if (BSAdType.GDT == adType) {
            jsonObject = jsonGdtObj;
            jsonGdtObj = null;
        }

        if (jsonObject == null) {
            SLog.e(TAG, "setAdInfo jsonObject is null, end report.");
            return;
        }
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
}