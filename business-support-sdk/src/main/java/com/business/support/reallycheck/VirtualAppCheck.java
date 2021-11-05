package com.business.support.reallycheck;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import com.business.support.utils.CommandUtils;
import com.business.support.utils.SLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class VirtualAppCheck {


    private static final String TAG = "VirtualAppCheck";

    public static ResultData validCheck(Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        int score = 0;
        try {
            if (checkByPrivateFilePath(context)) {
    //            Log.e(TAG, "checkByPrivateFilePath");
                stringBuilder.append("1");
                score += 30;
            }

            if (pathCheck(context)) {
    //            Log.e(TAG, "pathCheck");
                stringBuilder.append(",2");
                score += 30;
            }

            if (packageCheck(context)) {
    //            Log.e(TAG, "packageCheck");
                stringBuilder.append(",3");
                score += 10;
            }

            if (processCheck()) {
    //            Log.e(TAG, "processCheck");
                stringBuilder.append(",4");
                score += 10;
            }

            if (mapsCheck()) {
    //            Log.e(TAG, "mapsCheck");
                stringBuilder.append(",5");
                score += 10;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResultData(!TextUtils.isEmpty(stringBuilder), stringBuilder.toString(), score);
    }

    /**
     * 维护一份市面多开应用的包名列表
     */
    private static String[] virtualPkgs = {
            "com.bly.dkplat",//多开分身本身的包名
//            "dkplugin.pke.nnp",//多开分身克隆应用的包名会随机变换
            "com.by.chaos",//chaos引擎
            "com.lbe.parallel",//平行空间
            "com.excelliance.dualaid",//双开助手
            "com.lody.virtual",//VirtualXposed，VirtualApp
            "com.qihoo.magic"//360分身大师
    };


    /**
     * 通过检测app私有目录，多开后的应用路径会包含多开软件的包名
     *
     * @param context
     * @return
     */
    public static boolean checkByPrivateFilePath(Context context) {
        String path = context.getFilesDir().getPath();
        for (String virtualPkg : virtualPkgs) {
            if (path.contains(virtualPkg)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断当前私有路径是否是标准路径
     *
     * @param context
     * @return
     */
    public static boolean pathCheck(Context context) {

        String filesDir = context.getFilesDir().getAbsolutePath();
        String packageName = context.getPackageName();

        String normalPath_one = "/data/data/" + packageName + "/files";
        String normalPath_two = "/data/user/0/" + packageName + "/files";

        if (!normalPath_one.equals(filesDir) && !normalPath_two.equals(filesDir)) {
            return true;
        }

        return false;

    }

    /**
     * 若 applist 存在两个当前包名则为多开
     *
     * @param context
     * @return
     * @deprecated 大部分多开软件已经绕过
     */
    public static boolean packageCheck(Context context) {

        try {
            if (context == null) {
                return false;
            }
            int count = 0;
            String packageName = context.getPackageName();
            PackageManager pm = context.getPackageManager();
            List<PackageInfo> pkgs = pm.getInstalledPackages(0);
            for (PackageInfo info : pkgs) {
                if (packageName.equals(info.packageName)) {
                    count++;
                }
            }
            return count > 1;
        } catch (Exception ignore) {
        }
        return false;
    }


    /**
     * 进程检测，若出现同一个 uid 下出现的进程名对应 /data/data/pkg 私有目录，超出 1 个则为多开
     * 需要排除当前进程名存在多个情况
     *
     * @return
     * @deprecated 当前方案在 6.0 以上机型不可用，因为只能获取当前 uid 进程列表
     */
    public static boolean processCheck() {

        if (Build.VERSION.SDK_INT > 23) {
            return false;
        }
        String filter = CommandUtils.getUidStrFormat();

        String result = CommandUtils.execute("ps");
        if (result == null || result.isEmpty()) {
            return false;
        }

        SLog.d(result);

        String[] lines = result.split("\n");
        if (lines == null || lines.length <= 0) {
            return false;
        }

        int exitDirCount = 0;

        for (String line : lines) {
            if (line.contains(filter)) {
                int pkgStartIndex = line.lastIndexOf(" ");
                String processName = line.substring(pkgStartIndex <= 0
                        ? 0 : pkgStartIndex + 1, line.length());
                File dataFile = new File(String.format("/data/data/%s",
                        processName, Locale.CHINA));
                if (dataFile.exists()) {
                    exitDirCount++;
                }
            }
        }

        return exitDirCount > 1;

    }

    /**
     * maps检测, 若 maps 文件包含多开包名则为多开环境
     *
     * @return
     * @deprecated 无法普适所有多开软件, 且部分软件 maps 不依赖当前路径下 so
     */
    public static boolean mapsCheck() {
        BufferedReader bufr = null;
        try {
            bufr = new BufferedReader(new FileReader("/proc/self/maps"));
            String line;
            while ((line = bufr.readLine()) != null) {
                for (String pkg : virtualPkgs) {
                    if (line.contains(pkg)) {
                        return true;
                    }
                }
            }
        } catch (Exception ignore) {
            //忽略异常
        } finally {
            if (bufr != null) {
                try {
                    bufr.close();
                } catch (IOException e) {
                    //忽略异常
                }
            }
        }
        return false;
    }


}
