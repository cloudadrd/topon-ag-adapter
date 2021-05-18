package com.zcoup.multidownload.service;

import android.content.Context;
import android.util.Log;

import com.zcoup.multidownload.MultiDownloadManager;
import com.zcoup.multidownload.db.ThreadDAO;
import com.zcoup.multidownload.db.ThreadDAOImple;
import com.zcoup.multidownload.entitis.FileInfo;
import com.zcoup.multidownload.entitis.ThreadInfo;
import com.zcoup.multidownload.util.Logger;
import com.zcoup.multidownload.util.SSLUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;


public class DownloadTask {

    public static ExecutorService sExecutorService = Executors.newCachedThreadPool();

    private Context mContext;
    public FileInfo mFileInfo;
    private int mThreadCount;

    private ThreadDAO mDao;
    private long finishLen;

    private long mFinished = 0;
    public boolean mIsPause = false;
    private List<DownloadThread> mThreadlist;

    private ListenerTaskFinish mListenerTaskFinish;

    public DownloadTask(Context context, FileInfo fileInfo, int threadCount, ListenerTaskFinish listenerTaskFinish) {
        super();
        this.mContext = context;
        this.mFileInfo = fileInfo;
        this.mThreadCount = threadCount;
        this.mDao = new ThreadDAOImple(mContext);
        this.mListenerTaskFinish = listenerTaskFinish;
        this.finishLen = 0;
    }

    public void download() {

        List<ThreadInfo> list = mDao.queryThreads(mFileInfo.getUrl());
        mThreadlist = new ArrayList<>();

        if (list.size() == 0) {
            long length = mFileInfo.getLength();
            long block = length / mThreadCount;
            for (int i = 0; i < mThreadCount; i++) {
                long start = i * block;
                long end = (i + 1) * block - 1;
                if (i == mThreadCount - 1) {
                    end = length;
                }
                ThreadInfo threadInfo = new ThreadInfo(i, mFileInfo.getUrl(), start, end, 0);
                list.add(threadInfo);
                mDao.insertThread(threadInfo);
            }
        }

        for (ThreadInfo info : list) {
            if (isWriteFinish(info)) {
                finishLen += info.getFinished();
            } else {
                DownloadThread thread = new DownloadThread(info);
                DownloadTask.sExecutorService.execute(thread);
                mThreadlist.add(thread);
            }
        }

    }


    class DownloadThread extends Thread {
        private ThreadInfo threadInfo;
        public boolean isFinished = false;
        public boolean isError = false;
        private HttpURLConnection conn = null;
        private RandomAccessFile raf = null;
        private InputStream is = null;

        public DownloadThread(ThreadInfo threadInfo) {
            this.threadInfo = threadInfo;
        }

        @Override
        public void run() {

            try {

                String url = mFileInfo.getUrl();
                handleConnection(url);

            } catch (Exception e) {
                isError = true;
                checkAllError();
                Logger.log("get http tid:" + threadInfo.getId() + ", err:" + Log.getStackTraceString(e));
            } finally {
                if (conn != null) {
                    conn.disconnect();
                    Logger.log("DownloadTask >> disconnect >>> ,DownloadThread instanceId=" + this.hashCode());
                }
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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


        private void handleConnection(String url) throws Exception {
            Logger.log("DownloadTask >> handleConnection >> " + url + ",DownloadThread instanceId=" + this.hashCode());

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
            conn.setReadTimeout(mFileInfo.getTimeOut() * 1000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept-Encoding", "identity");

            long start = threadInfo.getStart() + threadInfo.getFinished();
            Logger.log("start:" + threadInfo.getStart() + ",end:" + threadInfo.getEnd() + ",finished:" + threadInfo.getFinished());
            conn.setRequestProperty("Range", "bytes=" + start + "-" + threadInfo.getEnd());
            File file = new File(mFileInfo.getSaveDir(), mFileInfo.getFileName());
            raf = new RandomAccessFile(file, "rwd");
            raf.seek(start);
            mFinished += threadInfo.getFinished();

            int code = conn.getResponseCode();
            Logger.log("DownloadTask >> http code:" + code);

            switch (code) {
                case HttpURLConnection.HTTP_OK:
                case HttpURLConnection.HTTP_PARTIAL:
                    handle200(conn);
                    break;
                case HttpURLConnection.HTTP_MOVED_TEMP:
                case HttpURLConnection.HTTP_MOVED_PERM:
                case HttpURLConnection.HTTP_SEE_OTHER:
                    handle302(conn);
                    break;

            }

        }

        private void handle200(HttpURLConnection conn) throws Exception {

            is = conn.getInputStream();
            byte[] bt = new byte[1024];
            int len;
            long time = System.currentTimeMillis();
            while ((len = is.read(bt)) != -1) {
                raf.write(bt, 0, len);
                mFinished += len;
                threadInfo.setFinished(threadInfo.getFinished() + len);

                mDao.updateThread(threadInfo.getUrl(), threadInfo.getId(), threadInfo.getFinished());

                if (System.currentTimeMillis() - time > 1000) {
                    time = System.currentTimeMillis();
                    mFileInfo.setFinished(((finishLen + mFinished) * 100 / mFileInfo.getLength()));
                    mFileInfo.getLoadListener().onUpdate(mFileInfo);
                }

                if (mIsPause) {
                    return;
                }
            }

            if (isWriteFinish(threadInfo)) {
                isFinished = true;
                Logger.log("tid:" + threadInfo.getId() + " is finished!");
                checkAllFinished();
            } else {
                isError = true;
                checkAllError();
            }

        }

        private void handle302(HttpURLConnection conn) throws Exception {
            Logger.log("DownloadTask >> handle302 >>> ");
            String location = conn.getHeaderField("Location");
            conn.disconnect();//解决302无法释放的问题
            Logger.log("DownloadTask >> 302-disconnect >>> ,DownloadThread instanceId=" + this.hashCode());
            handleConnection(location);
        }

    }


    private synchronized void checkAllFinished() {
        boolean allFinished = true;
        for (DownloadThread thread : mThreadlist) {
            if (!thread.isFinished) {
                allFinished = false;
                break;
            }
        }
        if (allFinished) {
            mFileInfo.setEnd(true);
            mDao.deleteThread(mFileInfo.getUrl());
            mFileInfo.getLoadListener().onSuccess(mFileInfo);
            mListenerTaskFinish.finish(mFileInfo);
        }
    }

    private synchronized void checkAllError() {
        boolean allError = true;
        for (DownloadThread thread : mThreadlist) {
            if (!thread.isError) {
                allError = false;
                break;
            }
        }
        if (allError) {
            Logger.log("allError=true isAutoRetry=" + mFileInfo.isAutoRetry());
            mFileInfo.setError(true);
            mFileInfo.setEnd(true);
            if (mFileInfo.isAutoRetry()) {
                MultiDownloadManager.startDownloadFile(mContext, mFileInfo);
            } else {
                mFileInfo.getLoadListener().onFailed(mFileInfo);
                mListenerTaskFinish.finish(mFileInfo);
            }
        }
    }


    private boolean isWriteFinish(ThreadInfo threadInfo) {
        return (threadInfo.getStart() + threadInfo.getFinished()) >= threadInfo.getEnd();
    }

    public void destroy() {
        if (mDao != null) {
            mDao.destroy();
        }
    }


    public interface ListenerTaskFinish {
        void finish(FileInfo fileInfo);
    }

}
