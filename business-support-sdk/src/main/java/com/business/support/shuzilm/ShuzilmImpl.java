package com.business.support.shuzilm;

import android.content.Context;

import com.business.support.compose.ISdkMain;
import com.business.support.compose.SdkType;
import com.business.support.compose.TaskResult;
import com.business.support.compose.TaskResultListener;
import com.business.support.config.Const;
import com.business.support.http.HttpRequester;
import com.business.support.utils.SLog;
import com.business.support.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.shuzilm.core.Listener;
import cn.shuzilm.core.Main;

public class ShuzilmImpl implements ISdkMain {

    private static final String TAG = "ShuzilmImpl";

    private TaskResultListener mListener;

    private Context mContext;

    @Override
    public boolean init(Context context, String... params) {
        mContext = context;
        if (params.length < 1) {
            SLog.e(TAG + " params error");
            return false;
        }
        Main.init(context, params[0]);
        return true;
    }

    @Override
    public void requestQuery(TaskResultListener listener) {
        mListener = listener;
        Main.getQueryID(mContext, "channel", "message", 1, new
                Listener() {
                    @Override
                    public void handler(String s) {
                        request(s);
                    }
                });
    }

    private void request(final String did) {
        Map<String, String> params = new HashMap<>();
        params.put("protocol", "2");
        params.put("pkg", mContext.getPackageName());
        params.put("did", did);
        params.put("ver", Utils.getAppVersion(mContext));
        StringBuilder stringBuilder = new StringBuilder(Const.SHUMENG_URL);
        Utils.appendUrlParameter(stringBuilder, params);
        HttpRequester.requestByGet(mContext, stringBuilder.toString(), new HttpRequester.Listener() {
            @Override
            public void onSuccess(byte[] data, String url) {
                resultHandler(did, data);
            }

            @Override
            public void onFailure(String msg, String url) {
                SLog.i(TAG + " requestQuery-onFailure msg=" + msg);
                if (mListener != null) {
                    mListener.result(new TaskResult(true, 0, msg, SdkType.SHUMENG));
                }
            }
        });
    }


    private void resultHandler(String did, byte[] data) {
        try {
            String strData = new String(data);
            SLog.i(TAG + " AdResponse==" + strData);

            JSONObject jsonObject = new JSONObject(strData);
            String errCode = Utils.optStringHelper(jsonObject, "err");
            String device_type = Utils.optStringHelper(jsonObject, "device_type");
//            String normal_times = Utils.optStringHelper(jsonObject, "normal_times");
//            String duplicate_times = Utils.optStringHelper(jsonObject, "duplicate_times");
//            String update_times = Utils.optStringHelper(jsonObject, "update_times");
//            String recall_times = Utils.optStringHelper(jsonObject, "recall_times");
            jsonObject.remove("protocol");
            jsonObject.remove("normal_times");
            jsonObject.remove("duplicate_times");
            jsonObject.remove("update_times");
            jsonObject.remove("recall_times");
            jsonObject.put("did", did);
            int score = 0;
            if ("0".equals(errCode)) {
                if ("1".equals(device_type)) {
                    score = 75;
                } else if ("2".equals(device_type)) {
                    score = 25;
                }
            }
            if (mListener != null) {
                mListener.result(new TaskResult(false, score, jsonObject.toString(), SdkType.SHUMENG));
            }

        } catch (JSONException e) {
            SLog.e(e);
            if (mListener != null) {
                mListener.result(new TaskResult(true, 0, e.getMessage(), SdkType.SHUMENG));
            }
        }
    }
}
