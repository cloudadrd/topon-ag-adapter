package com.business.support.sxe;

import android.content.Context;

import com.business.support.compose.ISdkMain;
import com.business.support.compose.SdkType;
import com.business.support.compose.TaskResult;
import com.business.support.compose.TaskResultListener;
import com.business.support.utils.SLog;

import net.security.device.api.SecurityCode;
import net.security.device.api.SecurityDevice;
import net.security.device.api.SecuritySession;

import org.json.JSONException;
import org.json.JSONObject;

public class AliYunImpl implements ISdkMain {

    private static final String TAG = "ShuzilmImpl";

    private TaskResultListener mListener;

    private Context mContext;

    private int queryCount = 0;

    @Override
    public boolean init(Context context, String... params) {
        mContext = context;
        if (params.length < 1) {
            SLog.e(TAG + " params error");
            return false;
        }
        // 初始化设备风险SDK，init接口需要在APP启动尽可能早的时候调用。
        SecurityDevice.getInstance().init(context, params[0], null);
        return true;
    }

    @Override
    public void requestQuery(TaskResultListener listener) {
        mListener = listener;
        SecuritySession securitySession = SecurityDevice.getInstance().getSession();
        if (null != securitySession) {
            if (SecurityCode.SC_SUCCESS == securitySession.code) {
//                Log.d("AliyunDevice", "session: " + securitySession.session);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("ali_did", securitySession.session);
                } catch (JSONException ignored) {
                }
                if (mListener != null) {
                    mListener.result(new TaskResult(false, 0, jsonObject.toString(), SdkType.ALI_YUN, 0));
                }
            } else {
                if (mListener != null) {
                    mListener.result(new TaskResult(true, 0, securitySession.session, SdkType.ALI_YUN, 0));
                }
//                Log.e("AliyunDevice", "getSession error, code: " + securitySession.code);
            }
        } else {
            if (mListener != null) {
                mListener.result(new TaskResult(true, 0, "session is null", SdkType.ALI_YUN, 3));
            }
//            Log.e("AliyunDevice", "getSession is null.");
        }
    }


}