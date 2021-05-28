package com.business.support.compose;

import android.content.Context;

import com.business.support.config.Const;
import com.business.support.shuzilm.ShuzilmImpl;
import com.business.support.smsdk.SmeiImpl;
import com.business.support.utils.SLog;
import com.business.support.utils.ThreadPoolProxy;
import com.business.support.utils.Utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

public class SdkTaskManager {


    private static final SdkTaskManager sdkTaskManager = new SdkTaskManager();

    private final Queue<TaskSdk> sdkQueue = new ArrayDeque<>(2);

    private final Map<SdkType, TaskResult> taskResults = new HashMap<>(2);


    private Map<SdkType, TimeOutRunnable> timeOutRunnableMap = new HashMap<>();

    private ZipSidListener mListener;

    private CountDownLatch countDownLatch;

    private int taskCount = 0;

    static class TaskSdk {

        private final ISdkMain sdkMain;

        private final SdkType sdkType;

        private final long reqInterval;

        private final long timeOut;

        private final String[] params;

        public TaskSdk(ISdkMain sdkMain, SdkType sdkType, long reqInterval, long timeOut, String... params) {
            this.sdkMain = sdkMain;
            this.timeOut = timeOut;
            this.params = params;
            this.sdkType = sdkType;
            this.reqInterval = reqInterval;
        }
    }


    private SdkTaskManager() {

    }

    public static SdkTaskManager getInstance() {
        return sdkTaskManager;
    }


    public SdkTaskManager add(ISdkMain sdkMain, long reqInterval, long timeOut, String... params) {
        sdkQueue.offer(new TaskSdk(sdkMain, getSdkType(sdkMain), reqInterval, timeOut, params));
        return this;
    }


    public void zip(Context context, ZipSidListener listener) {
        if (!context.getPackageName().equals(Utils.getCurProcessName(context))) {
            SLog.d("multi process init is not allowed. error");
            sdkQueue.clear();
            return;
        }

        this.mListener = listener;
        TaskSdk taskSdk;

        while ((taskSdk = sdkQueue.poll()) != null) {
            if (!taskSdk.sdkMain.init(context, taskSdk.params)) continue;
            Const.HANDLER.postDelayed(new TaskRunnable(taskSdk), taskSdk.reqInterval);
            TimeOutRunnable timeOutRunnable = new TimeOutRunnable(taskSdk.sdkType);
            timeOutRunnableMap.put(taskSdk.sdkType, timeOutRunnable);
            Const.HANDLER.postDelayed(timeOutRunnable, taskSdk.timeOut);
            taskCount++;
        }

        countDownLatch = new CountDownLatch(taskCount);
        ThreadPoolProxy.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    countDownLatch.await();
                    notifyListener();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void notifyListener() {
        Const.HANDLER.post(new Runnable() {
            @Override
            public void run() {
                taskCount = 0;
                if (mListener != null) {
                    mListener.result(taskResults.values());
                }
            }
        });
    }


    class TimeOutRunnable implements Runnable {

        private final SdkType mSdkType;

        public TimeOutRunnable(SdkType sdkType) {
            mSdkType = sdkType;
        }

        @Override
        public void run() {
            if (taskResults.size() == taskCount) {
                return;
            }

            if (taskResults.get(mSdkType) != null) return;

            taskResults.put(mSdkType, new TaskResult(true, 0, "time out", mSdkType));
            if (countDownLatch != null) {
                countDownLatch.countDown();
            }
        }
    }


    class TaskRunnable implements Runnable {

        private final TaskSdk mTaskSdk;

        public TaskRunnable(TaskSdk taskSdk) {
            mTaskSdk = taskSdk;
        }

        @Override
        public void run() {
            mTaskSdk.sdkMain.requestQuery(new TaskResultListener() {

                @Override
                public void result(TaskResult taskResult) {
                    Const.HANDLER.removeCallbacks(timeOutRunnableMap.remove(mTaskSdk.sdkType));

                    if (taskResults.get(taskResult.getSdkType()) != null) return;

                    taskResults.put(taskResult.getSdkType(), taskResult);
                    if (countDownLatch != null) {
                        countDownLatch.countDown();
                    }
                }

            });
        }
    }


    public SdkType getSdkType(ISdkMain sdkMain) {
        if (sdkMain instanceof ShuzilmImpl) {
            return SdkType.SHUMENG;
        }

        if (sdkMain instanceof SmeiImpl) {
            return SdkType.SHUMEI;
        }

        return SdkType.OTHER;
    }
}
