package com.test.ad.demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.business.support.StrategyInfoListener;
import com.business.support.TaskMonitorListener;
import com.business.support.WhiteService;
import com.business.support.YMBusinessService;
import com.business.support.adinfo.BSAdType;
import com.business.support.ascribe.InstallListener;
import com.business.support.compose.SIDListener;
import com.business.support.config.Const;
import com.business.support.jump.JumpService;
import com.business.support.jump.NativeActivity;
import com.business.support.jump.NativeAdManager;
import com.business.support.utils.ContextHolder;
import com.business.support.utils.ImageResultListener;
import com.business.support.utils.SLog;
import com.business.support.webview.CacheWebView;
import com.business.support.webview.ImagePreserve;
import com.business.support.webview.InnerWebViewActivity;
import com.business.support.webview.InnerWebViewActivity2;
import com.business.support.webview.WebViewToNativeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.thinkingdata.android.TDConfig;
import cn.thinkingdata.android.ThinkingAnalyticsSDK;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    CacheWebView cacheWebView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window window = getWindow();
        if (window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }
//        cacheWebView = new CacheWebView(this);
//        //http://redbag.adspools.cn:8081/?appId=119&token=ad6736e3-8384-42b0-90de-11924877129a&uid=20210324105106534533243063316480&IMEI=cd389fbee1d57a31231365551111&team=002&isNew=false
//        cacheWebView.loadUrl("https://m.baidu.com");
        findViewById(R.id.nativeAdBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NativeAdActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });


        YMBusinessService.setFirstInstallTime(System.currentTimeMillis());
        YMBusinessService.setRewardedVideoTimes(1);
        YMBusinessService.requestRewaredConfig(this, "1004", new StrategyInfoListener() {
            @Override
            public void isActive(boolean isActive, boolean install) {
                Log.i("YMBusinessService", "isActive=" + isActive + ",install=" + install);
            }
        });

        findViewById(R.id.rewardedVideoBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RewardVideoAdActivity.class));
            }
        });

        findViewById(R.id.rewardedVideoBtn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RewardVideoAdActivity2.class));
            }
        });

        findViewById(R.id.interstitialBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, InterstitialAdActivity.class));
            }
        });

        findViewById(R.id.bannerBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, BannerAdActivity.class));
            }
        });

        findViewById(R.id.splashBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SplashAdActivity.class));
            }
        });

        findViewById(R.id.nativeBannerAdBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NativeBannerActivity.class));
            }
        });

        findViewById(R.id.nativeSplashAdBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NativeSplashActivity.class));
            }
        });

        findViewById(R.id.nativeListBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NativeListActivity.class));
            }
        });

        findViewById(R.id.webviewBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                YMBusinessService.startCacheWebViewPage(MainActivity.this, cacheWebView, new WebViewToNativeListener() {
                    @Override
                    public void event1(InnerWebViewActivity activity) {

                    }

                    @Override
                    public void event2(InnerWebViewActivity2 activity) {

                    }

                });
            }
        });


        findViewById(R.id.webviewBtn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //不带缓存的webview content://com.scqdd.mobi.bssdk/bs_external_res_h5/forumweb/forumweb/index.html?user=1
                //file:///android_asset/forumweb/index.html?appId=111&token=c8f4e78d-f372-43f9-82f6-275de3421cf5
                YMBusinessService.startWebViewPage(MainActivity.this, "file:///android_asset/test.html", new WebViewToNativeListener() {

                    @Override
                    public void event1(InnerWebViewActivity activity) {

                    }

                    @Override
                    public void event2(InnerWebViewActivity2 activity) {

                    }

                });
            }
        });


        findViewById(R.id.contentBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ContentAdActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.startAdApp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = YMBusinessService.startCurrentAdApp("1");
                Log.i(TAG, "是否启动成功 result=" + result);

            }
        });

        findViewById(R.id.startAdApp2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = YMBusinessService.startCurrentAdApp("2");
                Log.i(TAG, "是否启动成功 result=" + result);

            }
        });

        YMBusinessService.setAndRefreshTaskMonitor(new TaskMonitorListener() {
            @Override
            public void over(String sceneId) {
                Log.i(TAG, "setAndRefreshTaskMonitor 任务完成  ok sceneId=" + sceneId);
            }
        });

        findViewById(R.id.customBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(MainActivity.this, WhiteService.class));
            }
        });

//        Const.HANDLER.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (cacheWebView.getParent() != null && cacheWebView.getParent() instanceof ViewGroup) {
//                    ViewGroup viewGroup = (ViewGroup) cacheWebView.getParent();
//                    viewGroup.removeView(cacheWebView);
//                }
//                InnerWebViewActivity.launch(MainActivity.this, cacheWebView);
//            }
//        },10000);

//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this).setIcon(R.mipmap.ic_launcher).setTitle("OAID")
//                        .setMessage(OAIDHandler.getOAID()).setPositiveButton("复制", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                putTextIntoClip(MainActivity.this, OAIDHandler.getOAID());
//                            }
//                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                            }
//                        });
//                builder.create().show();
//            }
//        }, 1000);


//        ResultData emulatorResult = EmulatorCheck.validCheck(this);
//
//        ResultData rootResult = RootCheck.validCheck(this);
//
//        ResultData hookResult = HookCheck.validCheck(this);
//
//        ResultData wireSharkResult = WireSharkCheck.validCheck(this);
//
//        Log.i("check", "isEmulator=" + emulatorResult.isError() + ",errorMessage=" + emulatorResult.getErrorMessage());
//
//        Log.i("check", "isRoot=" + rootResult.isError() + ",errorMessage=" + emulatorResult.getErrorMessage());
//
//        Log.i("check", "isHook=" + hookResult.isError() + ",errorMessage=" + emulatorResult.getErrorMessage());
//
//        Log.i("check", "isWireShark=" + wireSharkResult.isError() + ",errorMessage=" + emulatorResult.getErrorMessage());

        TDConfig biConfig = TDConfig.getInstance(this, "a697ed0e5fb34fba839cd1694b69d84a", " https://biapi.adsgreat.cn/logbu");
        biConfig.setMode(TDConfig.ModeEnum.DEBUG);
        ThinkingAnalyticsSDK biInstance = ThinkingAnalyticsSDK.sharedInstance(biConfig);

        YMBusinessService.init(this, biInstance,
                "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAMsZuh7bnTRuNGmu8urpyfvB5NERn6Z1dylHYD2Lgs2nKTUYJDoKsU+ALI21MY0NPif3YgdKgzMRZWg3zTL8fA8CAwEAAQ==",
                new SIDListener() {
                    @Override
                    public void onSuccess(int score, String data) {
                        Toast.makeText(getBaseContext(), "score=" + score + "\n" + "data=" + data, Toast.LENGTH_LONG).show();
                        Log.i("check-tjt", "onSuccess score=" + score + ",data=\n" + data);
                    }

                    @Override
                    public void onFailure(String msg) {
                        Log.i("check-tjt", "onFailure msg=\n" + msg);
                    }
                });
        YMBusinessService.enableAdTrace(new InstallListener() {
            @Override
            public void installedHit(String pkg, BSAdType bsAdType, String sceneId) {
                SLog.i(TAG, "installedHit pkg=" + pkg + ",sceneId=" + sceneId);
            }
        });
//        ResUpdateManager.getH5ResPathAndUpdate("95", "95", 101, new ResH5Listener() {
//
//            /**
//             * 资源获取回调函数
//             * @param isSuccess 是否成功返回
//             * @param path 返回的H5主页面地址
//             */
//            @Override
//            public void result(boolean isSuccess, String path) {
//                Log.i("check-tjt", "getH5ResPathAndUpdate result isSuccess=" + isSuccess + ",path=\n" + path);
//                if (!isSuccess) return;
//                YMBusinessService.startWebViewPage(MainActivity.this, path + "?appId=111&token=c8f4e78d-f372-43f9-82f6-275de3421cf5",
//                        "b5fb2228113cf7", new WebViewToNativeListener() {
//                            @Override
//                            public void event1(InnerWebViewActivity activity) {
//
//                            }
//
//                            @Override
//                            public void event2(InnerWebViewActivity2 activity) {
//
//                            }
//
//                            @Override
//                            public void tracking(String name, JSONObject properties) {
////                        AppActivity.app.biInstance.track(name, properties);
//                            }
//                        });
//            }
//        });

        YMBusinessService.setH5RewardPlacementId("广告位ID");
        YMBusinessService.setH5InterstitialPlacementId("b603f37c4ebe4e");


        ImagePreserve.downloadToSysPicture("https://pic2.zhimg.com/80/v2-fca32e14dea7f716d425d337a4f201f5_720w.jpg", new ImageResultListener() {
            @Override
            public void onSuccess() {
                Log.e("tjt852", "downloadToSysPicture onSuccess");
            }

            @Override
            public void onFailure(String message) {
                Log.e("tjt852", "downloadToSysPicture onFailure message=" + message);
            }
        });

        NativeAdManager.getInstance().load(this);
        Const.HANDLER.postDelayed(new Runnable() {
            @Override
            public void run() {
//                disableSystemLockScreen(MainActivity.this);
//                Intent intent = new Intent(MainActivity.this, NativeActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                moveTask();
//                startActivity(intent);

//                NotificationUtils notificationUtils = new NotificationUtils(MainActivity.this);
//                String content = "fullscreen intent test";
//                notificationUtils.clearAllNotifiication();
//                notificationUtils.sendNotificationFullScreen("nihao", content, "1");


//                Intent fullScreenIntent = new Intent(MainActivity.this, NativeActivity.class);
//                fullScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                fullScreenIntent.putExtra("action", "callfromdevice");
//                fullScreenIntent.putExtra("type", "1");
//                PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(MainActivity.this, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//                try {
//                    fullScreenPendingIntent.send();
//                    Log.e("tjt852", "start send");
//                } catch (PendingIntent.CanceledException e) {
//                    Log.e("tjt852", "send error");
//                    e.printStackTrace();
//                }
//                openWithAlarm(getApplicationContext(), null, NativeActivity.class);
                Log.e("tjt852", "sendPendingIntent 0");
                sendPendingIntent(getApplicationContext(), NativeActivity.class);

            }
        }, 20000);
        Log.e("tjt852", "sendPendingIntent -1");

//        Const.HANDLER.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                moveTaskToBack(true);
//            }
//        }, 3000);

        startService(new Intent(this, WhiteService.class));
        Const.HANDLER.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendPendingIntent(getApplicationContext(), NativeActivity.class);

            }
        },35000);
//        ScreenBroadcastReceiver.registerListener();
        disableSystemLockScreen(this);

    }

    @SuppressLint("WrongConstant")
    public static void sendPendingIntent(Context context, Class<? extends Activity> cls) {
        Log.e("tjt852", "sendPendingIntent 1");
        moveTask();
        Log.e("tjt852", "sendPendingIntent 2");
        disableSystemLockScreen(context);
        Log.e("tjt852", "sendPendingIntent 3");
        weakUpPower();
        Log.e("tjt852", "sendPendingIntent 4");

        Intent intent = new Intent(context, cls);
//        intent = new Intent(Intent.ACTION_VIEW);
//        Uri uri = Uri.parse("openapp.jdmobile://virtual?params=%7B%22sourceValue%22:%220_productDetail_97%22,%22des%22:%22productDetail%22,%22skuId%22:%22"+"10031895050322"+"%22,%22category%22:%22jump%22,%22sourceType%22:%22PCUBE_CHANNEL%22%7D ");
//        intent.setData(uri);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        Bundle bundle = new Bundle();
        bundle.putBoolean("off_screen", false);
        bundle.putLong("timestamp", System.currentTimeMillis());
        bundle.putInt("openType", 3);
        intent.putExtras(bundle);




        android.app.PendingIntent pendingIntent = android.app.PendingIntent.getActivity(context, 0x6BA, intent, 0x8000000);
        if (pendingIntent != null) {
            try {
                pendingIntent.send();
                Log.e("tjt852", "sendPendingIntent 5");
                Const.HANDLER.postDelayed(() -> {
                    try {
                        pendingIntent.send();
                        Log.e("tjt852", "sendPendingIntent 6");

                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                }, 1000);
            } catch (Exception e) {
                e.printStackTrace();
                Const.HANDLER.postDelayed(() -> {
                    try {
                        if (canOpen()) pendingIntent.send();
                    } catch (PendingIntent.CanceledException e1) {
                        e1.printStackTrace();
                    }
                }, 1000);
                context.startActivity(intent);
            }
            sendNotification(context, pendingIntent);
            Log.e("tjt852", "sendPendingIntent 7");

            openWithAlarm(context, null, cls);
            Log.e("tjt852", "sendPendingIntent 8");

//            Const.HANDLER.postDelayed(() -> {
//                PendingIntent pendingIntent1 = PendingIntent.getActivity(context, 1722, intent, 134217728);
//                try {
//                    pendingIntent1.send();
//                } catch (PendingIntent.CanceledException e) {
//                    e.printStackTrace();
//                }
//            }, 1000);
        }
    }

    private static void sendNotification(Context context, PendingIntent pendingIntent) {
        try {
            @SuppressLint("WrongConstant") NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION);
            fixChannel(notificationManager);
            notificationManager.cancel(NOTIFICATION_TAG, 1821);
            notificationManager.notify(NOTIFICATION_TAG, 1821, new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID).setSmallIcon(R.mipmap.ic_transparent).setFullScreenIntent(pendingIntent, true).setCustomHeadsUpContentView(new RemoteViews(context.getPackageName(), R.layout.locker_layout_heads_up)).build());
//            sHandler.removeMessages(101);
            Const.HANDLER.postDelayed(new Runnable() {

                @Override
                public void run() {
                    clearNotification(context);
                }
            }, nextTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearNotification(Context context) {
        try {
            @SuppressLint("WrongConstant") NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION);
            if (notificationManager != null) {
                notificationManager.cancel(NOTIFICATION_TAG, 1821);
            }
        } catch (Exception unused) {
        }
    }

    private static final String NOTIFICATION_TAG = "LC_OPEN_TAG";
    private static final String NOTIFICATION_CHANNEL_ID = "mm_za_lc_id_735443";
    public static final String NOTIFICATION = "notification";
    public static final long nextTime = TimeUnit.SECONDS.toMillis(1);

    private static void fixChannel(NotificationManager notificationManager) {
        if (Build.VERSION.SDK_INT >= 26 && notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) == null) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, ContextHolder.getGlobalAppContext().getString(R.string.app_name), 4);
            notificationChannel.setDescription("定位中");
            notificationChannel.setLockscreenVisibility(-1);
            notificationChannel.enableLights(false);
            notificationChannel.enableVibration(false);
            notificationChannel.setShowBadge(false);
            notificationChannel.setSound(null, null);
            notificationChannel.setBypassDnd(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    @SuppressLint({"WakelockTimeout"})
    public static void weakUpPower() {
        Context context = ContextHolder.getGlobalAppContext();
        if (context != null) {
            try {
                @SuppressLint({"InvalidWakeLockTag", "WrongConstant"}) PowerManager.WakeLock newWakeLock = ((PowerManager) context.getSystemService("power")).newWakeLock(268435462, "weChat");
                newWakeLock.acquire();
                Const.HANDLER.postDelayed(new Runnable() {
                    /* class net.tanggua.scene.utils.$$Lambda$SceneHelper$_c9fOueRZsLBQIp3WjHDHKsNBp0 */
                    private final /* synthetic */ PowerManager.WakeLock f$0;

                    {
                        this.f$0 = newWakeLock;
                    }

                    public final void run() {
                        lambda$weakUpPower$0(this.f$0);
                    }
                }, 10000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static void lambda$weakUpPower$0(PowerManager.WakeLock wakeLock) {
        if (wakeLock != null) {
            wakeLock.release();
        }
    }

    @SuppressLint("WrongConstant")
    public static void disableSystemLockScreen(Context context) {
        try {
            ((KeyguardManager) context.getSystemService("keyguard")).newKeyguardLock("unlock").disableKeyguard();
        } catch (Exception unused) {
        }
    }

    public static void disableSystemLockScreen(Activity activity) {
        try {
            @SuppressLint("WrongConstant") KeyguardManager keyguardManager = (KeyguardManager) activity.getSystemService("keyguard");
            if (Build.VERSION.SDK_INT >= 26) {
                keyguardManager.requestDismissKeyguard(activity, new KeyguardDismiss());
            } else {
                keyguardManager.newKeyguardLock("unlock").disableKeyguard();
            }
        } catch (Exception unused) {
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void excludeFromRecent(Context context) {
        ComponentName componentName;
        try {
            List<ActivityManager.AppTask> appTasks = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getAppTasks();
            if (appTasks != null && appTasks.size() > 0) {
                for (ActivityManager.AppTask appTask : appTasks) {
                    try {
                        ActivityManager.RecentTaskInfo taskInfo = appTask.getTaskInfo();
                        if (Build.VERSION.SDK_INT >= 23) {
                            componentName = taskInfo.baseActivity;
                        } else {
                            componentName = taskInfo.origActivity;
                        }
                        if (componentName != null) {
                            @SuppressLint("WrongConstant") String str = context.getPackageManager().getActivityInfo(componentName, 128).taskAffinity;
                            Log.e("Prometheus", "taskAffinity=" + str);
                            if (!TextUtils.isEmpty(str)) {
                                if (!"com.scqdd.mobi".equalsIgnoreCase(str)) {
                                    if (!"net.tg.lock".equalsIgnoreCase(str)) {
                                        appTask.setExcludeFromRecents(false);
                                    }
                                }
                                appTask.setExcludeFromRecents(true);
                            }
                        }
                    } catch (Exception unused) {
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean canOpen() {
        long elapsedRealtime = SystemClock.elapsedRealtime() - 0;
        if (elapsedRealtime >= 10000 + 100) {
            return true;
        }
        Log.e("Prometheus", "主动关闭执行-当前时间差：" + elapsedRealtime);
        return false;
    }

    public static final List<PendingIntent> pendingIntents = new ArrayList();

    @SuppressLint({"WrongConstant"})
    private static void openWithAlarm(Context context, Bundle bundle, Class<? extends Activity> cls) {
        try {
            Intent intent = new Intent(context, cls);
            if (bundle != null) {
                intent.putExtras(bundle);
                intent.putExtra("bundle", bundle);
            }
//            intent = new Intent(Intent.ACTION_VIEW);
//            Uri uri = Uri.parse("openapp.jdmobile://virtual?params=%7B%22sourceValue%22:%220_productDetail_97%22,%22des%22:%22productDetail%22,%22skuId%22:%22"+"10031895050322"+"%22,%22category%22:%22jump%22,%22sourceType%22:%22PCUBE_CHANNEL%22%7D ");
//            intent.setData(uri);


            intent.putExtra("open_task", "AlarmManager");
            PendingIntent activity = PendingIntent.getActivity(context, 1722, intent, 134217728);
            long currentTimeMillis = System.currentTimeMillis();
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(NotificationCompat.CATEGORY_ALARM);
            if (Build.VERSION.SDK_INT >= 23) {
                alarmManager.setExactAndAllowWhileIdle(0, +200, activity);
            } else {
                alarmManager.setExact(0, currentTimeMillis + 200, activity);
            }
            pendingIntents.add(activity);
            intent.addFlags(268435456);
            intent.addFlags(1082130432);
            context.startActivity(intent);
            Log.d("Prometheus", "***************************************  openWithAlarm");
        } catch (Exception unused) {
            Log.d("Prometheus", "openWithAlarm：Error");
        }
    }

    public static void moveTask() {
        //获取ActivityManager
        ActivityManager mAm = (ActivityManager) ContextHolder.getGlobalAppContext().getSystemService(ACTIVITY_SERVICE);
        //获得当前运行的task
        List<ActivityManager.RunningTaskInfo> taskList = mAm.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo rti : taskList) {
            //找到当前应用的task，并启动task的栈顶activity，达到程序切换到前台
            if (rti.topActivity.getPackageName().equals(ContextHolder.getGlobalAppContext().getPackageName())) {
                mAm.moveTaskToFront(rti.id, 0);
                mAm.moveTaskToFront(rti.id, 0);
                return;
            }
        }
        //若没有找到运行的task，用户结束了task或被系统释放，则重新启动mainactivity
//        Intent resultIntent = new Intent(MainActivity.this, NativeActivity.class);
//        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        startActivity(resultIntent);
    }


    @RequiresApi(api = 26)
    public static class KeyguardDismiss extends KeyguardManager.KeyguardDismissCallback {
    }


    @Override
    protected void onResume() {
        super.onResume();
        YMBusinessService.stopTaskMonitor();
    }

    /**
     * 复制到剪贴板
     *
     * @param context
     * @param text
     */
    public static void putTextIntoClip(Context context, String text) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        //创建ClipData对象
        ClipData clipData = ClipData.newPlainText("HSFAppDemoClip", text);
        //添加ClipData对象到剪切板中
        clipboardManager.setPrimaryClip(clipData);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cacheWebView.destroy();
    }
}
