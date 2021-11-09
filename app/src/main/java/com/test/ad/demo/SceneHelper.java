package com.test.ad.demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.business.support.config.Const;
import com.business.support.utils.ContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SceneHelper {

    @SuppressLint("WrongConstant")
    public static void sendPendingIntent(Context context, Class<? extends Activity> cls) {
//        disableSystemLockScreen(context);
//        weakUpPower();
        Intent intent = new Intent(context, cls);
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
                Const.HANDLER.postDelayed(() -> {
                    try {
                        pendingIntent.send();
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
            openWithAlarm(context, null, cls);

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

    @RequiresApi(api = 26)
    public static class KeyguardDismiss extends KeyguardManager.KeyguardDismissCallback {
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
}
