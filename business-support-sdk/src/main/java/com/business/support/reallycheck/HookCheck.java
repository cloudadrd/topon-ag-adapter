package com.business.support.reallycheck;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.io.File;
import java.util.List;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class HookCheck {


    private static final String TAG = "HookCheck";


    public static ResultData validCheck(Context context) {
        StringBuilder stringBuilder = new StringBuilder();

        if (pkgCheck(context)) {
//            Log.e(TAG, "检测核心Hook文件存在");
            stringBuilder.append("1");
        }

        if (packageCheck(context)) {
//            Log.e(TAG, "检查HOOK包名存在");
            stringBuilder.append(",2");
        }
        if (exceptionCheck()) {
//            Log.e(TAG, "检测到调用栈中的可疑方法");
            stringBuilder.append(",3");
        }

        if (classCheck()) {
//            Log.e(TAG, "尝试加载 Xposed 类成功");
            stringBuilder.append(",4");
        }

        return new ResultData(!TextUtils.isEmpty(stringBuilder), stringBuilder.toString());
    }

    /**
     * 检查核心文件，若存在则为危险环境。不一定正在被 hook，但是有风险
     *
     * @return
     */
    @SuppressLint("SdCardPath")
    public static boolean pkgCheck(Context context) {
        if (!isPermissionGranted(context, WRITE_EXTERNAL_STORAGE)) {
            return false;
        }
        return exists("/data/data/de.robv.android.xposed.installer")
                || exists("/data/data/com.saurik.substrate")
                || exists("/data/local/tmp/frida-server");
    }

    /**
     * 检查包名是否存在
     *
     * @param context
     * @return
     */
    public static boolean packageCheck(Context context) {
        PackageManager packageManager = context.getApplicationContext().getPackageManager();
        List<ApplicationInfo> applicationInfoList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo item : applicationInfoList) {
            if ("de.robv.android.xposed.installer".equals(item.packageName)) {
//                Log.i(TAG, "Xposeded fonund on device");
                return true;
            }
            if ("com.saurik.substrate".equals(item.packageName)) {
//                Log.i(TAG, "CydiaSubstrate fonund on device");
                return true;
            }
        }
        return false;
    }

    public static boolean exists(String path) {
        File file = new File(path);
        return file.exists();
    }

    /**
     * 检测调用栈中的可疑方法
     */
    public static boolean exceptionCheck() {
        try {
            throw new Exception("Deteck hook");
        } catch (Exception e) {
            int zygoteInitCallCount = 0;
            for (StackTraceElement item : e.getStackTrace()) {
                // 检测"com.android.internal.os.ZygoteInit"是否出现两次，如果出现两次，则表明Substrate框架已经安装
                if ("com.android.internal.os.ZygoteInit".equals(item.getClassName())) {
                    zygoteInitCallCount++;
                    if (zygoteInitCallCount == 2) {
//                        Log.i(TAG, "Substrate is active on the device.");
                        return true;
                    }
                }
                if ("com.saurik.substrate.MS$2".equals(item.getClassName()) && "invoke".equals(item.getMethodName())) {
//                    Log.i(TAG, "A method on the stack trace has been hooked using Substrate.");
                    return true;
                }
                if ("de.robv.android.xposed.XposedBridge".equals(item.getClassName())
                        && "main".equals(item.getMethodName())) {
//                    Log.i(TAG, "Xposed is active on the device.");
                    return true;
                }
                if ("de.robv.android.xposed.XposedBridge".equals(item.getClassName())
                        && "handleHookedMethod".equals(item.getMethodName())) {
//                    Log.i(TAG, "A method on the stack trace has been hooked using Xposed.");
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 尝试加载 Xposed 类
     *
     * @return
     */
    public static boolean classCheck() {
        try {
            Class.forName("de.robv.android.xposed.XC_MethodHook");
            return true;
        } catch (Exception e) {
        }
        try {
            Class.forName("de.robv.android.xposed.XposedHelpers");
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public static boolean isPermissionGranted(final Context context, final String permission) {
        if (null == context || TextUtils.isEmpty(permission)) {
//            Log.e(TAG, String.format("[msg=check android permission][parameter is null or empty][permission=%s]", permission));
            return false;
        }

        //之前的方法,对版本有要求,必须是23以上
        return context.checkPermission(permission, android.os.Process.myPid(), android.os.Process.myUid()) == PackageManager.PERMISSION_GRANTED;
    }
}
