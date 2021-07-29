package com.business.support.ascribe;

import com.business.support.adinfo.BSAdType;
import com.business.support.utils.ContextHolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class NativeDataManager {

    public static File getRootDirFile() {
        return new File(ContextHolder.getGlobalAppContext().getCacheDir(), "ascribe");
    }

    public static File getTaskInfoFile() {
        return new File(getRootDirFile(), "taskinfo");
    }

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

    public static void removeFile() {
        File taskInfoFile = getTaskInfoFile();
        if (taskInfoFile.exists()) {
            taskInfoFile.delete();
        }
    }

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
