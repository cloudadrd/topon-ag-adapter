package com.zcoup.multidownload;

import android.content.Context;
import android.support.annotation.Keep;


import com.zcoup.multidownload.entitis.FileInfo;
import com.zcoup.multidownload.service.DownloadControl;
import com.zcoup.multidownload.util.Logger;
import com.zcoup.multidownload.util.Utils;

@Keep
public class MultiDownloadManager {

    private static final String TAG = "MultiDownloadManager";

    public static void startDownloadFile(Context mContext, FileInfo fileInfo) {
        if (mContext == null || fileInfo == null) {
            return;
        }

        if (!Utils.isNetAvailable(mContext)) {
            fileInfo.setError(true);
            fileInfo.setEnd(true);
            fileInfo.getLoadListener().onFailed(fileInfo);
            return;
        }

        try {
            DownloadControl.getInstance(mContext).start(fileInfo);
            fileInfo.setEnd(false);
        } catch (Exception e) {
            Logger.log(TAG, "startDownloadFile failed");
        }
    }


    public static void stopDownloadFile(Context mContext, FileInfo fileInfo) {
        if ((mContext == null) || (fileInfo == null)) {
            return;
        }

        try {
            DownloadControl.getInstance(mContext).stop(fileInfo);
            fileInfo.setEnd(true);
        } catch (Exception e) {
            Logger.log(TAG, "stopDownloadFile failed");
        }
    }


    public static void destroy(Context mContext) {
        try {
            DownloadControl.getInstance(mContext).destroy();
        } catch (Exception e) {
            Logger.log(TAG, "destory failed");
        }
    }

    public static void openDebug(boolean isDebug) {
        Logger.isDebug = isDebug;
    }

}
