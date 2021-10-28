package com.business.support.h5_update;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;


import com.business.support.config.Const;
import com.business.support.http.HttpRequester;
import com.business.support.utils.ContextHolder;
import com.business.support.utils.FileUtils;
import com.business.support.utils.SLog;
import com.business.support.utils.ThreadPoolProxy;
import com.business.support.utils.ZipUtils;
import com.zcoup.multidownload.MultiDownloadManager;
import com.zcoup.multidownload.entitis.FileInfo;
import com.zcoup.multidownload.service.LoadListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ResUpdateManager {


    private final static String VERSION_FILE_NAME = "bs_version";

    private static final String TAG = "ResUpdateManager";

//    private static final String ZIP_FILE_NAME = "forumweb.zip";
//
//    private static final String RES_DIR_FILE_NAME = "forumweb";
//    private static final String RES_DIR_TEMP_FILE_NAME = "forumweb_temp";
//
//    private static final String RES_TEMP_FILE_NAME = "forumweb.temp";
//
//    private static final int CURRENT_VERSION = 101;

    private static String GET_ZIP_FILE_NAME(String appId, String fileName) {
        return appId + fileName + ".zip";
    }

    private static String GET_RES_DIR_FILE_NAME(String appId, String fileName) {
        return appId + fileName;
    }

    private static String GET_RES_DIR_TEMP_FILE_NAME(String appId, String fileName) {
        return appId + fileName + "_temp";
    }

    private static String GET_RES_TEMP_FILE_NAME(String appId, String fileName) {
        return appId + fileName + ".temp";
    }

    public static File getRootDirFile() {
        return new File(ContextHolder.getGlobalAppContext().getFilesDir(), "res_h5");
    }

    public static File getResDirFile(String appId, String fileName) {
        return new File(getRootDirFile(), GET_RES_DIR_FILE_NAME(appId, fileName));
    }

    public static File getResZip(String appId, String fileName) {
        return new File(getRootDirFile(), GET_ZIP_FILE_NAME(appId, fileName));
    }

    public static File getResDirTemp(String appId, String fileName) {
        return new File(getRootDirFile(), GET_RES_DIR_TEMP_FILE_NAME(appId, fileName));
    }

    public static File getResVersionFile(String appId, String fileName) {
        return new File(getResDirFile(appId, fileName), VERSION_FILE_NAME);
    }

    public static File getResTempVersionFile(String appId, String fileName) {
        return new File(getResDirTemp(appId, fileName), VERSION_FILE_NAME);
    }


    public static boolean createVersionFile(boolean isTemp, int version, String appId, String fileName) {
        File forumweb = null;
        File versionFile = null;
        if (isTemp) {
            forumweb = getResDirTemp(appId, fileName);
            versionFile = getResTempVersionFile(appId, fileName);
        } else {
            forumweb = getResDirFile(appId, fileName);
            versionFile = getResVersionFile(appId, fileName);
        }

        if (!forumweb.exists() || !forumweb.isDirectory()) {
            return false;
        }

        if (!versionFile.exists()) {
            try {
                versionFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream fileInputStream = null;
        try {

            fileInputStream = new FileOutputStream(versionFile);
            fileInputStream.write(String.valueOf(version).getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;

    }

    public static int getVersion(String appId, String fileName) {
        File forumweb = getResDirFile(appId, fileName);
        if (!forumweb.exists() || !forumweb.isDirectory()) {
            return -1;
        }
        File versionFile = getResVersionFile(appId, fileName);
        if (!versionFile.exists() || !versionFile.isFile()) {
            return -1;
        }
        FileInputStream inputStream = null;
        try {
            byte[] bytes = new byte[64];
            inputStream = new FileInputStream(versionFile);
            int result = inputStream.read(bytes);
            if (result == -1) return result;
            return Integer.parseInt(new String(bytes, 0, result));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return -1;
    }

    /**
     * 获取H5资源地址并且检测服务端是否有可用更新
     * 有则下载覆盖，下次app启动时生效
     *
     * @param appId
     * @param channel
     * @param currentVersion
     * @param listener
     */
//    public static void getH5ResPathAndUpdate(final String appId, final String channel, final int currentVersion, final ResH5Listener listener) {
//        getH5ResPathAndUpdate(appId, "forumweb", channel, currentVersion, listener);
//    }
    public static void getH5ResPathAndUpdate(final String appId, final String fileName, final String channel, final int currentVersion, final ResH5Listener listener) {
        ThreadPoolProxy.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                execute(appId, fileName, channel, currentVersion, listener);
            }
        });
    }


    private static void execute(String appId, final String fileName, String channel, final int currentVersion, ResH5Listener listener) {
        Context context = ContextHolder.getGlobalAppContext();
        File destDir = getResDirFile(appId, fileName);
        File resDirTemp = getResDirTemp(appId, fileName);
        if (resDirTemp.exists() && resDirTemp.isDirectory()) {
            FileUtils.delete(destDir);
            resDirTemp.renameTo(destDir);
        }

        final int version = getVersion(appId, fileName);
        boolean isUnzip = version == -1;
        boolean isSuccess = true;
        if (isUnzip) {
            File subFile = null;
            try {
                subFile = getResZip(appId, fileName);
                if (!subFile.exists()) {
                    final String zipFileName = GET_ZIP_FILE_NAME(appId, fileName);
                    if (!AssetsFileManager.isFileExists(context, zipFileName)) {
                        throw new FileNotFoundException(zipFileName + " no exist");
                    }
                    AssetsFileManager.copyAssets(context, zipFileName, subFile, "700");
                }
                FileUtils.delete(destDir);
                List<File> fileList = ZipUtils.unzipFile(subFile, destDir);
                if (fileList == null || fileList.size() == 0) {
                    isSuccess = false;
                } else {
                    if (!createVersionFile(false, currentVersion, appId, fileName)) {
                        isSuccess = false;
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                isSuccess = false;
            } finally {
                FileUtils.delete(subFile);
            }
        }

        if (listener != null) {
            File file = new File(destDir, "index.html");
//            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".bssdk", file);
            listener.result(isSuccess, "file://" + file.getAbsolutePath());
        }

        JSONObject requestBody = new JSONObject();

        try {
            requestBody.put("channelId", channel);
            requestBody.put("appId", appId);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        HttpRequester.requestByPost(context, Const.RES_H5_VERSION_URL, requestBody.toString(), new HttpRequester.Listener() {

            @Override
            public void onSuccess(byte[] data, String url) {
                try {
                    String str = new String(data);
                    JSONObject jsonObject = new JSONObject(str);
                    if (jsonObject.optInt("code") != 200) {
                        return;
                    }
                    JSONObject dataObj = jsonObject.optJSONObject("data");
                    if (dataObj == null) return;
                    int newVersion = dataObj.optInt("version");
                    int tempVersion = version == -1 ? currentVersion : version;
                    if (newVersion > tempVersion) {
                        String downloadUrl = dataObj.optString("downloadFile");
                        if (newVersion > 0 && !TextUtils.isEmpty(downloadUrl)) {
                            downZipAndUnZip(appId, fileName, downloadUrl, getRootDirFile().getAbsolutePath(), newVersion);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(String msg, String url) {
                SLog.e(TAG + " requestQuery-onFailure msg=" + msg);
            }
        });
    }


    public static void downZipAndUnZip(String appId, String fileName, String url, final String saveDir, final int version) {
        FileInfo fileInfo = new FileInfo(url, GET_RES_TEMP_FILE_NAME(appId, fileName), saveDir, 3, 80,
                true, new LoadListener() {
            @Override
            public void onStart(FileInfo fileInfo) {
                SLog.i(TAG, "开始下载: >> " + fileInfo.getFileName());
            }

            @Override
            public void onUpdate(FileInfo fileInfo) {
                SLog.i(TAG, "下载中: >> " + fileInfo.getFileName() + " >>下载进度: " + fileInfo.getFinished());
            }

            @Override
            public void onSuccess(FileInfo fileInfo) {
                SLog.i(TAG, "下载成功: >> " + fileInfo.getFileName());
                ThreadPoolProxy.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String filePath = saveDir + File.separator + GET_RES_TEMP_FILE_NAME(appId, fileName);
                            File file = new File(filePath);
                            File zipFile = getResZip(appId, fileName);
                            if (file.exists() && file.isFile()) {
                                FileUtils.delete(zipFile);
                                file.renameTo(zipFile);
                            }
                            FileUtils.delete(file);
                            //解压
                            File destDir = new File(saveDir, GET_RES_DIR_TEMP_FILE_NAME(appId, fileName));
                            FileUtils.delete(destDir);

                            ZipUtils.unzipFile(zipFile, destDir);
                            createVersionFile(true, version, appId, fileName);
                            FileUtils.delete(zipFile);
                            FileUtils.delete(file);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            }

            @Override
            public void onFailed(FileInfo fileInfo) {
                SLog.i(TAG, "下载失败: >> " + fileInfo.getFileName());

            }
        });
        MultiDownloadManager.startDownloadFile(ContextHolder.getGlobalAppContext(), fileInfo);
    }

}
