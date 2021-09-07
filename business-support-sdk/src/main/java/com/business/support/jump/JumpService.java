package com.business.support.jump;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.business.support.config.Const;
import com.business.support.utils.ContextHolder;

public class JumpService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("tjt852", "JumpService 进来了");
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationUtils notificationUtils = new NotificationUtils(this);
            String content = "fullscreen intent test";
            notificationUtils.clearAllNotifiication();
            notificationUtils.sendNotificationFullScreen("nihao", content, "1");
        } else {
            Intent intent1 = new Intent(JumpService.this, NativeActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ContextHolder.getGlobalAppContext().startActivity(intent1);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressLint({"WrongConstant"})
    private static void openWithAlarm(Context context, Bundle bundle, Class<? extends Activity> cls) {
        try {
            Intent intent = new Intent(context, cls);
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            intent.putExtra("open_task", "AlarmManager");
            final PendingIntent activity = PendingIntent.getActivity(context, 1722, intent, 134217728);
            long currentTimeMillis = System.currentTimeMillis();
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(NotificationCompat.CATEGORY_ALARM);
            if (Build.VERSION.SDK_INT >= 23) {
                alarmManager.setExactAndAllowWhileIdle(0, currentTimeMillis + 200, activity);
            } else {
                alarmManager.setExact(0, currentTimeMillis + 200, activity);
            }
//            pendingIntents.add(activity);
            Const.HANDLER.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        activity.send();
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                }
            }, 3000);
            intent.addFlags(268435456);
            intent.addFlags(1082130432);
            context.startActivity(intent);
            Log.d("Prometheus", "***************************************  openWithAlarm");
        } catch (Exception unused) {
            Log.d("Prometheus", "openWithAlarm：Error");
        }
    }
}
