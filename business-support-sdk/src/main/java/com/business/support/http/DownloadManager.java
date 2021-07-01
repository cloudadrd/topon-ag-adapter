package com.business.support.http;


import com.business.support.config.Const;
import com.business.support.utils.ContextHolder;
import com.business.support.utils.SLog;
import com.business.support.utils.ThreadPoolProxy;
import com.business.support.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * Created by jiantao.tu on 2019/11/11.
 */
public class DownloadManager {


    public final static int MAX_REQUEST_COUNT = 10;

    public static List<String> requestUrls = new ArrayList<>(MAX_REQUEST_COUNT);

    /**
     * 需要主线程执行
     *
     * @param url
     * @param saveDir
     * @param listener
     */
    public static void downloadAsync(final String url, final File saveDir, final DownloadListener
            listener) {
        if (requestUrls.size() >= 10) {
            if (listener != null) {
                SLog.e("downloadAsync download task is over quantity");
                listener.onFailure(url);
            }
            return;
        }
        if (requestUrls.contains(url)) {
            if (listener != null) {
                SLog.e("downloadAsync this download connection is already under download. Please do not download again");
                listener.onFailure(url);
            }
            return;
        }
        requestUrls.add(url);
        ThreadPoolProxy.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                download(url, saveDir, listener);
            }
        });
    }

    public static boolean download(final String url, final File saveDir) {
        return download(url, saveDir, null);
    }

    public static boolean download(final String url, final File saveDir, final DownloadListener
            listener) {
        RandomAccessFile accessFile = null;
        File tempFile = null;
        HttpURLConnection conn = null;
        try {
            String userAgentStr = Utils.getUserAgentStr(ContextHolder.getGlobalAppContext(), false);
            conn = HttpUtils.handleConnection(url, userAgentStr, RequestMethod.GET, null);
            InputStream is;
            if ("gzip".equals(conn.getContentEncoding())) {
                is = new GZIPInputStream(conn.getInputStream());
            } else {
                is = conn.getInputStream();
            }
            byte[] buffer = new byte[512];
            long len;

            tempFile = new File(saveDir.getParent(), saveDir.getName() + ".temp");
            accessFile = new RandomAccessFile(tempFile, "rw");
            while ((len = is.read(buffer)) != -1) {
                accessFile.write(buffer, 0, (int) len);
            }
        } catch (Exception e) {
            if (tempFile != null && tempFile.exists()) tempFile.delete();
            Const.HANDLER.post(new Runnable() {
                @Override
                public void run() {
                    requestUrls.remove(url);
                    if (listener != null) listener.onFailure(url);
                }
            });
            SLog.e(e);
            return false;
        } finally {
            if (accessFile != null) {
                try {
                    accessFile.close();
                } catch (IOException e) {
                    SLog.e(e);
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        final File finalTempFile = tempFile;
        Const.HANDLER.post(new Runnable() {
            @Override
            public void run() {
                requestUrls.remove(url);
                if (!finalTempFile.renameTo(saveDir)) {
                    if (listener != null) listener.onFailure(url);
                    return;
                }
                if (listener != null) listener.onComplete(url);
            }
        });

        return true;
    }

    public interface DownloadListener {
        void onComplete(String url);

        void onFailure(String url);
    }
}