package com.business.support.webview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.util.Log;

public class AppInstallReceiver extends BroadcastReceiver {


    private static final String TAG = "AppInstallReceiver";


    public static void setInstallCallback(InstallCallback installCallback) {
        AppInstallReceiver.installCallback = installCallback;
    }

    private static InstallCallback installCallback;

    public interface InstallCallback {

        void success(String pkg);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        Uri uri = intent.getData();
        if (uri == null) {
            return;
        }
        try {
            String pkgName = uri.getSchemeSpecificPart();

            if (action.equals(Intent.ACTION_PACKAGE_ADDED)) {
                Log.d(TAG, "installed pkg -> " + pkgName);
                if (installCallback != null) {
                    installCallback.success(pkgName);
                }
                ApkDownloadManager.APK_LIST.remove(pkgName);
                ApkDownloadManager.deleteFile(pkgName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static boolean isReceiver = false;

    public static void registerReceiver(Context context) {
        if (isReceiver) {
            return;
        }
        isReceiver = true;

        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
            intentFilter.addDataScheme("package");
            context.registerReceiver(new AppInstallReceiver(), intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
