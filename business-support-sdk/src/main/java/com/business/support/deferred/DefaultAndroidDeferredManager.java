package com.business.support.deferred;

import com.business.support.utils.ThreadPoolProxy;

import java.util.concurrent.ExecutorService;

public class DefaultAndroidDeferredManager extends DefaultDeferredManager {

    public DefaultAndroidDeferredManager() {
        super(ThreadPoolProxy.getInstance().getThreadPoolExecutor());
    }

    public DefaultAndroidDeferredManager(ExecutorService executorService) {
        super(executorService);
    }

    /**
     * Wrap with {@link AndroidDeferredObject} so that callbacks can be executed in UI thread.
     */
    @Override
    public <D, P> Promise<D, Throwable, P> when(DeferredFutureTask<D, P> task) {
        return new AndroidDeferredObject<D, Throwable, P>(super.when(task)).promise();
    }

}
