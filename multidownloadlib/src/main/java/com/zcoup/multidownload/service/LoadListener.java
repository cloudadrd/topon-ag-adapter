package com.zcoup.multidownload.service;

import android.support.annotation.Keep;

import com.zcoup.multidownload.entitis.FileInfo;

/**
 * Created by huangdong on 18/5/7.
 */

@Keep
public interface LoadListener {

    void onStart(FileInfo fileInfo);
    void onUpdate(FileInfo fileInfo);
    void onSuccess(FileInfo fileInfo);
    void onFailed(FileInfo fileInfo);
}
