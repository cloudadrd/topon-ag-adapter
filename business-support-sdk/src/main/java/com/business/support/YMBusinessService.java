package com.business.support;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

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
import com.business.support.reallycheck.WireSharkCheck;
import com.business.support.shuzilm.ShuzilmImpl;
import com.business.support.smsdk.SmeiImpl;
import com.business.support.utils.ContextHolder;
import com.business.support.utils.SLog;
import com.business.support.utils.Utils;

import org.json.JSONObject;

import java.util.Collection;
import java.util.List;

public class YMBusinessService {
    private static final String TAG = "YMBusinessService";
    private static long mAppInstallTime = 0;
    private static long mDays = 0;
    private static int mNumberOfTimes = 0;

    public static void init(final Context context, String shuMengApiKey, final SIDListener listener) {
        ContextHolder.init(context);
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
    }


    private static void composeNativeValid(Context context, int score, String data, SIDListener listener) {

        ResultData emulatorResult = EmulatorCheck.validCheck(context);

        ResultData rootResult = RootCheck.validCheck(context);

        ResultData hookResult = HookCheck.validCheck(context);

        ResultData wireSharkResult = WireSharkCheck.validCheck(context);

        ResultData debugResult = DebugCheck.validCheck(context);

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


        try {
            JSONObject jsonObject = new JSONObject(data);
            jsonObject.put("Emulator", emulatorResult.isError());
            jsonObject.put("EmulatorMsg", emulatorResult.getErrorMessage());
            jsonObject.put("Hook", hookResult.isError());
            jsonObject.put("WireShark", wireSharkResult.isError());
            jsonObject.put("Root", rootResult.isError());
            jsonObject.put("Debug", debugResult.isError());
            jsonObject.put("DebugMsg", debugResult.getErrorMessage());

            if (listener != null) {
                listener.onSuccess(score, jsonObject.toString());
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

}