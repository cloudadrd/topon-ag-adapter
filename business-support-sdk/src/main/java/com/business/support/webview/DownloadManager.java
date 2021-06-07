package com.business.support.webview;

import android.content.Context;
import android.support.annotation.Keep;
import android.util.Log;

import com.business.support.utils.ContextHolder;
import com.zcoup.multidownload.MultiDownloadManager;
import com.zcoup.multidownload.entitis.FileInfo;
import com.zcoup.multidownload.service.LoadListener;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DownloadManager {

    @Keep
    public static final String PATH = ContextHolder.getGlobalAppContext().getCacheDir().getAbsolutePath() + "/creative/";


    public static final Map<String, DownloadInfo> APK_LIST = new ConcurrentHashMap<>();

    public static class DownloadInfo {
        String path;
        DownloadState downState;
    }

    public static void cleanTimeOutFiles() {
        File parentFile = new File(PATH);
        if (!(parentFile.exists() && parentFile.isDirectory())) return;
        for (File file : parentFile.listFiles()) {
            long surplus = System.currentTimeMillis() - file.lastModified();
            final long term = 259200000;
            if (surplus >= term) {
                file.delete();
            }
        }
    }

    public static void deleteFile(String pkg) {
        DownloadInfo info = APK_LIST.get(pkg);
        String path;
        if (info != null) {
            path = info.path;
        } else {
            path = PATH + getFileName(pkg);
        }
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    public static boolean isFileExists(String pkg) {
        String path = PATH + getFileName(pkg);
        File file = new File(path);
        return file.exists();
    }

    public static String getPath(String pkg) {
        return PATH + getFileName(pkg);
    }

    private static String getFileName(String pkg) {
        return pkg + ".apk";
    }


    private final static String TAG = "DownloadManager";

    public static void download(Context context, String url, final String packageName, final LoadListener loadListener) {

        DownloadInfo info = APK_LIST.get(packageName);
        if (info != null) {
            switch (info.downState) {
                case START:
                    return;
                case SUCCESS:
                    if (loadListener != null) {
                        FileInfo file = new FileInfo();
                        file.setFileName(packageName);
                        loadListener.onSuccess(file);
                    }
                    return;
            }
        }

        FileInfo fileInfo = new FileInfo(url, getFileName(packageName) + ".temp", PATH, 3, 80,
                true, new LoadListener() {
            @Override
            public void onStart(FileInfo fileInfo) {
                Log.i(TAG, "开始下载: >> " + fileInfo.getFileName());
                DownloadInfo info = new DownloadInfo();
                info.path = PATH + fileInfo.getFileName();
                info.downState = DownloadState.START;
                APK_LIST.put(packageName, info);
                if (loadListener != null) {
                    loadListener.onStart(fileInfo);
                }
            }

            @Override
            public void onUpdate(FileInfo fileInfo) {
                Log.i(TAG, "下载中: >> " + fileInfo.getFileName() + " >>下载进度: " + fileInfo.getFinished());
                if (loadListener != null) {
                    loadListener.onUpdate(fileInfo);
                }
            }

            @Override
            public void onSuccess(FileInfo fileInfo) {
                Log.i(TAG, "下载成功: >> " + fileInfo.getFileName());

                DownloadInfo info = APK_LIST.get(packageName);
                if (info != null) {
                    info.downState = DownloadState.SUCCESS;
                }
                try {
                    String filePath = getPath(packageName) + ".temp";
                    File file = new File(filePath);
                    if (file.exists() && file.isFile()) {
                        file.renameTo(new File(filePath.replace(".temp", "")));
                    }
                    new File(filePath).delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (loadListener != null) {
                    loadListener.onSuccess(fileInfo);
                }
            }

            @Override
            public void onFailed(FileInfo fileInfo) {
                Log.i(TAG, "下载失败: >> " + fileInfo.getFileName());
                DownloadManager.APK_LIST.remove(packageName);
                if (loadListener != null) {
                    loadListener.onFailed(fileInfo);
                }
            }
        });
        MultiDownloadManager.startDownloadFile(context, fileInfo);
    }
}
