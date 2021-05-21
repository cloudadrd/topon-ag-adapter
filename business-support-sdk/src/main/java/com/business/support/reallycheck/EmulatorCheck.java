package com.business.support.reallycheck;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import static android.Manifest.permission.BLUETOOTH;
import static android.Manifest.permission.READ_PHONE_STATE;

public class EmulatorCheck {


    private static final String TAG = "EmulatorCheck";

    public static ResultData validCheck(Context context) {
//        if (notHasBlueTooth()
//                || notHasLightSensorManager(context)
//                || isFeatures()
//                || checkIsNotRealPhone()
//                || checkPipes()
//                || checkEmulatorBuild7()
//                || isEmulatorFromAbi()
//                || checkDeviceIDS5(context)
//                || checkQEmuDriverFile2()
//                || checkEmulatorFiles3()
//                || checkOperatorNameAndroid8(context)
//        ) {
//            Log.e(TAG, "检查到您的设备违规,将限制您的所有功能使用!");
//            return false;
//        }

        StringBuilder stringBuilder = new StringBuilder();

        if (notHasBlueTooth(context)) {
//            Log.e(TAG, "notHasBlueTooth");
            stringBuilder.append("1");
        }
//        if (notHasLightSensorManager(context)) {
////            Log.e(TAG, "notHasLightSensorManager");
//            stringBuilder.append(",2");
//        }
        if (isFeatures()) {
//            Log.e(TAG, "isFeatures");
            stringBuilder.append(",3");
        }

        if (checkIsNotRealPhone()) {
//            Log.e(TAG, "checkIsNotRealPhone");
            stringBuilder.append(",4");
        }

        if (checkPipes()) {
//            Log.e(TAG, "checkPipes");
            stringBuilder.append(",5");
        }

        if (isEmulatorFromAbi()) {
//            Log.e(TAG, "isEmulatorFromAbi");
            stringBuilder.append(",7");
        }

        if (checkDeviceIDS5(context)) {
//            Log.e(TAG, "checkDeviceIDS5");
            stringBuilder.append(",8");
        }

        if (checkQEmuDriverFile2()) {
//            Log.e(TAG, "checkQEmuDriverFile2");
            stringBuilder.append(",9");
        }

        if (checkEmulatorFiles3()) {
//            Log.e(TAG, "checkEmulatorFiles3");
            stringBuilder.append(",10");
        }

        if (checkOperatorNameAndroid8(context)) {
//            Log.e(TAG, "checkOperatorNameAndroid8");
            stringBuilder.append(",11");
        }

        return new ResultData(!TextUtils.isEmpty(stringBuilder), stringBuilder.toString());

    }


    //用途:判断蓝牙是否有效来判断是否为模拟器
    public static boolean notHasBlueTooth(Context context) {
        if (!isPermissionGranted(context, BLUETOOTH)) {
            return false;
        }
        try {
            BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
            if (ba == null) {
                return true;
            } else {
                // 如果有蓝牙不一定是有效的。获取蓝牙名称，若为null 则默认为模拟器
                String name = ba.getName();
                if (TextUtils.isEmpty(name)) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
    }

    //依据是否存在光传感器来判断是否为模拟器
    public static Boolean notHasLightSensorManager(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor8 = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT); //光
        if (null == sensor8) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isFeatures() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("Android")
                || Build.FINGERPRINT.toLowerCase().contains("vbox")
                || Build.FINGERPRINT.toLowerCase().contains("test-keys")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }

    //用途:根据CPU是否为电脑来判断是否为模拟器
    public static boolean checkIsNotRealPhone() {
        String cpuInfo = readCpuInfo();
        if ((cpuInfo.contains("intel") || cpuInfo.contains("amd"))) {
            return true;
        }
        return false;
    }

    private static boolean isEmulatorFromAbi() {

        String abi = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            abi = Arrays.toString(Build.SUPPORTED_ABIS);
        } else {
            abi = Build.CPU_ABI + Build.CPU_ABI2;
        }

        return !TextUtils.isEmpty(abi) && abi.contains("x86");
    }

    private static String[] known_device_ids = {"000000000000000" // 默认ID
    };
    private static String[] known_imsi_ids = {"310260000000000" // 默认的 imsi id
    };

    /**
     * 方法5
     */
    public static Boolean checkDeviceIDS5(Context context) {
        String device_ids = getIMEI(context);
        if (!TextUtils.isEmpty(device_ids)) {
            for (String know_deviceid : known_device_ids) {
                if (know_deviceid.equalsIgnoreCase(device_ids)) {
//                    Log.v(TAG, "Result: Find ids: 000000000000000!");
                    return true;
                }
            }
        }
//        Log.v(TAG, "Result: Not Find ids: 000000000000000!");
        return false;
    }

    /**
     * @return 获取手机IMEI
     */
    @SuppressLint({"HardwareIds", "MissingPermission"})
    public static String getIMEI(final Context context) {
        if (!isPermissionGranted(context, READ_PHONE_STATE)) {
            return null;
        }
        String imei = "";
        try {
            TelephonyManager mTelephony =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephony == null) return null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (mTelephony.getPhoneCount() == 2) {
                        imei = mTelephony.getImei(0);
                    } else {
                        imei = mTelephony.getImei();
                    }
                } else {
                    if (mTelephony.getPhoneCount() == 2) {
                        imei = mTelephony.getDeviceId(0);
                    } else {
                        imei = mTelephony.getDeviceId();
                    }
                }
            } else {
                imei = mTelephony.getDeviceId();
            }
        } catch (Exception e) {
//            Log.e(TAG, "imei Error=" + e.getMessage());
        }
        return imei;
    }

    private static String[] known_files = {
            "/system/lib/libc_malloc_debug_qemu.so",
            "/sys/qemu_trace",
            "/system/bin/qemu-props"
    };

    private static String[] known_qemu_drivers = {"goldfish"};

    /**
     * 方法2
     */
    public static Boolean checkQEmuDriverFile2() {
        File driver_file = new File("/proc/tty/drivers");
        if (driver_file.exists() && driver_file.canRead()) {
//            byte[] data = new byte[(int) driver_file.length()];
            byte[] data = new byte[1000];
            try {
                InputStream inStream = new FileInputStream(driver_file);
                inStream.read(data);
                inStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String driver_data = new String(data);
            for (String known_qemu_driver : known_qemu_drivers) {
                if (driver_data.contains(known_qemu_driver)) {
//                    Log.v(TAG, "Result: Find known_qemu_drivers!");
                    return true;
                }
            }
        }
//        Log.v(TAG, "Result: Not Find known_qemu_drivers!");
        return false;
    }

    /**
     * 方法3
     */
    public static Boolean checkEmulatorFiles3() {
        for (String file_name : known_files) {
            File qemu_file = new File(file_name);
            if (qemu_file.exists()) {
//                Log.v(TAG, "Result: Find Emulator Files!");
                return true;
            }
        }
//        Log.v(TAG, "Result: Not Find Emulator Files!");
        return false;
    }


//    /**
//     * 方法6
//     */
//    public static Boolean checkImsiIDS6(Context context) {
//        if (!isPermissionGranted(context, READ_PHONE_STATE) ) {
//            return false;
//        }
//        TelephonyManager telephonyManager = (TelephonyManager)
//                context.getSystemService(Context.TELEPHONY_SERVICE);
//
//        @SuppressLint("HardwareIds") String imsi_ids = telephonyManager.getSubscriberId();
//        if (!TextUtils.isEmpty(imsi_ids)) {
//            for (String know_imsi : known_imsi_ids) {
//                if (know_imsi.equalsIgnoreCase(imsi_ids)) {
////                    Log.v(TAG, "Result: Find imsi ids: 310260000000000!");
//                    return true;
//                }
//            }
//        }
////        Log.v(TAG, "Result: Not Find imsi ids: 310260000000000!");
//        return false;
//    }

    public static boolean isPermissionGranted(final Context context, final String permission) {
        if (null == context || TextUtils.isEmpty(permission)) {
//            Log.e(TAG, String.format("[msg=check android permission][parameter is null or empty][permission=%s]", permission));
            return false;
        }

        //之前的方法,对版本有要求,必须是23以上
        return context.checkPermission(permission, android.os.Process.myPid(), android.os.Process.myUid()) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 方法8
     */
    public static boolean checkOperatorNameAndroid8(Context context) {
        String szOperatorName = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkOperatorName();
        return !TextUtils.isEmpty(szOperatorName) && "android".equals(szOperatorName.toLowerCase());
    }

    /*
     *作者:赵星海
     *时间:2019/2/21 17:58
     *用途:根据CPU是否为电脑来判断是否为模拟器(子方法)
     *返回:String
     */
    public static String readCpuInfo() {
        String result = "";
        try {
            String[] args = {"/system/bin/cat", "/proc/cpuinfo"};
            ProcessBuilder cmd = new ProcessBuilder(args);

            Process process = cmd.start();
            StringBuffer sb = new StringBuffer();
            String readLine = "";
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            while ((readLine = responseReader.readLine()) != null) {
                sb.append(readLine);
            }
            responseReader.close();
            result = sb.toString().toLowerCase();
        } catch (IOException ex) {
        }
        return result;
    }

    //用途:检测模拟器的特有文件
    private static String[] known_pipes = {"/dev/socket/qemud", "/dev/qemu_pipe"};

    public static boolean checkPipes() {
        for (int i = 0; i < known_pipes.length; i++) {
            String pipes = known_pipes[i];
            File qemu_socket = new File(pipes);
            if (qemu_socket.exists()) {
//                Log.v("Result:", "Find pipes!");
                return true;
            }
        }
//        Log.i("Result:", "Not Find pipes!");
        return false;
    }

}
