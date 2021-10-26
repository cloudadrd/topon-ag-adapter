package com.business.support.ascribe;

import android.content.Context;
import android.text.TextUtils;

import com.business.support.adinfo.BSAdType;
import com.business.support.utils.ContextHolder;
import com.business.support.utils.PreferenceTools;
import com.business.support.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class NativeDataManager {

    public static final String PROCESS_NAME = Utils.getProcessName(ContextHolder.getGlobalAppContext(), android.os.Process.myPid());

    public static final String PREF_REWARD_TASK_FILE_NAME = PROCESS_NAME + "_reward_task";

    @Deprecated
    public static File getRootDirFile() {
        return new File(ContextHolder.getGlobalAppContext().getCacheDir(), "ascribe");
    }

    @Deprecated
    public static File getTaskInfoFile() {
        return new File(getRootDirFile(), "taskinfo");
    }

    @Deprecated
    public static boolean writeFileForTaskInfo(RewardTaskInfo taskInfo) {
        File taskInfoFile = getTaskInfoFile();

        if (!taskInfoFile.exists()) {
            try {
                if (taskInfoFile.getParentFile() != null &&
                        !taskInfoFile.getParentFile().exists()) {
                    taskInfoFile.getParentFile().mkdirs();
                }
                taskInfoFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        FileOutputStream fileInputStream = null;
        char split = '$';
        try {
            String data = taskInfo.currentInstallPkg + split +
                    taskInfo.bsAdType.getName() + split + taskInfo.infoState + split + taskInfo.startTaskAppTime;
            fileInputStream = new FileOutputStream(taskInfoFile);
            fileInputStream.write(data.getBytes(StandardCharsets.UTF_8));
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

    public static void writeFileForTaskInfo2(RewardTaskInfo taskInfo) {
        Context context = ContextHolder.getGlobalAppContext();
        char split = '$';
        StringBuilder sbConcat = new StringBuilder();
        sbConcat.append(taskInfo.currentInstallPkg)
                .append(split)
                .append(taskInfo.bsAdType.getName())
                .append(split)
                .append(taskInfo.sceneId)
                .append(split)
                .append(taskInfo.appName)
                .append(split)
                .append(taskInfo.infoState)
                .append(split)
                .append(taskInfo.startTaskAppTime);

        PreferenceTools.persistString(context, PREF_REWARD_TASK_FILE_NAME, taskInfo.sceneId, sbConcat.toString());
    }

    public static void removeForSceneId(String sceneId) {
        Context context = ContextHolder.getGlobalAppContext();
        PreferenceTools.removeKey(context, PREF_REWARD_TASK_FILE_NAME, sceneId);
    }

    @Deprecated
    public static void removeFile() {
        File taskInfoFile = getTaskInfoFile();
        if (taskInfoFile.exists()) {
            taskInfoFile.delete();
        }
    }

    public static RewardTaskInfo getTaskInfoForSceneId(String sceneId) {
        Context context = ContextHolder.getGlobalAppContext();
        String data = PreferenceTools.getString(context, PREF_REWARD_TASK_FILE_NAME, sceneId, "");
        if (TextUtils.isEmpty(data)) return null;
        String[] splits = data.split("\\$");
        if (splits.length != 6) return null;
        return new RewardTaskInfo(splits[0], BSAdType.get(splits[1]), splits[2], splits[3], Integer.parseInt(splits[4]), Long.parseLong(splits[5]));
    }

    public static RewardTaskInfo[] getTaskInfoAll() {
        Context context = ContextHolder.getGlobalAppContext();
        Set<String> strs = PreferenceTools.getAllKeys(context, PREF_REWARD_TASK_FILE_NAME);
        Iterator<String> iterable = strs.iterator();
        RewardTaskInfo[] arrays = new RewardTaskInfo[strs.size()];
        int index = 0;
        while (iterable.hasNext()) {
            String data = iterable.next();
            String[] splits = data.split("\\$");
            if (splits.length != 5) return null;
            RewardTaskInfo taskInfo = new RewardTaskInfo(splits[0], BSAdType.get(splits[1]), splits[2], Integer.parseInt(splits[3]), Long.parseLong(splits[4]));
            arrays[index] = taskInfo;
            index++;
        }
        return arrays;
    }

//    public static boolean isExistsForPkg(String packageName) {
//        RewardTaskInfo[] rewardTaskInfos = getTaskInfoAll();
//        if (rewardTaskInfos == null) return false;
//        for (RewardTaskInfo taskInfo : rewardTaskInfos) {
//
//        }
//    }

    @Deprecated
    public static RewardTaskInfo getTaskInfo() {
        File taskInfoFile = getTaskInfoFile();
        if (!taskInfoFile.exists() || !taskInfoFile.isFile()) {
            return null;
        }
        FileInputStream inputStream = null;
        try {
            byte[] bytes = new byte[256];
            inputStream = new FileInputStream(taskInfoFile);
            int result = inputStream.read(bytes);
            if (result == -1) return null;
            String data = new String(bytes, 0, result);
            String[] splits = data.split("\\$");
            if (splits.length != 4) return null;

            return new RewardTaskInfo(splits[0], BSAdType.get(splits[1]), Integer.parseInt(splits[2]), Long.parseLong(splits[3]));
        } catch (Throwable e) {
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
        return null;
    }
}
