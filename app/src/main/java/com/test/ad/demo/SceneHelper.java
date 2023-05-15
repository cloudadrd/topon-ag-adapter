package com.test.ad.demo;

import static android.content.Context.ACTIVITY_SERVICE;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.business.support.utils.ContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SceneHelper {

    private final static String TAG = "SceneHelper";

    public static final Handler sHandler = new Handler(Looper.getMainLooper()) {

        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 101) {
                clearNotification(ContextHolder.getGlobalAppContext());
            } else if (message.what == 102) {
                if (message.obj != null) {
                    sendPendingIntent((PendingIntent) message.obj);
                }
            } else if (message.what == 103) {
                SystemClock.elapsedRealtime();
            }
        }
    };

    public static void delayPending(PendingIntent pendingIntent) {
        if (pendingIntent != null) {
            sHandler.removeMessages(102);
            Message obtain = Message.obtain();
            obtain.what = 102;
            obtain.obj = pendingIntent;
            sHandler.sendMessageDelayed(obtain, 1000);
        }
    }

    public static long lastCloseTime = 0;

    public static void sendPendingIntent(PendingIntent pendingIntent) {
        if (pendingIntent != null) {
            try {
                long elapsedRealtime = SystemClock.elapsedRealtime() - lastCloseTime;
                if (canOpen()) {
                    pendingIntent.send();
                }
                Log.i(TAG, "sendPendingIntent  : PendingIntent  Send 时间差: " + elapsedRealtime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("WrongConstant")
    public static void openActivity(Context context, Intent intent) {
        closeAlarm();
        Log.i(TAG, "sendPendingIntent 1");
        moveTask();
        Log.i(TAG, "sendPendingIntent 2");

        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0x6BA,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        if (pendingIntent != null) {
            try {
                pendingIntent.send();
                Log.i(TAG, "sendPendingIntent 5");
                delayPending(pendingIntent);
            } catch (Exception e) {
                e.printStackTrace();
                delayPending(pendingIntent);
                context.startActivity(intent);
            }
            if (Build.VERSION.SDK_INT > 28) {
                sendNotification(context, pendingIntent);
                Log.i(TAG, "sendPendingIntent 7");

                openWithAlarm(context, intent);
                Log.i(TAG, "sendPendingIntent 8");
            }

        }
    }

    private static void sendNotification(Context context, PendingIntent pendingIntent) {
        try {
            @SuppressLint("WrongConstant") NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            fixChannel(notificationManager);
            notificationManager.cancel(NOTIFICATION_TAG, 1821);
            notificationManager.notify(NOTIFICATION_TAG, 1821, new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID).setSmallIcon(R.mipmap.ic_transparent).setFullScreenIntent(pendingIntent, true).setCustomHeadsUpContentView(new RemoteViews(context.getPackageName(), R.layout.locker_layout_heads_up)).build());
            sHandler.removeMessages(101);
            sHandler.sendEmptyMessageDelayed(101, nextTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearNotification(Context context) {
        try {
            @SuppressLint("WrongConstant") NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.cancel(NOTIFICATION_TAG, 1821);
            }
        } catch (Exception unused) {
        }
    }

    private static final String NOTIFICATION_TAG = "GREET_OPEN_TAG";
    private static final String NOTIFICATION_CHANNEL_ID = "easeutility_greet_id_735443";
    public static final long nextTime = TimeUnit.SECONDS.toMillis(1);

    private static void fixChannel(NotificationManager notificationManager) {
        if (Build.VERSION.SDK_INT >= 26 && notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) == null) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, getAppName(ContextHolder.getGlobalAppContext()), 4);
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


    public static boolean canOpen() {
        long elapsedRealtime = SystemClock.elapsedRealtime() - lastCloseTime;
        if (elapsedRealtime >= 10000 + 100) {
            return true;
        }
        Log.i(TAG, "主动关闭执行-当前时间差：" + elapsedRealtime);
        return false;
    }


    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static final List<PendingIntent> pendingIntents = new ArrayList<>();

    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.M)
    private static void openWithAlarm(Context context, Intent intent) {
        try {

            PendingIntent activity = PendingIntent.getActivity(context, 1722, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            long currentTimeMillis = System.currentTimeMillis();
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(NotificationCompat.CATEGORY_ALARM);
            if (Build.VERSION.SDK_INT >= 23) {
                alarmManager.setExactAndAllowWhileIdle(0, +200, activity);
            } else {
                alarmManager.setExact(0, currentTimeMillis + 200, activity);
            }
            pendingIntents.add(activity);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(1082130432);
            context.startActivity(intent);
            Log.i(TAG, "***************************************  openWithAlarm");
        } catch (Exception unused) {
            Log.d(TAG, "openWithAlarm：Error");
        }
    }

    public static void closeAlarm() {
        try {
            if (pendingIntents.size() > 0) {
                for (PendingIntent pendingIntent : pendingIntents) {
                    closeAlarm(pendingIntent);
                }
                pendingIntents.clear();
            }
        } catch (Exception e) {
            Log.d(TAG, "AlarmManager closeAlarm : " + e.toString());
        }
    }

    public static void closeAlarm(PendingIntent pendingIntent) {
        @SuppressLint("WrongConstant") AlarmManager alarmManager = (AlarmManager) ContextHolder.getGlobalAppContext().getSystemService(NotificationCompat.CATEGORY_ALARM);
        if (pendingIntent != null) {
            try {
                alarmManager.cancel(pendingIntent);
                Log.d(TAG, "AlarmManager cancel : success");
            } catch (Exception e) {
                Log.d(TAG, "AlarmManager cancel : " + e.toString());
            }
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
    }

}
