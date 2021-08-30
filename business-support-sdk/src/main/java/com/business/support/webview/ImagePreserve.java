package com.business.support.webview;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.Keep;

import com.business.support.http.DownloadManager;
import com.business.support.utils.ContextHolder;
import com.business.support.utils.FileUtils;
import com.business.support.utils.ImageResultListener;
import com.business.support.utils.ImageUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class ImagePreserve {

    @Keep
    public static final String PATH = ContextHolder.getGlobalAppContext().getCacheDir().getAbsolutePath() + "/image/";


    public static void downloadToSysPicture(String url, final ImageResultListener listener) {
        String fileName = UUID.randomUUID().toString();
        final File saveFile = new File(PATH + fileName);
        DownloadManager.download(url, saveFile, new DownloadManager.DownloadListener() {
            @Override
            public void onComplete(String url) {
                boolean result = save(saveFile);
                if (listener == null) return;
                if (result) {
                    listener.onSuccess();
                } else {
                    listener.onFailure("save to system picture error");
                }
            }

            @Override
            public void onFailure(String url) {
                if (listener == null) return;
                listener.onFailure("download failed url=" + url);
            }
        });
    }


    public static boolean save(File saveFile) {
        String picType = ImageUtils.getPicType(saveFile);
        String fileName = System.currentTimeMillis() + "." + picType;
        if (picType == null || ImageUtils.TYPE_UNKNOWN.equals(picType)) {
            return false;
        }
        Context context = ContextHolder.getGlobalAppContext();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            // 其次把文件插入到系统图库
            try {
                MediaStore.Images.Media.insertImage(context.getContentResolver(), saveFile.getAbsolutePath(), fileName, null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, saveFile.getAbsolutePath());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/" + picType);
            Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            ContextHolder.getGlobalAppContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
        } else {
            ContentValues values = new ContentValues();

            values.put(MediaStore.Images.Media.DESCRIPTION, "This is an image");
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/" + picType);
            values.put(MediaStore.Images.Media.TITLE, fileName);
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/");

            ContentResolver resolver = context.getContentResolver();
            Uri insertUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            BufferedInputStream inputStream = null;
            OutputStream os = null;
            try {
                inputStream = new BufferedInputStream(new FileInputStream(saveFile));
                if (insertUri != null) {
                    os = resolver.openOutputStream(insertUri);
                }
                if (os != null) {
                    byte[] buffer = new byte[1024 * 4];
                    int len;
                    while ((len = inputStream.read(buffer)) != -1) {
                        os.write(buffer, 0, len);
                    }
                    os.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FileUtils.delete(saveFile);
            }
        }

        return true;
    }

}
