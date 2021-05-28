package com.business.support.smsdk;

import android.content.Context;
import android.icu.text.UnicodeMatcher;
import android.text.TextUtils;

import com.business.support.compose.ISdkMain;
import com.business.support.compose.SdkType;
import com.business.support.compose.TaskResult;
import com.business.support.compose.TaskResultListener;
import com.business.support.config.Const;
import com.business.support.http.HttpRequester;
import com.business.support.utils.SLog;
import com.business.support.utils.Utils;
import com.ishumei.smantifraud.SmAntiFraud;

import org.json.JSONException;
import org.json.JSONObject;

public class SmeiImpl implements ISdkMain {

    private static final String TAG = "SmeiImpl";
    private String clientIp = "";

    private Context mContext;


    private String accessKey;

    private TaskResultListener mListener;

    @Override
    public boolean init(Context context, String... params) {
        if (params.length < 2) {
            SLog.e(TAG + " params error");
            return false;
        }
        requestIp();
        mContext = context;
        SmAntiFraud.SmOption option = new SmAntiFraud.SmOption();
        option.setOrganization(params[0]);//必填，组织标识，邮件中 organization 项
        option.setAppId(context.getPackageName()); //必填，应用标识，登录数美后台应用管理查看，没有合适值，可以写 default
        accessKey = params[1];
        option.setPublicKey(params[2]); //必填，加密 KEY，邮件中 android_public_key 附件内容
        option.setArea(SmAntiFraud.AREA_BJ);
        String host = "http://fp-it-acc.fengkongcloud.com";
        option.setUrl(host + "/deviceprofile/v4");
        option.setConfUrl(host + "/v3/cloudconf");
        SmAntiFraud.create(context, option);
        return true;
    }


    public void requestIp() {
        HttpRequester.requestByGet(mContext, Const.IP_URL, new HttpRequester.Listener() {
            @Override
            public void onSuccess(byte[] data, String url) {
                try {
                    String strData = new String(data);
                    JSONObject jsonObject = new JSONObject(strData);
                    String msg = jsonObject.optString("msg");
                    if (jsonObject.optInt("code") != 10000) {
                        SLog.e(TAG + " request ip error");
                        return;
                    }
                    JSONObject dataObj = jsonObject.optJSONObject("data");
                    String ip = null;
                    if (dataObj != null) {
                        ip = dataObj.optString("ip");
                    }
                    if (!TextUtils.isEmpty(ip)) {
                        clientIp = ip;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String msg, String url) {
                SLog.e("requestIp-onFailure msg=" + msg);
            }
        });
    }

    @Override
    public void requestQuery(TaskResultListener listener) {
        mListener = listener;
        final String deviceId = SmAntiFraud.getDeviceId();
        if (TextUtils.isEmpty(clientIp) || TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(accessKey)) {
            SLog.e(TAG + " requestQuery fail,clientIp=" + clientIp + ",deviceId=" + deviceId + ",accessKey=" + accessKey);
            if (listener != null) {
                listener.result(new TaskResult(true, 0, "", SdkType.SHUMEI));
            }
        }
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("accessKey", accessKey);
            jsonObj.put("appId", mContext.getPackageName());
            jsonObj.put("eventId", "activation");
            JSONObject dataObj = new JSONObject();
            dataObj.put("ip", clientIp);
            dataObj.put("deviceId", deviceId);
            dataObj.put("isTokenSeperate", 1);
            dataObj.put("timestamp", System.currentTimeMillis());
            dataObj.put("advertisingId", "");
            dataObj.put("apputm", "");
            dataObj.put("os", "android");
            dataObj.put("appVersion", Utils.getAppVersion(mContext));
            jsonObj.put("data", dataObj);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String requestBody = jsonObj.toString();
        SLog.d(TAG + " requestBody=" + requestBody);
        HttpRequester.requestByPost(mContext, Const.SHUMEI_URL, requestBody, new HttpRequester.Listener() {

            @Override
            public void onSuccess(byte[] data, String url) {
                resultHandler(deviceId, data);
            }

            @Override
            public void onFailure(String msg, String url) {
                SLog.e(TAG + " requestQuery-onFailure msg=" + msg);
                if (mListener != null) {
                    mListener.result(new TaskResult(true, 0, msg, SdkType.SHUMEI));
                }
            }
        });

    }


    private void resultHandler(String deviceId, byte[] data) {
        String strData = new String(data);
        SLog.d(TAG + " strData=" + strData);
        try {
            JSONObject jsonObj = new JSONObject(strData);
            String riskLevel = jsonObj.optString("riskLevel");
            JSONObject jsonDetail = jsonObj.optJSONObject("detail");
            if (jsonDetail == null) {
                throw new NullPointerException(TAG + " detail is null");
            }
            String model = jsonDetail.optString("model");
            int riskType = jsonDetail.optInt("riskType");
            String description = jsonDetail.optString("description");
            int score = 0;
            switch (riskLevel) {
                case "REJECT":
                    score = 75;
                    break;
                case "REVIEW":
                    score = 35;
                    break;
                case "VERIFY":
                    score = 30;
                    break;
                case "PASS":
                default:
            }
            JSONObject resultObj = new JSONObject();
            resultObj.put("riskLevel", riskLevel);
            resultObj.put("description", description);
            resultObj.put("model", model);
            resultObj.put("riskType", riskType);
            resultObj.put("shuMeiDid", deviceId);
            if (mListener != null) {
                mListener.result(new TaskResult(false, score, resultObj.toString(), SdkType.SHUMEI));
            }
        } catch (Exception e) {
            SLog.e(e);
            if (mListener != null) {
                mListener.result(new TaskResult(true, 0, e.getMessage(), SdkType.SHUMEI));
            }
        }

    }
}
