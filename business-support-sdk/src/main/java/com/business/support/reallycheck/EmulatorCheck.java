package com.business.support.reallycheck;

import static android.Manifest.permission.BLUETOOTH;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.content.Context.SENSOR_SERVICE;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.business.support.utils.CommandUtils;
import com.business.support.utils.ReflectUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Arrays;

public class EmulatorCheck {


    private static final String TAG = "EmulatorCheck";

    public static ResultData validCheck(Context context) {

        StringBuilder stringBuilder = new StringBuilder();

        if (notHasBlueTooth(context)) {
//            Log.e(TAG, "notHasBlueTooth");
            stringBuilder.append("1");
        }

        if (buildCheck()) {
//            Log.e(TAG, "buildCheck");
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

        //////////START///
        if (qemuCheck(context)) {
//            Log.e(TAG, "checkOperatorNameAndroid8");
            stringBuilder.append(",12");
        }

        if (getUserAppNumber()) {
//            Log.e(TAG, "checkOperatorNameAndroid8");
            stringBuilder.append(",13");
        }

        if (!supportCamera(context)) {
//            Log.e(TAG, "checkOperatorNameAndroid8");
            stringBuilder.append(",14");
        }

        if (checkFeaturesByCgroup()) {
//            Log.e(TAG, "checkOperatorNameAndroid8");
            stringBuilder.append(",15");
        }

        if (checkFeaturesByHardware()) {
//            Log.e(TAG, "checkOperatorNameAndroid8");
            stringBuilder.append(",16");
        }

        if (checkFeaturesByFlavor()) {
//            Log.e(TAG, "checkOperatorNameAndroid8");
            stringBuilder.append(",17");
        }

        if (checkFeaturesByModel()) {
//            Log.e(TAG, "checkOperatorNameAndroid8");
            stringBuilder.append(",18");
        }

        if (checkFeaturesByManufacturer()) {
//            Log.e(TAG, "checkOperatorNameAndroid8");
            stringBuilder.append(",19");
        }

        if (checkFeaturesByBoard()) {
//            Log.e(TAG, "checkOperatorNameAndroid8");
            stringBuilder.append(",20");
        }

        if (checkFeaturesByPlatform()) {
//            Log.e(TAG, "checkOperatorNameAndroid8");
            stringBuilder.append(",21");
        }

        if (checkFeaturesByBaseBand()) {
//            Log.e(TAG, "checkOperatorNameAndroid8");
            stringBuilder.append(",22");
        }

        if (getSensorNumber(context)) {
//            Log.e(TAG, "checkOperatorNameAndroid8");
            stringBuilder.append(",23");
        }

        if (checkSystemProperty()) {
//            Log.e(TAG, "checkOperatorNameAndroid8");
            stringBuilder.append(",24");
        }

        return new ResultData(!TextUtils.isEmpty(stringBuilder), stringBuilder.toString(), 30);

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


    /**
     * Build 文件检测
     */
    public static boolean buildCheck() {

        if (Build.PRODUCT.contains("sdk") ||
                Build.PRODUCT.contains("sdk_x86") ||
                Build.PRODUCT.contains("sdk_google") ||
                "google_sdk".equals(Build.PRODUCT) ||
                Build.PRODUCT.contains("Andy") ||
                Build.PRODUCT.contains("Droid4X") ||
                Build.PRODUCT.contains("nox") ||
                Build.PRODUCT.contains("vbox86p") ||
                Build.PRODUCT.contains("aries")) {
            return true;
        }
        if (Build.MANUFACTURER.equals("Genymotion") ||
                Build.MANUFACTURER.contains("Andy") ||
                Build.MANUFACTURER.contains("nox") ||
                Build.MANUFACTURER.contains("TiantianVM")) {
            return true;
        }
        if (Build.BRAND.contains("Andy") ||
                (Build.BRAND.startsWith("generic")
                        && Build.DEVICE.startsWith("generic"))) {
            return true;
        }
        if (Build.DEVICE.contains("Andy") ||
                Build.DEVICE.contains("Droid4X") ||
                Build.DEVICE.contains("nox") ||
                Build.DEVICE.contains("vbox86p") ||
                Build.DEVICE.contains("aries")) {
            return true;
        }
        if (Build.MODEL.contains("Emulator") ||
                Build.MODEL.equals("google_sdk") ||
                Build.MODEL.contains("Droid4X") ||
                Build.MODEL.contains("TiantianVM") ||
                Build.MODEL.contains("Andy") ||
                Build.MODEL.equals("Android SDK built for x86_64") ||
                Build.MODEL.equals("Android SDK built for x86")) {
            return true;
        }
        if (Build.HARDWARE.equals("vbox86") ||
                Build.HARDWARE.contains("nox") ||
                Build.HARDWARE.contains("ttVM_x86")) {
            return true;
        }
        if (Build.FINGERPRINT.contains("generic/sdk/generic") ||
                Build.FINGERPRINT.startsWith("generic") ||
                Build.FINGERPRINT.startsWith("Android") ||
                Build.FINGERPRINT.toLowerCase().contains("vbox") ||
                Build.FINGERPRINT.toLowerCase().contains("test-keys") ||
                Build.FINGERPRINT.contains("generic_x86/sdk_x86/generic_x86") ||
                Build.FINGERPRINT.contains("Andy") ||
                Build.FINGERPRINT.contains("ttVM_Hdragon") ||
                Build.FINGERPRINT.contains("generic/google_sdk/generic") ||
                Build.FINGERPRINT.contains("vbox86p") ||
                Build.FINGERPRINT.contains("generic/vbox86p/vbox86p")) {
            return true;
        }

        return false;
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

    private static final String[] known_device_ids = {"000000000000000" // 默认ID
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

    public static boolean qemuCheck(Context context) {
        String qemu = getProp(context, "ro.kernel.qemu");
        return "1".equals(qemu);
    }

    /**
     * 获取已安装第三方应用数量
     */
    private static boolean getUserAppNumber() {
        String userApps = CommandUtils.execute("pm list package -3");
        return getUserAppNum(userApps) <= 5;
    }

    /**
     * 是否支持相机
     */
    private static boolean supportCamera(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /**
     * 特征参数-进程组信息,一般都是有内容的，文件格式如下：
     * 6:memory:/
     * 5:freezer:/
     * 4:cpuset:/
     * 3:cpuacct:/uid_0/pid_22029
     * 2:cpu:/
     * 1:blkio:/
     * 0::/
     */
    private static boolean checkFeaturesByCgroup() {
        String filter = CommandUtils.execute("cat /proc/self/cgroup");
        return TextUtils.isEmpty(filter);
    }

    /**
     * 特征参数-硬件名称
     */
    private static boolean checkFeaturesByHardware() {
        String hardware = getProperty("ro.hardware");
        if (null == hardware) {
            return false;
        }
        int result;
        String tempValue = hardware.toLowerCase();
        switch (tempValue) {
            case "ttvm"://天天模拟器
            case "nox"://夜神模拟器
            case "cancro"://网易MUMU模拟器
            case "intel"://逍遥模拟器
            case "vbox":
            case "vbox86"://腾讯手游助手
            case "android_x86"://雷电模拟器
                return true;
            default:
                return false;
        }
    }

    /**
     * 特征参数-渠道
     */
    private static boolean checkFeaturesByFlavor() {
        String flavor = getProperty("ro.build.flavor");
        if (null == flavor) {
            return false;
        }
        String tempValue = flavor.toLowerCase();
        if (tempValue.contains("vbox")) {
            return true;
        } else return tempValue.contains("sdk_gphone");
    }

    /**
     * 特征参数-设备型号
     */
    private static boolean checkFeaturesByModel() {
        String model = getProperty("ro.product.model");
        if (null == model) {
            return false;
        }
        String tempValue = model.toLowerCase();
        if (tempValue.contains("google_sdk")) {
            return true;
        } else if (tempValue.contains("emulator")) {
            return true;
        } else return tempValue.contains("android sdk built for x86");
    }

    /**
     * 特征参数-硬件制造商
     */
    private static boolean checkFeaturesByManufacturer() {
        String manufacturer = getProperty("ro.product.manufacturer");
        if (null == manufacturer) {
            return false;
        }
        String tempValue = manufacturer.toLowerCase();
        //网易MUMU模拟器
        if (tempValue.contains("genymotion")) {
            return true;
        } else return tempValue.contains("netease");
    }

    /**
     * 特征参数-主板名称
     */
    private static boolean checkFeaturesByBoard() {
        String board = getProperty("ro.product.board");
        if (null == board) {
            return false;
        }
        String tempValue = board.toLowerCase();
        if (tempValue.contains("android")) {
            return true;
        } else return tempValue.contains("goldfish");
    }

    /**
     * 特征参数-主板平台
     */
    private static boolean checkFeaturesByPlatform() {
        String platform = getProperty("ro.board.platform");
        if (null == platform) {
            return false;
        }
        String tempValue = platform.toLowerCase();
        return tempValue.contains("android");
    }

    /**
     * 特征参数-基带信息
     */
    private static boolean checkFeaturesByBaseBand() {
        String baseBandVersion = getProperty("gsm.version.baseband");
        if (null == baseBandVersion) {
            return false;
        }
        return baseBandVersion.contains("1.0.0.0");
    }

    /**
     * 获取传感器数量
     */
    private static boolean getSensorNumber(Context context) {
        SensorManager sm = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        return sm.getSensorList(Sensor.TYPE_ALL).size() <= 7;
    }

    /**
     * 检测系统属性，非常可靠
     * <p>
     * 1. data.unique.br_list.arch 为 i686
     * 2. data.unique.br_list.har 为 intel 或 vbox86 或包含 x86
     * 3. data.unique.br_list.a 为 x86
     * 4. data.unique.br_list.a_list 包含 x86
     * 5. sound 信息为 I82801AAICH
     * 6. su_v 为 16 com.android.settings
     *
     * @return
     */
    public static boolean checkSystemProperty() {

        String hardware = ReflectUtils.getProperty("ro.hardware");
        if (hardware.contains("intel")
                || hardware.contains("vbox86")
                || hardware.contains("vbox")
                || hardware.contains("ttvm")
                || hardware.contains("cancro")
                || hardware.contains("nox")
                || hardware.contains("x86")) {
            return true;
        }
        String abi = ReflectUtils.getProperty("ro.product.cpu.abi");
        if (abi.contains("x86")) {
            return true;
        }
        String abilist = ReflectUtils.getProperty("ro.product.cpu.abilist");
        if (abilist.contains("x86")) {
            return true;
        }
        String arch = CommandUtils.execute("uname -m");
        if (arch.contains("i686")) {
            return true;
        }
        String su = CommandUtils.execute("su -v");
        if (TextUtils.equals("16 com.android.settings", su)) {
            return true;
        }
        String sound = CommandUtils.execute("cat /proc/asound/card0/id");
        if (TextUtils.equals("I82801AAICH", sound)) {
            return true;
        }

        return false;
    }


    private static int getUserAppNum(String userApps) {
        if (TextUtils.isEmpty(userApps)) {
            return 0;
        }
        String[] result = userApps.split("package:");
        return result.length;
    }

    private static String getProp(Context context, String property) {
        try {
            ClassLoader cl = context.getClassLoader();
            Class<?> SystemProperties = cl.loadClass("android.os.SystemProperties");
            Method method = SystemProperties.getMethod("get", String.class);
            Object[] params = new Object[1];
            params[0] = property;
            return (String) method.invoke(SystemProperties, params);
        } catch (Exception e) {
            return null;
        }
    }

    private static String getProperty(String propName) {
        String property = ReflectUtils.getProperty(propName);
        return TextUtils.isEmpty(property) ? null : property;
    }


}
