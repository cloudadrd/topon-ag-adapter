package com.business.support.webview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AppInstallReceiver extends BroadcastReceiver {


    private static final String TAG = "AppInstallReceiver";


    public static void addInstallCallback(InstallCallback installCallback) {
        installCallbacks.add(installCallback);
    }

    public static void removeInstallCallback(InstallCallback installCallback) {
        installCallbacks.remove(installCallback);
    }


    private static final List<InstallCallback> installCallbacks = new LinkedList<>();

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
                for (InstallCallback callback : installCallbacks) {
                    callback.success(pkgName);
                }
                DownloadManager.APK_LIST.remove(pkgName);
                DownloadManager.deleteFile(pkgName);
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
