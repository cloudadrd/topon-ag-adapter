package com.business.support.deferred;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public abstract class DefaultDeferredManager implements DeferredManager  {

    /**
     * By default, {@link #autoSubmit} will be set to true
     * You can set it to false by using {@link #setAutoSubmit(boolean)}
     * If you set it to false, that means you'll be responsible to make sure any
     * {@link Runnable} or {@link Callable} are executed.
     */
    public static final boolean DEFAULT_AUTO_SUBMIT = true;

    private final ExecutorService executorService;
    private boolean autoSubmit = DEFAULT_AUTO_SUBMIT;

    /**
     * Equivalent to {@link #DefaultDeferredManager(ExecutorService)} using
     * {@link Executors#newCachedThreadPool()}
     */
    public DefaultDeferredManager() {
        this.executorService = Executors.newCachedThreadPool();
    }

    /**
     * @param executorService
     */
    public DefaultDeferredManager(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public boolean awaitTermination(long timeout, TimeUnit unit)
            throws InterruptedException {
        return executorService.awaitTermination(timeout, unit);
    }

    public boolean isShutdown() {
        return executorService.isShutdown();
    }

    public boolean isTerminated() {
        return executorService.isTerminated();
    }

    public void shutdown() {
        executorService.shutdown();
    }

    public List<Runnable> shutdownNow() {
        return executorService.shutdownNow();
    }

    protected void submit(Runnable runnable) {
        executorService.submit(runnable);
    }

    protected void submit(Callable<?> callable) {
        executorService.submit(callable);
    }

    public boolean isAutoSubmit() {
        return autoSubmit;
    }

    public void setAutoSubmit(boolean autoSubmit) {
        this.autoSubmit = autoSubmit;
    }


    @Override
    public <D, F, P> Promise<D, F, P> when(Promise<D, F, P> promise) {
        assertNotNull(promise, "promise");
        return promise;
    }

    @Override
    public <P> Promise<Void, Throwable, P> when(DeferredRunnable<P> runnable) {
        assertNotNull(runnable, "runnable");
        return when(new DeferredFutureTask<Void, P>(runnable));
    }

    @Override
    public <D, P> Promise<D, Throwable, P> when(DeferredCallable<D, P> callable) {
        assertNotNull(callable, "callable");
        return when(new DeferredFutureTask<D, P>(callable));
    }

    @Override
    public Promise<Void, Throwable, Void> when(Runnable runnable) {
        assertNotNull(runnable, "runnable");
        return when(new DeferredFutureTask<Void, Void>(runnable));
    }

    @Override
    public <D> Promise<D, Throwable, Void> when(Callable<D> callable) {
        assertNotNull(callable, "callable");
        return when(new DeferredFutureTask<D, Void>(callable));
    }

    @Override
    public <D, P> Promise<D, Throwable, P> when(DeferredFutureTask<D, P> task) {
        assertNotNull(task, "task");
        if (task.getStartPolicy() == StartPolicy.AUTO
                || (task.getStartPolicy() == StartPolicy.DEFAULT && isAutoSubmit())) {
            submit(task);
        }

        return task.promise();
    }

    @Override
    public <D> Promise<D, Throwable, Void> when(Future<D> future) {
        // make sure the task is automatically started
        return when(deferredCallableFor(future));
    }



    protected void assertNotNull(Object object, String name) {
        if (object == null) {
            throw new IllegalArgumentException("Argument '" + name + "' must not be null");
        }
    }

    protected <D> DeferredCallable<D, Void> deferredCallableFor(final Future<D> future) {
        assertNotNull(future, "future");

        return new DeferredCallable<D, Void>(StartPolicy.AUTO) {
            @Override
            public D call() throws Exception {
                try {
                    return future.get();
                } catch (InterruptedException e) {
                    throw e;
                } catch (ExecutionException e) {
                    if (e.getCause() instanceof Exception) {
                        throw (Exception) e.getCause();
                    } else {
                        throw e;
                    }
                }
            }
        };
    }
}
