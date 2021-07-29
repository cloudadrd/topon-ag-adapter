package com.business.support.ascribe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.business.support.adinfo.BSAdType;
import com.business.support.config.Const;
import com.business.support.utils.SLog;
import com.business.support.utils.ThreadPoolProxy;

import java.io.File;

class InstallStateReceiver extends BroadcastReceiver {


    private static final String TAG = "InstallStateReceiver";


    private static final String ACTION = "android.intent.action.PACKAGE_ADDED";

    private final InstallListener installListener;


    public InstallStateReceiver(InstallListener installListener) {
        this.installListener = installListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        SLog.e(TAG, "onReceive install complete");

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

            if (!action.equals(ACTION)) {
                return;
            }

            //通过播放过的广告app来命中当前已安装的app
            if (RewardTaskInfo.revealAdPackages.get(pkgName) != null) {
                BSAdType bsAdType = RewardTaskInfo.revealAdPackages.get(pkgName);
                RewardTaskInfo.taskInfo = new RewardTaskInfo(pkgName, bsAdType, 0, 0);
                RewardTaskInfo.taskInfo.infoState = 0;
                RewardTaskInfo.taskInfo.startTaskAppTime = 0;
                NativeDataManager.writeFileForTaskInfo(RewardTaskInfo.taskInfo);
                if (installListener != null) {
                    installListener.installedHit(pkgName, bsAdType);
                }
                RewardTaskInfo.revealAdPackages.remove(pkgName);
            } else {
                SLog.e(TAG, "匹配失败");
            }


            //以前通过下载目录判断安装app来源
//            ThreadPoolProxy.getInstance().execute(
//                    new HitRunnable(context.getApplicationContext(), pkgName, installListener));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    static class HitRunnable implements Runnable {

        private Context mContext;

        private String mPkgName;

        private InstallListener mListener;

        public HitRunnable(Context context, String pkgName, InstallListener installListener) {
            mContext = context;
            mPkgName = pkgName;
            mListener = installListener;
        }

        @Override
        public void run() {
            if (!pangleIsHit(mContext, mPkgName)) {
                return;
            }
//            Const.HANDLER.post(new Runnable() {
//                @Override
//                public void run() {
//                    if (mListener != null) {
//                        mListener.installedHit(mPkgName,);
//                        mPkgName = null;
//                        mContext = null;
//                        mListener = null;
//                    }
//                }
//            });
        }
    }

    //判断哪穿山甲安装包的命中
    public static boolean pangleIsHit(Context context, String pkgName) {
        File external_files_path = context.getExternalFilesDir(null);
        if (external_files_path == null) return false;

        File downloadFile = new File(external_files_path.getAbsolutePath() + "/Download");
        File[] downloadListFile = downloadFile.listFiles();
        if (downloadListFile == null || downloadListFile.length <= 0) return false;
        long tempLastModified = 0;
        int index = -1;

        //start 最新的文件提到最前
        for (int i = 0; i < downloadListFile.length; i++) {
            if (tempLastModified < downloadListFile[i].lastModified()) {
                tempLastModified = downloadListFile[i].lastModified();
                index = i;
            }
        }
        File tempFile = downloadListFile[0];

        if (index != -1) {
            downloadListFile[0] = downloadListFile[index];
            downloadListFile[index] = tempFile;
        }
        //end

        for (File childFile : downloadListFile) {
            if (!childFile.isFile() || childFile.getName().lastIndexOf("apk") == -1) {
                continue;
            }
            Log.i(TAG, "is apk file ,filePath=" + childFile.getAbsolutePath());
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(childFile.getAbsolutePath(),
                    PackageManager.GET_ACTIVITIES);
            if (info != null) {
                String packageName = info.packageName;
                if (pkgName.equals(packageName)) {
                    Log.i(TAG, "hit ok,filePath=" + childFile.getAbsolutePath());
                    return true;
                }
            }

        }

        return false;

    }

    private static boolean isReceiver = false;

    static void registerReceiver(Context context, InstallListener installListener) {
        if (isReceiver) {
            return;
        }
        isReceiver = true;

        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION);
            intentFilter.addDataScheme("package");
            context.registerReceiver(new InstallStateReceiver(installListener), intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
