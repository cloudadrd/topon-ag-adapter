package com.business.support.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ImageUtils {

    public static final String TYPE_JPG = "jpeg";
    public static final String TYPE_GIF = "gif";
    public static final String TYPE_PNG = "png";
    public static final String TYPE_BMP = "bmp";
    public static final String TYPE_UNKNOWN = "unknown";

    /**
     * @return
     * @description: 根据文件流判断图片类型
     */
    public static String getPicType(File file) {
        FileInputStream fis = null;
        // 读取文件的前几个字节来判断图片格式
        byte[] b = new byte[4];
        try {
            fis = new FileInputStream(file);
            fis.read(b, 0, b.length);
            String type = bytesToHexString(b).toUpperCase();
            if (type.contains("FFD8FF")) {
                return TYPE_JPG;
            } else if (type.contains("89504E47")) {
                return TYPE_PNG;
            } else if (type.contains("47494638")) {
                return TYPE_GIF;
            } else if (type.contains("424D")) {
                return TYPE_BMP;
            } else {
                return TYPE_UNKNOWN;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * @return
     * @description: byte数组转换成16进制字符串
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * base64字符串转为drawable
     */
    public static BitmapDrawable getDrawable(String base64) {
        if (TextUtils.isEmpty(base64)) {
            return null;
        }
        String url = base64.substring(base64.indexOf(";") + ";base64,".length());
        byte[] rawImageData = Base64.decode(url, Base64.NO_WRAP);
        Bitmap bitmap = BitmapFactory.decodeByteArray(rawImageData, 0, rawImageData.length);

        if (bitmap == null) return null;

        Resources resources = Resources.getSystem();
        BitmapDrawable drawable = new BitmapDrawable(resources, bitmap);
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        int density = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, displayMetrics.xdpi, displayMetrics);
        drawable.setTargetDensity(density);
        return drawable;
    }

    /**
     * base64字符串转为drawable
     */
    public static byte[] base64ToBytes(String base64) {
        if (TextUtils.isEmpty(base64)) {
            return null;
        }
        String url = base64.substring(base64.indexOf(";") + ";base64,".length());

        return Base64.decode(url, Base64.NO_WRAP);
    }

}
