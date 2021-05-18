package com.zcoup.multidownload.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.Keep;

import com.zcoup.multidownload.MultiDownloadManager;
import com.zcoup.multidownload.entitis.FileInfo;
import com.zcoup.multidownload.util.Logger;
import com.zcoup.multidownload.util.SSLUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

@Keep
public class DownloadControl {

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
//    private static final int MSG_INIT = 0;

    //多线程操作
    private static Map<String, DownloadTask> mTasks = new ConcurrentHashMap<>();

    @SuppressLint("StaticFieldLeak")
    private static DownloadControl downloadControl = new DownloadControl();

    public static DownloadControl getInstance(Context context) {
        mContext = context;
        return downloadControl;
    }

    public void start(final FileInfo fileInfo) {
        InitThread initThread = new InitThread(fileInfo);
        DownloadTask.sExecutorService.execute(initThread);
    }

    public void stop(FileInfo fileInfo) {
        DownloadTask task = mTasks.get(fileInfo.getUrl());
        if (task != null) {
            task.mIsPause = true;
        }
    }

    public void destroy() {
        if (mTasks != null && mTasks.size() > 0) {
            for (String url : mTasks.keySet()) {
                DownloadTask downloadTask = mTasks.get(url);
                if (downloadTask != null) {
                    downloadTask.mIsPause = true;
                    downloadTask.destroy();
                }
            }
        }
    }


//    @SuppressLint("HandlerLeak")
//    public static Handler mHandler = new Handler() {
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case MSG_INIT:
//                    FileInfo fileInfo = (FileInfo) msg.obj;
//
//                    //上次下载结束前，不对同一个url开始下载
//                    DownloadTask oldDownloadTask = mTasks.get(fileInfo.getUrl());
//                    if (oldDownloadTask != null) {
//                        FileInfo oldFileInfo = oldDownloadTask.mFileInfo;
//                        if (oldFileInfo != null && !oldFileInfo.isEnd()) {
//                            return;
//                        }
//                    }
//
//                    DownloadTask task = new DownloadTask(mContext, fileInfo, fileInfo.getThreadCount());
//                    task.download();
//                    mTasks.put(fileInfo.getUrl(), task);
//                    fileInfo.getLoadListener().onStart(fileInfo);
//                    break;
//            }
//        }
//    };

    static class InitThread extends Thread {

        private FileInfo mFileInfo;
        private HttpURLConnection conn;
        private RandomAccessFile raf;

        InitThread(FileInfo mFileInfo) {
            super();
            this.mFileInfo = mFileInfo;
        }

        @Override
        public void run() {

            try {
                String url = mFileInfo.getUrl();
                handleConnection(url);

            } catch (Exception e) {
                mFileInfo.setEnd(true);
                mFileInfo.setError(true);
                if (mFileInfo.isAutoRetry()) {
                    MultiDownloadManager.startDownloadFile(mContext, mFileInfo);
                } else {
                    mFileInfo.getLoadListener().onFailed(mFileInfo);
                }
                Logger.log("get http err:" + Log.getStackTraceString(e));
            } finally {
                if (conn != null) {
                    conn.disconnect();
                    Logger.log("DownloadService >> disconnect >>> ,InitThread instanceId=" + this.hashCode());
                }
                try {
                    if (raf != null) {
                        raf.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            super.run();
        }


        private void handleConnection(String url) throws IOException {
            Logger.log("DownloadService >> handleConnection >> url >>> " + url + ",InitThread instanceId=" + this.hashCode());

            URL finalUrl = new URL(url);
            conn = (HttpURLConnection) finalUrl.openConnection();

            if (conn instanceof HttpsURLConnection) {
                SSLSocketFactory sslSocketFactory = SSLUtils.defaultSSLSocketFactory();
                ((HttpsURLConnection) conn).setSSLSocketFactory(sslSocketFactory);
                HostnameVerifier hostnameVerifier = SSLUtils.defaultHostnameVerifier();
                if (hostnameVerifier != null) {
                    ((HttpsURLConnection) conn).setHostnameVerifier(hostnameVerifier);
                }
            }
            conn.setConnectTimeout(mFileInfo.getTimeOut() * 1000);
            conn.setReadTimeout(20 * 1000);
            conn.setRequestMethod("GET");
            int code = conn.getResponseCode();
            Logger.log("DownloadService >> http code:" + code);
            switch (code) {
                case HttpURLConnection.HTTP_OK:
                    handle200(conn);
                    break;
                case HttpURLConnection.HTTP_MOVED_TEMP:
                case HttpURLConnection.HTTP_MOVED_PERM:
                case HttpURLConnection.HTTP_SEE_OTHER:
                    handle302(conn);
                    break;
            }


        }

        private void handle200(HttpURLConnection conn) throws IOException {
            Logger.log("DownloadService >> handle200 >>> ");
            int length = conn.getContentLength();

            if (length <= 0) {
                return;
            }
            File dir = new File(this.mFileInfo.getSaveDir());
            if (!dir.exists()) {
                dir.mkdir();
            }
            File file = new File(dir, mFileInfo.getFileName());
            raf = new RandomAccessFile(file, "rwd");
            raf.setLength(length);
            mFileInfo.setLength(length);
//            Message msg = Message.obtain();
////            msg.obj = mFileInfo;
////            msg.what = MSG_INIT;
////            mHandler.sendMessage(msg);
            initTask();

        }

        //子线程执行
        private void initTask() {
            //上次下载结束前，不对同一个url开始下载
            DownloadTask oldDownloadTask = mTasks.get(mFileInfo.getUrl());
            if (oldDownloadTask != null) {
                FileInfo oldFileInfo = oldDownloadTask.mFileInfo;
                if (oldFileInfo != null && !oldFileInfo.isEnd()) {
                    return;
                }
            }

            DownloadTask task = new DownloadTask(mContext, mFileInfo, mFileInfo.getThreadCount(),
                    listenerTaskFinish);
            task.download();
            mTasks.put(mFileInfo.getUrl(), task);
            mFileInfo.getLoadListener().onStart(mFileInfo);
        }

        private DownloadTask.ListenerTaskFinish listenerTaskFinish = new DownloadTask.ListenerTaskFinish() {
            @Override
            public void finish(FileInfo fileInfo) {
                DownloadTask downloadTask = mTasks.get(fileInfo.getUrl());
                if (downloadTask != null) {
                    downloadTask.mIsPause = true;
                    downloadTask.destroy();
                }
                mTasks.remove(fileInfo.getUrl());
            }

        };

        private void handle302(HttpURLConnection conn) throws IOException {
            Logger.log("DownloadService >> handle302 >>> ");
            String location = conn.getHeaderField("Location");
            conn.disconnect();//解决302无法释放的问题
            Logger.log("DownloadService >> 302-disconnect >>> ,DownloadControl instanceId=" + this.hashCode());
            handleConnection(location);
        }

    }


}