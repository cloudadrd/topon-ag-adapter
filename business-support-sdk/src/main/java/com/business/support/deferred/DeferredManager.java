package com.business.support.deferred;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface DeferredManager {
    enum StartPolicy {
        /**
         * Let Deferred Manager to determine whether to start the task at its own
         * discretion.
         */
        DEFAULT,

        /**
         * Tells Deferred Manager to automatically start the task
         */
        AUTO,

        /**
         * Tells Deferred Manager that this task will be manually started
         */
        MANUAL
    }

    /**
     * Simply returns the promise.
     *
     * @param promise
     *
     * @return promise
     */
    <D, F, P> Promise<D, F, P> when(Promise<D, F, P> promise);

    /**
     * Wraps {@link Runnable} with {@link DeferredFutureTask}.
     *
     * @param runnable
     *
     * @return {@link #when(DeferredFutureTask)}
     *
     * @see #when(DeferredFutureTask)
     */
    Promise<Void, Throwable, Void> when(Runnable runnable);

    /**
     * Wraps {@link Callable} with {@link DeferredFutureTask}
     *
     * @param callable
     *
     * @return {@link #when(DeferredFutureTask)}
     *
     * @see #when(DeferredFutureTask)
     */
    <D> Promise<D, Throwable, Void> when(Callable<D> callable);

    /**
     * Wraps {@link Future} and waits for {@link Future#get()} to return a result
     * in the background.
     *
     * @param future
     *
     * @return {@link #when(Callable)}
     */
    <D> Promise<D, Throwable, Void> when(Future<D> future);

    /**
     * Wraps {@link DeferredRunnable} with {@link DeferredFutureTask}
     *
     * @param runnable
     *
     * @return {@link #when(DeferredFutureTask)}
     *
     * @see #when(DeferredFutureTask)
     */
    <P> Promise<Void, Throwable, P> when(
            DeferredRunnable<P> runnable);

    /**
     * Wraps {@link DeferredCallable} with {@link DeferredFutureTask}
     *
     * @param callable
     *
     * @return {@link #when(DeferredFutureTask)}
     *
     * @see #when(DeferredFutureTask)
     */
    <D, P> Promise<D, Throwable, P> when(
            DeferredCallable<D, P> callable);

    /**
     * May or may not submit {@link DeferredFutureTask} for execution. See
     * implementation documentation.
     *
     * @param task
     *
     * @return {@link DeferredFutureTask#promise()}
     */
    <D, P> Promise<D, Throwable, P> when(
            DeferredFutureTask<D, P> task);
}
