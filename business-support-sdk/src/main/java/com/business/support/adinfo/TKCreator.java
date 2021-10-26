package com.business.support.adinfo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.os.StatFs;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoNr;
import android.telephony.CellInfoTdscdma;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrength;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthTdscdma;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.business.support.config.Const;
import com.business.support.http.HttpRequester;
import com.business.support.utils.ContextHolder;
import com.business.support.utils.MDIDHandler;
import com.business.support.utils.SLog;
import com.business.support.utils.ShellUtils;
import com.business.support.utils.ThreadPoolProxy;
import com.business.support.utils.Utils;
import com.ease.utility.utils.aes.RijindaelUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by jiantao.tu on 2020/8/24.
 */
public class TKCreator {


    private final static String TAG = "TKCreator";

    private static AtomicBoolean isFirst = new AtomicBoolean(false);

    private static String userAgent = null;
    private static String mAppid = null;
    private static String tarPath = "";

    private static SensorNumbers sensorNumbers = new SensorNumbers();

    public static void setTarPath(String tar) {
        tarPath = tar;
    }

    public static void send(final Context context, String appid) {
        mAppid = appid;
        if (TextUtils.isEmpty(userAgent)) {
            userAgent = Utils.getUserAgentStr(context, false);
        }
        ThreadPoolProxy.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isFirst.compareAndSet(false, true)) {
                        sensorNumbers.registerSensorNumbers(context);
                        Thread.sleep(3000);
                        String result = getData(context);
                        String aesStr = RijindaelUtils.encrypt(result, "osdj30LOawDK83kFOksw7HiKKk9D3CR$");
                        if (aesStr != null) {
//                            String base64Str = Base64.encodeToString(aesStr.getBytes(), Base64.NO_WRAP);
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("type", 1);
                            jsonObject.put("data", aesStr);
                            request(jsonObject.toString());
                        }
                    }
                } catch (Throwable e) {
                    Log.d(TAG, e.getMessage());
                }
            }
        });
    }

    private static void request(String json) {
        Context context = ContextHolder.getGlobalAppContext();
        HttpRequester.requestByPost(context, Const.TK_CREATOR_URL, json, new HttpRequester.Listener() {

            @Override
            public void onSuccess(byte[] data, String url) {
                try {
                    String str = new String(data);
                    JSONObject jsonObject = new JSONObject(str);
                    if (jsonObject.optInt("code") != 200) {
                        return;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(String msg, String url) {
                SLog.e(TAG + " requestQuery-onFailure msg=" + msg);
            }
        });
    }


    static class SensorNumbers {
        private SensorManager sm;

        Sensor mysensor1; // ACCELEROMETER
        Sensor mysensor2; // MAGNETIC_FIELD
        Sensor mysensor3; // GYROSCOPE

        MySensorListener listener1;
        MySensorListener listener2;
        MySensorListener listener3;

        private float[][] ACCELEROMETER_VALUES = new float[2][];
        private float[][] MAGNETIC_FIELD_VALUES = new float[2][];
        private float[][] GYROSCOPE_VALUES = new float[2][];

        public void registerSensorNumbers(Context context) {
            sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

            mysensor1 = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            listener1 = new MySensorListener();
            if (mysensor1 != null) {
                sm.registerListener(listener1, mysensor1, SensorManager.SENSOR_DELAY_NORMAL);
            }

            mysensor2 = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            listener2 = new MySensorListener();
            if (mysensor2 != null) {
                sm.registerListener(listener2, mysensor2, SensorManager.SENSOR_DELAY_NORMAL);
            }

            mysensor3 = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            listener3 = new MySensorListener();
            if (mysensor3 != null) {
                sm.registerListener(listener3, mysensor3, SensorManager.SENSOR_DELAY_NORMAL);
            }

        }


        class MySensorListener implements SensorEventListener {

            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                int type = sensorEvent.sensor.getType();
                float[][] temps = null;

                if (type == Sensor.TYPE_ACCELEROMETER) {
                    temps = ACCELEROMETER_VALUES;

                } else if (type == Sensor.TYPE_MAGNETIC_FIELD) {
                    temps = MAGNETIC_FIELD_VALUES;

                } else if (type == Sensor.TYPE_GYROSCOPE) {
                    temps = GYROSCOPE_VALUES;

                }
                if (temps == null) return;
                if (temps[0] == null) {
                    temps[0] = Arrays.copyOf(sensorEvent.values, sensorEvent.values.length);
                } else if (temps[1] == null) {
                    temps[1] = Arrays.copyOf(sensorEvent.values, sensorEvent.values.length);
                } else {
                    sm.unregisterListener(this);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        }
    }


    public static String getData(Context context) throws JSONException {
        JSONObject keyVal = null;
        JSONArray datas = new JSONArray();

        List<String> commands = new ArrayList<>();
        commands.add("getprop ro.bootimage.build.date.utc");
        commands.add("getprop ro.build.description");
        commands.add("getprop ro.build.fingerprint");
        commands.add("getprop ro.build.product");
        commands.add("getprop ro.build.version.all_codenames");
        commands.add("getprop ro.sf.lcd_density");

        commands.add("getprop dalvik.vm.isa.arm.features");//6
        commands.add("getprop dalvik.vm.isa.arm.variant");//7
        commands.add("getprop dalvik.vm.isa.arm64.features");//8
        commands.add("getprop dalvik.vm.isa.arm64.variant");//9
        commands.add("getprop ro.product.cpu.abi2");//10
        commands.add("getprop ro.arch");//11
        commands.add("getprop ro.chipname");//12
        commands.add("getprop ro.dalvik.vm.native.bridge");//13
        commands.add("getprop ro.dalvik.vm.isa.arm");//14
        commands.add("getprop persist.sys.nativebridge");//15
        commands.add("getprop ro.enable.native.bridge.exec");//16
        commands.add("getprop dalvik.vm.isa.x86.features");//17
        commands.add("getprop dalvik.vm.isa.x86.variant");//18
        commands.add("getprop ro.zygote");//19
        commands.add("getprop ro.allow.mock.location");//20
        commands.add("getprop vzw.os.rooted");//21
        commands.add("getprop ro.kernel.qemu");//22
        commands.add("getprop ro.hardware");//23
        commands.add("getprop gsm.sim.operator.iso-country");//24
        commands.add("getprop ro.build.display.id");//25
        commands.add("getprop ro.bootimage.build.fingerprint");//26
        commands.add("getprop gsm.network.type");//27
        commands.add("getprop persist.sys.timezone");//28
        commands.add("getprop ro.build.ab_update");//29
        commands.add("getprop ro.build.date");//30
        commands.add("getprop ro.build.characteristics");//31
        commands.add("getprop ro.build.flavor");//32

        commands.add("getprop ro.build.hardware.version");//33
        commands.add("getprop ro.build.date.utc");//34
        commands.add("getprop ro.build.system_root_image");//35
        commands.add("getprop ro.build.id");//36
        commands.add("getprop ro.build.version.base_os");//37
        commands.add("getprop ro.build.version.min_supported_target_sdk");//38
        commands.add("getprop ro.build.version.preview_sdk_fingerprint");//39
        commands.add("getprop ro.build.version.preview_sdk");//40
        commands.add("getprop ro.build.version.security_patch");//41


        ShellUtils.CommandResult result = ShellUtils.execCommand(commands, false);
        if (result.result == 0) {
            String[] results = result.successMsg.split("\n");
            if (results.length > 0 && !TextUtils.isEmpty(results[0])) {
                keyVal = new JSONObject();
                keyVal.put("key", "ro.bootimage.build.date.utc");
                keyVal.put("value", results[0]);
                datas.put(keyVal);
            }
            if (results.length > 1 && !TextUtils.isEmpty(results[1])) {
                keyVal = new JSONObject();
                keyVal.put("key", "ro.build.description");
                keyVal.put("value", results[1]);
                datas.put(keyVal);
            }

            if (results.length > 2 && !TextUtils.isEmpty(results[2])) {
                keyVal = new JSONObject();
                keyVal.put("key", "ro.build.fingerprint");
                keyVal.put("value", results[2]);
                datas.put(keyVal);
            }

            if (results.length > 3 && !TextUtils.isEmpty(results[3])) {
                keyVal = new JSONObject();
                keyVal.put("key", "ro.build.product");
                keyVal.put("value", results[3]);
                datas.put(keyVal);
            }
            if (results.length > 4 && !TextUtils.isEmpty(results[4])) {
                keyVal = new JSONObject();
                keyVal.put("key", "ro.build.version.all_codenames");
                keyVal.put("value", results[4]);
                datas.put(keyVal);
            }

            if (results.length > 5 && !TextUtils.isEmpty(results[5])) {
                keyVal = new JSONObject();
                keyVal.put("key", "ro.sf.lcd_density");
                keyVal.put("value", results[5]);
                datas.put(keyVal);
            }

            if (results.length > 6 && !TextUtils.isEmpty(results[6])) {
                keyVal = new JSONObject();
                keyVal.put("key", "dalvik.vm.isa.arm.features");
                keyVal.put("value", results[6]);
                datas.put(keyVal);
            }

            if (results.length > 7 && !TextUtils.isEmpty(results[7])) {
                keyVal = new JSONObject();
                keyVal.put("key", "dalvik.vm.isa.arm.variant");
                keyVal.put("value", results[7]);
                datas.put(keyVal);
            }

            if (results.length > 8 && !TextUtils.isEmpty(results[8])) {
                keyVal = new JSONObject();
                keyVal.put("key", "dalvik.vm.isa.arm64.features");
                keyVal.put("value", results[8]);
                datas.put(keyVal);
            }

            if (results.length > 9 && !TextUtils.isEmpty(results[9])) {
                keyVal = new JSONObject();
                keyVal.put("key", "dalvik.vm.isa.arm64.variant");
                keyVal.put("value", results[9]);
                datas.put(keyVal);
            }

            if (results.length > 10 && !TextUtils.isEmpty(results[10])) {
                keyVal = new JSONObject();
                keyVal.put("key", "ro.product.cpu.abi2");
                keyVal.put("value", results[10]);
                datas.put(keyVal);
            }

            if (results.length > 11 && !TextUtils.isEmpty(results[11])) {
                keyVal = new JSONObject();
                keyVal.put("key", "ro.arch");
                keyVal.put("value", results[11]);
                datas.put(keyVal);
            }


            if (results.length > 12 && !TextUtils.isEmpty(results[12])) {
                keyVal = new JSONObject();
                keyVal.put("key", "ro.chipname");
                keyVal.put("value", results[12]);
                datas.put(keyVal);
            }

            if (results.length > 13 && !TextUtils.isEmpty(results[13])) {
                keyVal = new JSONObject();
                keyVal.put("key", "ro.dalvik.vm.native.bridge");
                keyVal.put("value", results[13]);
                datas.put(keyVal);
            }

            if (results.length > 14 && !TextUtils.isEmpty(results[14])) {
                keyVal = new JSONObject();
                keyVal.put("key", "ro.dalvik.vm.isa.arm");
                keyVal.put("value", results[14]);
                datas.put(keyVal);
            }

            if (results.length > 15 && !TextUtils.isEmpty(results[15])) {
                keyVal = new JSONObject();
                keyVal.put("key", "persist.sys.nativebridge");
                keyVal.put("value", results[15]);
                datas.put(keyVal);
            }

            if (results.length > 16 && !TextUtils.isEmpty(results[16])) {
                keyVal = new JSONObject();
                keyVal.put("key", "ro.enable.native.bridge.exec");
                keyVal.put("value", results[16]);
                datas.put(keyVal);
            }

            if (results.length > 17 && !TextUtils.isEmpty(results[17])) {
                keyVal = new JSONObject();
                keyVal.put("key", "dalvik.vm.isa.x86.features");
                keyVal.put("value", results[17]);
                datas.put(keyVal);
            }

            if (results.length > 18 && !TextUtils.isEmpty(results[18])) {
                keyVal = new JSONObject();
                keyVal.put("key", "dalvik.vm.isa.x86.variant");
                keyVal.put("value", results[18]);
                datas.put(keyVal);
            }

            if (results.length > 19 && !TextUtils.isEmpty(results[19])) {
                keyVal = new JSONObject();
                keyVal.put("key", "ro.zygote");
                keyVal.put("value", results[19]);
                datas.put(keyVal);
            }

            if (results.length > 20 && !TextUtils.isEmpty(results[20])) {
                keyVal = new JSONObject();
                keyVal.put("key", "ro.allow.mock.location");
                keyVal.put("value", results[20]);
                datas.put(keyVal);
            }

            if (results.length > 21 && !TextUtils.isEmpty(results[21])) {
                keyVal = new JSONObject();
                keyVal.put("key", "vzw.os.rooted");
                keyVal.put("value", results[21]);
                datas.put(keyVal);
            }

            if (results.length > 22 && !TextUtils.isEmpty(results[22])) {
                keyVal = new JSONObject();
                keyVal.put("key", "ro.kernel.qemu");
                keyVal.put("value", results[22]);
                datas.put(keyVal);
            }

            if (results.length > 23 && !TextUtils.isEmpty(results[23])) {
                keyVal = new JSONObject();
                keyVal.put("key", "ro.hardware");
                keyVal.put("value", results[23]);
                datas.put(keyVal);
            }

            if (results.length > 24 && !TextUtils.isEmpty(results[24])) {
                keyVal = new JSONObject();
                keyVal.put("key", "gsm.sim.operator.iso-country");
                keyVal.put("value", results[24]);
                datas.put(keyVal);
            }

            if (results.length > 25 && !TextUtils.isEmpty(results[25])) {
                keyVal = new JSONObject();
                keyVal.put("key", "ro.build.display.id");
                keyVal.put("value", results[25]);
                datas.put(keyVal);
            }

            if (results.length > 26 && !TextUtils.isEmpty(results[26])) {
                keyVal = new JSONObject();
                keyVal.put("key", "ro.bootimage.build.fingerprint");
                keyVal.put("value", results[26]);
                datas.put(keyVal);
            }

            if (results.length > 27 && !TextUtils.isEmpty(results[27])) {
                keyVal = new JSONObject();
                keyVal.put("key", "gsm.network.type");
                keyVal.put("value", results[27]);
                datas.put(keyVal);
            }

            if (results.length > 28 && !TextUtils.isEmpty(results[28])) {
                keyVal = new JSONObject();
                keyVal.put("key", "persist.sys.timezone");
                keyVal.put("value", results[28]);
                datas.put(keyVal);
            }

            if (results.length > 29 && !TextUtils.isEmpty(results[29])) {
                keyVal = new JSONObject();
                keyVal.put("key", "ro.build.ab_update");
                keyVal.put("value", results[29]);
                datas.put(keyVal);
            }

            if (results.length > 30 && !TextUtils.isEmpty(results[30])) {
                keyVal = new JSONObject();
                keyVal.put("key", "ro.build.date");
                keyVal.put("value", results[30]);
                datas.put(keyVal);
            }

            if (results.length > 31 && !TextUtils.isEmpty(results[31])) {
                keyVal = new JSONObject();
                keyVal.put("key", "ro.build.characteristics");
                keyVal.put("value", results[31]);
                datas.put(keyVal);
            }

            if (results.length > 32 && !TextUtils.isEmpty(results[32])) {
                keyVal = new JSONObject();
                keyVal.put("key", "ro.build.flavor");
                keyVal.put("value", results[32]);
                datas.put(keyVal);
            }

            if (results.length > 33 && !TextUtils.isEmpty(results[33])) {
                keyVal = new JSONObject();
                keyVal.put("key", "ro.build.hardware.version");
                keyVal.put("value", results[33]);
                datas.put(keyVal);
            }

            if (results.length > 34 && !TextUtils.isEmpty(results[34])) {
                keyVal = new JSONObject();
                keyVal.put("key", "ro.build.date.utc");
                keyVal.put("value", results[34]);
                datas.put(keyVal);
            }

            if (results.length > 35 && !TextUtils.isEmpty(results[35])) {
                keyVal = new JSONObject();
                keyVal.put("key", "ro.build.system_root_image");
                keyVal.put("value", results[35]);
                datas.put(keyVal);
            }

            if (results.length > 36 && !TextUtils.isEmpty(results[36])) {
                keyVal = new JSONObject();
                keyVal.put("key", "ro.build.id");
                keyVal.put("value", results[36]);
                datas.put(keyVal);
            }

            if (results.length > 37 && !TextUtils.isEmpty(results[37])) {
                keyVal = new JSONObject();
                keyVal.put("key", "ro.build.version.base_os");
                keyVal.put("value", results[37]);
                datas.put(keyVal);
            }

            if (results.length > 38 && !TextUtils.isEmpty(results[38])) {
                keyVal = new JSONObject();
                keyVal.put("key", "ro.build.version.min_supported_target_sdk");
                keyVal.put("value", results[38]);
                datas.put(keyVal);
            }

            if (results.length > 39 && !TextUtils.isEmpty(results[39])) {
                keyVal = new JSONObject();
                keyVal.put("key", "ro.build.version.preview_sdk_fingerprint");
                keyVal.put("value", results[39]);
                datas.put(keyVal);
            }

            if (results.length > 40 && !TextUtils.isEmpty(results[40])) {
                keyVal = new JSONObject();
                keyVal.put("key", "ro.build.version.preview_sdk");
                keyVal.put("value", results[40]);
                datas.put(keyVal);
            }

            if (results.length > 41 && !TextUtils.isEmpty(results[41])) {
                keyVal = new JSONObject();
                keyVal.put("key", "ro.build.version.security_patch");
                keyVal.put("value", results[41]);
                datas.put(keyVal);
            }


        }

        ShellUtils.CommandResult cpuInfoResult = ShellUtils.execCommand("cat /proc/cpuinfo", false);
        if (cpuInfoResult.result == 0 && !TextUtils.isEmpty(cpuInfoResult.successMsg)) {
            keyVal = new JSONObject();
            keyVal.put("key", "cpu_info");
            keyVal.put("value", Base64.encodeToString(cpuInfoResult.successMsg.getBytes(), Base64.NO_WRAP));
            datas.put(keyVal);
        }

        ShellUtils.CommandResult cpuCore = ShellUtils.execCommand("cat /sys/devices/system/cpu/possible", false);
        if (cpuCore.result == 0 && !TextUtils.isEmpty(cpuCore.successMsg)) {
            String[] cpuCoreSplit = cpuCore.successMsg.replace("\n", "").split("-");
            if (cpuCoreSplit.length > 1) {
                int count = Integer.parseInt(cpuCoreSplit[1]) + 1;
                List<String> maxFreqCommands = new ArrayList<>();
                for (int y = 0; y < count; y++) {
                    maxFreqCommands.add("cat /sys/devices/system/cpu/cpu" + y + "/cpufreq/cpuinfo_max_freq");
                }
                ShellUtils.CommandResult possiblesResult = ShellUtils.execCommand(maxFreqCommands, false);
                if (possiblesResult.result == 0 && !TextUtils.isEmpty(possiblesResult.successMsg)) {
                    String[] results = possiblesResult.successMsg.split("\n");
                    StringBuilder stringBuilder = new StringBuilder(results.length);
                    for (int o = 0; o < results.length; o++) {
                        stringBuilder.append(results[o]);
                        if (o != results.length - 1) {
                            stringBuilder.append(",");
                        }
                    }

                    keyVal = new JSONObject();
                    keyVal.put("key", "cpuinfo_max_freq_cpus");
                    keyVal.put("value", stringBuilder.toString());
                    datas.put(keyVal);
                }
            }
        }


        keyVal = new JSONObject();
        keyVal.put("key", "pkg");
        keyVal.put("value", context.getPackageName());
        datas.put(keyVal);

        String bootloader = Build.BOOTLOADER;
        if (!TextUtils.isEmpty(bootloader) && !"unknown".equals(bootloader)) {
            keyVal = new JSONObject();
            keyVal.put("key", "ro.bootloader");
            keyVal.put("value", bootloader);
            datas.put(keyVal);
        }


        keyVal = new JSONObject();
        keyVal.put("key", "ro.build.host");
        keyVal.put("value", Build.HOST);
        datas.put(keyVal);

        keyVal = new JSONObject();
        keyVal.put("key", "ro.build.id");
        keyVal.put("value", Build.ID);
        datas.put(keyVal);

        keyVal = new JSONObject();
        keyVal.put("key", "ro.build.tags");
        keyVal.put("value", Build.TAGS);
        datas.put(keyVal);

        keyVal = new JSONObject();
        keyVal.put("key", "ro.build.type");
        keyVal.put("value", Build.TYPE);
        datas.put(keyVal);

        keyVal = new JSONObject();
        keyVal.put("key", "ro.build.user");
        keyVal.put("value", Build.USER);
        datas.put(keyVal);

        keyVal = new JSONObject();
        keyVal.put("key", "ro.build.version.codename");
        keyVal.put("value", Build.VERSION.CODENAME);
        datas.put(keyVal);

        keyVal = new JSONObject();
        keyVal.put("key", "ro.build.version.incremental");
        keyVal.put("value", Build.VERSION.INCREMENTAL);
        datas.put(keyVal);

        keyVal = new JSONObject();
        keyVal.put("key", "ro.build.version.release");
        keyVal.put("value", Build.VERSION.RELEASE);
        datas.put(keyVal);

        keyVal = new JSONObject();
        keyVal.put("key", "ro.build.version.sdk");
        keyVal.put("value", String.valueOf(Build.VERSION.SDK_INT));
        datas.put(keyVal);

        keyVal = new JSONObject();
        keyVal.put("key", "ro.product.manufacturer");
        keyVal.put("value", Build.MANUFACTURER);
        datas.put(keyVal);

        keyVal = new JSONObject();
        keyVal.put("key", "ro.product.model");
        keyVal.put("value", Build.MODEL);
        datas.put(keyVal);

        keyVal = new JSONObject();
        keyVal.put("key", "ro.product.name");
        keyVal.put("value", Build.PRODUCT);
        datas.put(keyVal);

        keyVal = new JSONObject();
        keyVal.put("key", "ro.product.board");
        keyVal.put("value", Build.BOARD);
        datas.put(keyVal);

        keyVal = new JSONObject();
        keyVal.put("key", "ro.product.brand");
        keyVal.put("value", Build.BRAND);
        datas.put(keyVal);

        keyVal = new JSONObject();
        keyVal.put("key", "ro.product.cpu.abi");
        keyVal.put("value", Build.CPU_ABI);
        datas.put(keyVal);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            keyVal = new JSONObject();
            keyVal.put("key", "ro.product.cpu.abilist");
            keyVal.put("value", splitJoint(Build.SUPPORTED_ABIS));
            datas.put(keyVal);

            keyVal = new JSONObject();
            keyVal.put("key", "ro.product.cpu.abilist32");
            keyVal.put("value", splitJoint(Build.SUPPORTED_32_BIT_ABIS));
            datas.put(keyVal);

            keyVal = new JSONObject();
            keyVal.put("key", "ro.product.cpu.abilist64");
            keyVal.put("value", splitJoint(Build.SUPPORTED_64_BIT_ABIS));
            datas.put(keyVal);
        }

        keyVal = new JSONObject();
        keyVal.put("key", "ro.product.device");
        keyVal.put("value", Build.DEVICE);
        datas.put(keyVal);

        keyVal = new JSONObject();

        String serial = Build.SERIAL;
        if (!TextUtils.isEmpty(serial) && !"unknown".equals(serial)) {
            keyVal.put("key", "ro.serialno");
            keyVal.put("value", serial);
            datas.put(keyVal);
        }

        keyVal = new JSONObject();
        keyVal.put("key", "screen_width");
        keyVal.put("value", String.valueOf(context.getResources().getDisplayMetrics().widthPixels));
        datas.put(keyVal);

        keyVal = new JSONObject();
        keyVal.put("key", "screen_height");
        keyVal.put("value", String.valueOf(context.getResources().getDisplayMetrics().heightPixels));
        datas.put(keyVal);

        keyVal = new JSONObject();
        keyVal.put("key", "dpi");
        keyVal.put("value", String.valueOf(Resources.getSystem().getDisplayMetrics().densityDpi));
        datas.put(keyVal);

        keyVal = new JSONObject();
        keyVal.put("key", "top_height");
        keyVal.put("value", String.valueOf(getStatusBarHeight(context)));
        datas.put(keyVal);

        keyVal = new JSONObject();
        keyVal.put("key", "bottom_height");
        keyVal.put("value", String.valueOf(getNavigationBarHeight(context)));
        datas.put(keyVal);

        String blueMac = getBlueMac(context);
        if (!TextUtils.isEmpty(blueMac) && !"00:00:00:00:00:00".equals(blueMac)
                && !"02:00:00:00:00:00".equals(blueMac)) {
            keyVal = new JSONObject();
            keyVal.put("key", "bluetooth_address");
            keyVal.put("value", blueMac);
            datas.put(keyVal);
        }

        keyVal = new JSONObject();
        keyVal.put("key", "bluetooth_name");
        keyVal.put("value", "");
        datas.put(keyVal);

//        String gaid = "NULL";
//        if (!TextUtils.isEmpty(gaid)) {
//            keyVal = new JSONObject();
//            keyVal.put("key", "gaid");
//            keyVal.put("value", gaid);
//            datas.put(keyVal);
//        }

        //总内存大小
        keyVal = new JSONObject();

        long memorySize = Utils.getTotalMemory(context);
        long memoryAvailSize = Utils.getAvailMemory(context);
        long memoryUseSize = memorySize - memoryAvailSize;
        keyVal.put("key", "memory");
        keyVal.put("value", memoryUseSize + "/" + memorySize);
        datas.put(keyVal);

        //总磁盘大小
        keyVal = new JSONObject();

        long romSize = getTotalSpace(Environment.getDataDirectory().getAbsolutePath());//手机内部存储大小
        long romAvailSize = getAvailSpace(Environment.getDataDirectory().getAbsolutePath());//手机内部存储大小
        long romUseSize = romSize - romAvailSize;
        keyVal.put("key", "disk");
        keyVal.put("value", romUseSize + "/" + romSize);
        datas.put(keyVal);


        long[] disks = getDiskInfo(Environment.getDataDirectory().getAbsolutePath());//手机内部存储大小
        keyVal = new JSONObject();
        keyVal.put("key", "disk.block_size");
        keyVal.put("value", String.valueOf(disks[0]));
        datas.put(keyVal);

        keyVal = new JSONObject();
        keyVal.put("key", "disk.available_blocks");
        keyVal.put("value", String.valueOf(disks[1]));
        datas.put(keyVal);

        keyVal = new JSONObject();
        keyVal.put("key", "disk.total_block_count");
        keyVal.put("value", String.valueOf(disks[2]));
        datas.put(keyVal);


        String wifiMac = getWifiMac();
        if (!TextUtils.isEmpty(wifiMac) && !"00:00:00:00:00:00".equals(wifiMac)
                && !"02:00:00:00:00:00".equals(wifiMac)) {
            keyVal = new JSONObject();
            keyVal.put("key", "wifi_mac");
            keyVal.put("value", wifiMac);
            datas.put(keyVal);
        }

        String wifiName = getConnectWifiSsid(context);
        if (!TextUtils.isEmpty(wifiName)) {
            keyVal = new JSONObject();
            keyVal.put("key", "wifi_name");
            keyVal.put("value", wifiName);
            datas.put(keyVal);
        }

        String bssid = getConnectWifiBssid(context);
        if (!TextUtils.isEmpty(wifiName)) {
            keyVal = new JSONObject();
            keyVal.put("key", "wifi_bssid");
            keyVal.put("value", bssid);
            datas.put(keyVal);
        }

        //运营商名称
        String nonName = Utils.getNetworkOperatorName(context);
        if (!TextUtils.isEmpty(nonName)) {
            keyVal = new JSONObject();
            keyVal.put("key", "non_name");
            keyVal.put("value", Utils.getNetworkOperatorName(context));
            datas.put(keyVal);
        }

        if (!TextUtils.isEmpty(tarPath)) {
            keyVal = new JSONObject();
            keyVal.put("key", "tar");
            keyVal.put("value", tarPath);
            datas.put(keyVal);
        }

        String imei = Utils.getIMEI(context);
        if (!TextUtils.isEmpty(imei)) {
            keyVal = new JSONObject();
            keyVal.put("key", "imei");
            keyVal.put("value", imei);
            datas.put(keyVal);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            String deviceName = Settings.Global.getString(context.getContentResolver(), "device_name");
            if (!TextUtils.isEmpty(deviceName)) {
                keyVal = new JSONObject();
                keyVal.put("key", "device_name");
                keyVal.put("value", Settings.Global.getString(context.getContentResolver(), "device_name"));
                datas.put(keyVal);
            }
        }

        keyVal = new JSONObject();
        keyVal.put("key", "user_agent");
        keyVal.put("value", userAgent);
        datas.put(keyVal);

        /////////////////////////////////////////////////////////////////////////////////////////

        Locale locale = context.getResources().getConfiguration().locale;

        String language = locale.getLanguage();

        String country = locale.getCountry();

        String lang = locale.getDisplayLanguage();


        keyVal = new JSONObject();
        keyVal.put("key", "country");
        keyVal.put("value", country);
        datas.put(keyVal);


        keyVal = new JSONObject();
        keyVal.put("key", "ro.product.locale");
        keyVal.put("value", language);
        datas.put(keyVal);


        keyVal = new JSONObject();
        keyVal.put("key", "lang");
        keyVal.put("value", lang);
        datas.put(keyVal);

        TimeZone tz = TimeZone.getDefault();
        keyVal = new JSONObject();
        keyVal.put("key", "timezone");
        keyVal.put("value", tz.getDisplayName(false, TimeZone.SHORT));
        datas.put(keyVal);


        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        keyVal = new JSONObject();
        keyVal.put("key", "gsm.sim.operator.alpha");
        keyVal.put("value", telephonyManager.getSimOperatorName());
        datas.put(keyVal);


//        keyVal = new JSONObject();
//        keyVal.put("key", "isGaidWithGps");
//        keyVal.put("value", String.valueOf(GpsHelper.isGaidWithGps()));
//        datas.put(keyVal);

        DisplayMetrics v1 = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(v1);

        int size = context.getResources().getConfiguration().screenLayout & 15;

        keyVal = new JSONObject();
        keyVal.put("key", "size");
        keyVal.put("value", String.valueOf(size));
        datas.put(keyVal);

        keyVal = new JSONObject();
        keyVal.put("key", "xdp");
        keyVal.put("value", String.valueOf(v1.xdpi));
        datas.put(keyVal);

        keyVal = new JSONObject();
        keyVal.put("key", "ydp");
        keyVal.put("value", String.valueOf(v1.ydpi));
        datas.put(keyVal);

        keyVal = new JSONObject();
        keyVal.put("key", "density_api");
        keyVal.put("value", String.valueOf(v1.scaledDensity));
        datas.put(keyVal);

        int screenLayout = context.getResources().getConfiguration().screenLayout;
        keyVal = new JSONObject();
        keyVal.put("key", "screen_layout");
        keyVal.put("value", String.valueOf(screenLayout));
        datas.put(keyVal);


        String httpAgent = System.getProperty("http.agent");
        if (httpAgent != null) {
            keyVal = new JSONObject();
            keyVal.put("key", "http_agent");
            keyVal.put("value", httpAgent);
            datas.put(keyVal);
        }


        keyVal = new JSONObject();
        keyVal.put("key", "android_id");
        keyVal.put("value", Utils.getAndroidId(context));
        datas.put(keyVal);

        keyVal = new JSONObject();
        keyVal.put("key", "oaid");
        keyVal.put("value", MDIDHandler.getMdid());
        datas.put(keyVal);

        keyVal = new JSONObject();
        keyVal.put("key", "appid");
        keyVal.put("value", mAppid);
        datas.put(keyVal);

        keyVal = new JSONObject();
        keyVal.put("key", "network");
        keyVal.put("value", Utils.getNetworkType(context));
        datas.put(keyVal);

        SensorManager sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = sm.getSensorList(Sensor.TYPE_ALL);
        keyVal = new JSONObject();

        JSONArray sensorArray = new JSONArray();
        for (Sensor sensor : sensorList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", sensor.getName());
            jsonObject.put("vendor", sensor.getVendor());
            jsonObject.put("version", sensor.getVersion());
            jsonObject.put("type", sensor.getType());
            jsonObject.put("maxRange", sensor.getMaximumRange());
            jsonObject.put("resolution", sensor.getResolution());
            jsonObject.put("power", sensor.getPower());
            jsonObject.put("minDelay", sensor.getMinDelay());
            float[][] temps = null;
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                temps = sensorNumbers.ACCELEROMETER_VALUES;
            } else if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                temps = sensorNumbers.MAGNETIC_FIELD_VALUES;
            } else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                temps = sensorNumbers.GYROSCOPE_VALUES;
            }
            if (temps != null) {
                JSONArray jsonArray;
                if (temps[0] != null) {
                    jsonArray = new JSONArray();
                    for (float temp : temps[0]) {
                        jsonArray.put(temp);
                    }
                    jsonObject.put("value_1", jsonArray);
                }

                if (temps[1] != null) {
                    jsonArray = new JSONArray();
                    for (float temp : temps[1]) {
                        jsonArray.put(temp);
                    }
                    jsonObject.put("value_2", jsonArray);
                }

            }
            sensorArray.put(jsonObject);
        }
        keyVal.put("key", "sensors");
        keyVal.put("value", sensorArray.toString());
        datas.put(keyVal);


        BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isEnabled = blueAdapter.isEnabled();

        keyVal = new JSONObject();
        keyVal.put("key", "bluetooth_manager");
        keyVal.put("value", isEnabled ? "6" : 8);
        datas.put(keyVal);

        int simState = telephonyManager.getSimState();

        String simStateStr = "";
        switch (simState) {
            case TelephonyManager.SIM_STATE_UNKNOWN:
                simStateStr = "UNKNOWN";
                break;
            case TelephonyManager.SIM_STATE_ABSENT:
                simStateStr = "ABSENT";
                break;
            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                simStateStr = "PIN_REQUIRED";
                break;
            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                simStateStr = "PUK_REQUIRED";
                break;
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                simStateStr = "NETWORK_LOCKED";
                break;
            case TelephonyManager.SIM_STATE_READY:
                simStateStr = "READY";
                break;
            case TelephonyManager.SIM_STATE_NOT_READY:
                simStateStr = "NOT_READY";
                break;
            case TelephonyManager.SIM_STATE_PERM_DISABLED:
                simStateStr = "PERM_DISABLED";
                break;
            case TelephonyManager.SIM_STATE_CARD_IO_ERROR:
                simStateStr = "CARD_IO_ERROR";
                break;
            case TelephonyManager.SIM_STATE_CARD_RESTRICTED:
                simStateStr = "CARD_RESTRICTED";
                break;
        }

        keyVal = new JSONObject();
        keyVal.put("key", "gsm.sim.state");
        keyVal.put("value", simStateStr);
        datas.put(keyVal);

        keyVal = new JSONObject();
        keyVal.put("key", "gsm.sim.operator.numeric");
        keyVal.put("value", telephonyManager.getSimOperator());
        datas.put(keyVal);

        keyVal = new JSONObject();
        keyVal.put("key", "gsm.sim.operator.iso-country");
        keyVal.put("value", telephonyManager.getSimCountryIso());
        datas.put(keyVal);

        keyVal = new JSONObject();
        keyVal.put("key", "gsm.operator.alphac");
        keyVal.put("value", telephonyManager.getNetworkOperatorName());
        datas.put(keyVal);

        keyVal = new JSONObject();
        keyVal.put("key", "gsm.operator.numeric");
        keyVal.put("value", telephonyManager.getNetworkOperator());
        datas.put(keyVal);

        keyVal = new JSONObject();
        keyVal.put("key", "gsm.operator.iso-country");
        keyVal.put("value", telephonyManager.getNetworkCountryIso());
        datas.put(keyVal);

        keyVal = new JSONObject();
        keyVal.put("key", "last_boot_time");
        keyVal.put("value", String.valueOf(System.currentTimeMillis() - SystemClock.elapsedRealtime()));
        datas.put(keyVal);

        // 使用
        Location location = getLastKnownLocation(context);
        if (location != null) {
            double latitude = location.getLatitude();//经度
            double longitude = location.getLongitude();//纬度
            double altitude = location.getAltitude();//海拔
            float speed = location.getSpeed();//速度
            float bearing = location.getBearing();//方向

            keyVal = new JSONObject();
            keyVal.put("key", "last_boot_time");
            keyVal.put("value", String.valueOf(System.currentTimeMillis() - SystemClock.elapsedRealtime()));
            datas.put(keyVal);

            keyVal = new JSONObject();
            keyVal.put("key", "gps.longitude");
            keyVal.put("value", String.valueOf(longitude));
            datas.put(keyVal);

            keyVal = new JSONObject();
            keyVal.put("key", "gps.latitude");
            keyVal.put("value", String.valueOf(latitude));
            datas.put(keyVal);

            keyVal = new JSONObject();
            keyVal.put("key", "gps.altitude");
            keyVal.put("value", String.valueOf(altitude));
            datas.put(keyVal);

            keyVal = new JSONObject();
            keyVal.put("key", "gps.speed");
            keyVal.put("value", String.valueOf(speed));
            datas.put(keyVal);

            keyVal = new JSONObject();
            keyVal.put("key", "gps.bearing");
            keyVal.put("value", String.valueOf(bearing));
            datas.put(keyVal);
        }

        try {
            IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent receiver = context.registerReceiver(null, filter);
            if (receiver != null) {
                int level = receiver.getIntExtra("level", 0);//获取当前电量
                //1、Power source is an AC charger，2、Power source is a USB port. 4、Power source is wireless.
                int status = receiver.getIntExtra("status", 0);//获取充电状态
                int voltage = receiver.getIntExtra("voltage", 0);//获取电压(mv)
                int temperature = receiver.getIntExtra("temperature", 0);//获取温度(数值)
                double t = temperature / 10.0;  //运算转换,电池摄氏温度，默认获取的非摄氏温度值

                keyVal = new JSONObject();
                keyVal.put("key", "battery.capacity");
                keyVal.put("value", String.valueOf(level));
                datas.put(keyVal);

                keyVal = new JSONObject();
                keyVal.put("key", "battery.status");
                keyVal.put("value", String.valueOf(status));
                datas.put(keyVal);

                keyVal = new JSONObject();
                keyVal.put("key", "battery.voltage_now");
                keyVal.put("value", String.valueOf(voltage));
                datas.put(keyVal);

                keyVal = new JSONObject();
                keyVal.put("key", "battery.temp");
                keyVal.put("value", String.valueOf(t));
                datas.put(keyVal);
            }

        } catch (
                Throwable e) {
            e.printStackTrace();
        }

        // Wifi的连接速度及信号强度：
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        WifiInfo info = wifiManager.getConnectionInfo();
        if (info.getBSSID() != null) {
            // 链接信号强度
            int rssi = info.getRssi();
            keyVal = new JSONObject();
            keyVal.put("key", "com.cph.wifi.rssi");
            keyVal.put("value", String.valueOf(rssi));
            datas.put(keyVal);

            // 链接速度
            String speed = info.getLinkSpeed() + WifiInfo.LINK_SPEED_UNITS;
            keyVal = new JSONObject();
            keyVal.put("key", "network.speed");
            keyVal.put("value", speed);
            datas.put(keyVal);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                String frequency = info.getFrequency() + WifiInfo.FREQUENCY_UNITS;
                //wifi频率
                keyVal = new JSONObject();
                keyVal.put("key", "network.frequency");
                keyVal.put("value", frequency);
                datas.put(keyVal);
            }
        }

        int simDmb = getMobileDbm(context);
        if (simDmb != -1) {
            keyVal = new JSONObject();
            keyVal.put("key", "com.cph.im.rssi");
            keyVal.put("value", String.valueOf(simDmb));
            datas.put(keyVal);
        }

        boolean afExists = false;
        try {
            Class.forName("com.appsflyer.AppsFlyerLib");
            afExists = true;
        } catch (
                Exception ignored) {
        }

        keyVal = new JSONObject();
        keyVal.put("key", "af_exists");
        keyVal.put("value", String.valueOf(afExists).toUpperCase());
        datas.put(keyVal);

        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo("com.android.vending", 0);
            String versionName = packageInfo.versionName;
            int versionCode = packageInfo.versionCode;

            keyVal = new JSONObject();
            keyVal.put("key", "gp_vn");
            keyVal.put("value", versionName);
            datas.put(keyVal);

            keyVal = new JSONObject();
            keyVal.put("key", "gp_vc");
            keyVal.put("value", String.valueOf(versionCode));
            datas.put(keyVal);
        } catch (
                PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        return datas.toString();
    }

    public static int getMobileDbm(Context context) {
        int dbm = -1;
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            List<CellInfo> cellInfoList = null;
            if (checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return dbm;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                cellInfoList = tm.getAllCellInfo();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (null != cellInfoList) {
                    for (CellInfo cellInfo : cellInfoList) {
                        if (cellInfo instanceof CellInfoGsm) {
                            CellSignalStrengthGsm cellSignalStrengthGsm = ((CellInfoGsm) cellInfo).getCellSignalStrength();
                            dbm = cellSignalStrengthGsm.getDbm();

                        } else if (cellInfo instanceof CellInfoCdma) {
                            CellSignalStrengthCdma cellSignalStrengthCdma = ((CellInfoCdma) cellInfo).getCellSignalStrength();
                            dbm = cellSignalStrengthCdma.getDbm();

                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && cellInfo instanceof CellInfoWcdma) {
                            CellSignalStrengthWcdma cellSignalStrengthWcdma = ((CellInfoWcdma) cellInfo).getCellSignalStrength();
                            dbm = cellSignalStrengthWcdma.getDbm();

                        } else if (cellInfo instanceof CellInfoLte) {
                            CellSignalStrengthLte cellSignalStrengthLte = ((CellInfoLte) cellInfo).getCellSignalStrength();
                            dbm = cellSignalStrengthLte.getDbm();

                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && cellInfo instanceof CellInfoTdscdma) {
                            CellSignalStrengthTdscdma cellSignalStrengthTdscdma = ((CellInfoTdscdma) cellInfo).getCellSignalStrength();
                            dbm = cellSignalStrengthTdscdma.getDbm();

                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && cellInfo instanceof CellInfoNr) {
                            CellSignalStrength cellSignalStrength = ((CellInfoNr) cellInfo).getCellSignalStrength();
                            dbm = cellSignalStrength.getDbm();

                        }
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return dbm;

    }


    private static Location getLastKnownLocation(Context context) {
        //获取地理位置管理器
        LocationManager mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO:去请求权限后再获取
            return null;
        }
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
// 在一些手机5.0(api21)获取为空后，采用下面去兼容获取。
        if (bestLocation == null) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setCostAllowed(true);
            criteria.setPowerRequirement(Criteria.POWER_LOW);
            String provider = mLocationManager.getBestProvider(criteria, true);
            if (!TextUtils.isEmpty(provider)) {
                assert provider != null;
                bestLocation = mLocationManager.getLastKnownLocation(provider);
            }
        }
        return bestLocation;
    }

    public static int checkSelfPermission(@NonNull Context context, @NonNull String permission) {

        return context.checkPermission(permission, android.os.Process.myPid(), Process.myUid());
    }

    /**
     * 获取某个目录的可用空间
     */

    public static long getTotalSpace(String path) {
        StatFs statfs = new StatFs(path);

        long size = statfs.getBlockSize();//获取分区的大小

        long count = statfs.getAvailableBlocks();//获取可用分区块的个数

        return size * count;
    }

    public static long[] getDiskInfo(String path) {
        StatFs statfs = new StatFs(path);
        long[] values = new long[3];
        values[0] = statfs.getBlockSize();
        values[1] = statfs.getAvailableBlocks();
        values[2] = statfs.getBlockCount();
        return values;//获取分区的大小
    }

    public static long getAvailSpace(String path) {

        StatFs dataFs = new StatFs(path);

        return dataFs.getFreeBlocks() * dataFs.getBlockSize();
    }


    private static String splitJoint(String[] strs) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < strs.length; i++) {
            String str = strs[i];
            stringBuilder.append(str);
            if (i != strs.length - 1) {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }

    private static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    private static String getConnectWifiSsid(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = null;
            if (wifiManager != null) {
                wifiInfo = wifiManager.getConnectionInfo();
//                Log.d("wifiInfo", wifiInfo.toString());
//                Log.d("SSID", wifiInfo.getSSID());
                return wifiInfo.getSSID();
            }
        } catch (Throwable e) {
//            ZCLog.e(e);
            Log.d(TAG, e.getMessage());
        }
        return "";
    }

    private static String getConnectWifiBssid(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = null;
            if (wifiManager != null) {
                wifiInfo = wifiManager.getConnectionInfo();
//                Log.d("wifiInfo", wifiInfo.toString());
//                Log.d("SSID", wifiInfo.getBSSID());
                return wifiInfo.getBSSID();
            }
        } catch (Throwable e) {
//            ZCLog.e(e);
            Log.d(TAG, e.getMessage());
        }
        return "";
    }

    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
            //do something
        }
        return hasNavigationBar;
    }

    private static String getBlueMac(Context ctx) {
        String packageName = ctx.getPackageName();
        try {
            if (PackageManager.PERMISSION_DENIED == ctx.getPackageManager().checkPermission("android.permission.BLUETOOTH", packageName)) {
                return "";
            }
            return getBluetoothMacAddress();
        } catch (Throwable e) {
            return "";
        }
    }

    private static String getBluetoothMacAddress() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String bluetoothMacAddress = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            try {
                Field mServiceField = bluetoothAdapter.getClass().getDeclaredField("mService");
                mServiceField.setAccessible(true);

                Object btManagerService = mServiceField.get(bluetoothAdapter);

                if (btManagerService != null) {
                    bluetoothMacAddress = (String) btManagerService.getClass().getMethod("getAddress").invoke(btManagerService);
                }
            } catch (NoSuchFieldException e) {

            } catch (NoSuchMethodException e) {

            } catch (IllegalAccessException e) {

            } catch (InvocationTargetException e) {

            }
        } else {
            bluetoothMacAddress = bluetoothAdapter.getAddress();
        }
        return bluetoothMacAddress;
    }

    private static String getWifiMac() {
        String mac = null;
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iF = interfaces.nextElement();
                if ("wlan0".equals(iF.getName())) {
                    byte[] addr = iF.getHardwareAddress();
                    if (addr == null || addr.length == 0) {
                        continue;
                    }
                    StringBuilder buf = new StringBuilder();
                    for (byte b : addr) {
                        buf.append(String.format("%02X:", b));
                    }
                    if (buf.length() > 0) {
                        buf.deleteCharAt(buf.length() - 1);
                    }
                    mac = buf.toString();
                    break;
                }
//                Log.i("tujiantao", "interfaceName="+iF.getName()+", mac="+mac);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return mac;
    }
}
