package com.business.support.jump;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;

import com.business.support.config.Const;
import com.business.support.utils.ContextHolder;
import com.business.support.utils.SLog;


/**
 * Created by jiantao.tu on 2019-07-16.
 */
public class ScreenBroadcastReceiver extends BroadcastReceiver {

    private Context mContext;

    private ScreenStateListener mScreenStateListener;

    @SuppressLint("StaticFieldLeak")
    private static ScreenBroadcastReceiver receiver = null;

    private static boolean isScreenListener = false;

    public ScreenBroadcastReceiver(Context context) {
        mContext = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏
            mScreenStateListener.onScreenOn();
        } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
            mScreenStateListener.onScreenOff();
        } else if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁
            mScreenStateListener.onUserPresent();
        } else if (Intent.ACTION_NEW_OUTGOING_CALL.equals(action)) { // 关机
            mScreenStateListener.onOutgoingCall();
        } else if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
            mScreenStateListener.onCloseSystemDialogs();
        }
    }


    /**
     * 停止screen状态监听
     */
    public synchronized static void unregisterListener() {
        if (isScreenListener && receiver != null) {
            Context context = ContextHolder.getGlobalAppContext();
            context.unregisterReceiver(receiver);
            receiver = null;
        }
    }


    public void setScreenStateListener(ScreenStateListener screenStateListener) {
        mScreenStateListener = screenStateListener;
        getScreenState();
    }

    /**
     * 启动screen状态广播接收器
     */
    public synchronized static void registerListener() {
        if (!isScreenListener) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);//屏幕亮屏广播
            filter.addAction(Intent.ACTION_SCREEN_OFF);//屏幕灭屏广播
            filter.addAction(Intent.ACTION_USER_PRESENT);//屏幕解锁广播
            filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);//监听来电
            filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);//当长按电源键弹出“关机”对话或者锁屏时系统会发出这个广播
            Context context = ContextHolder.getGlobalAppContext();
            receiver = new ScreenBroadcastReceiver(context);
            receiver.setScreenStateListener(new ScreenStateListenerImpl());
            context.registerReceiver(receiver, filter);
            isScreenListener = true;
        }
    }

    /**
     * 获取screen状态
     */
    private void getScreenState() {
        PowerManager manager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        if (manager.isScreenOn()) {
            if (mScreenStateListener != null) {
                mScreenStateListener.onScreenOn();
            }
        } else {
            if (mScreenStateListener != null) {
                mScreenStateListener.onScreenOff();
            }
        }
    }

    private static class ScreenStateListenerImpl implements ScreenStateListener {

        @Override
        public void onScreenOn() {
            SLog.d("onScreenOn");
//            AwkAppsManager.stopTimerTask();
//            ContextHolder.getGlobalAppContext()
//                    .sendBroadcast(new Intent(OnePixelActivity.ACTION));

        }

        @Override
        public void onScreenOff() {
            SLog.d("onScreenOff");
//            BackgroundManager.screenOff();
//            AwkAppsManager.startTimerTask();
//            OnePixelActivity.launch();
        }

        @Override
        public void onUserPresent() {
            SLog.d("onUserPresent");
//            AwkAppsManager.stopTimerTask();
//            ContextHolder.getGlobalAppContext()
//                    .sendBroadcast(new Intent(OnePixelActivity.ACTION));

        }

        @Override
        public void onOutgoingCall() {
//            AwkAppsManager.stopTimerTask();
        }

        @Override
        public void onCloseSystemDialogs() {
//            AwkAppsManager.stopTimerTask();
        }
    }


    public interface ScreenStateListener {// 返回给调用者屏幕状态信息

        void onScreenOn();// 开屏

        void onScreenOff();// 锁屏

        void onUserPresent();// 解锁

        void onOutgoingCall();// 来电

        void onCloseSystemDialogs();//关机


    }

}
