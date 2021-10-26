/*
 * Copyright 2013-2018 Ray Tsang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.business.support.deferred;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Ray Tsang
 * @see Promise
 */
public abstract class AbstractPromise<D, F, P> implements Promise<D, F, P> {

    protected volatile State state = State.PENDING;

    protected final List<DoneCallback<? super D>> doneCallbacks = new CopyOnWriteArrayList<DoneCallback<? super D>>();
    protected final List<FailCallback<? super F>> failCallbacks = new CopyOnWriteArrayList<FailCallback<? super F>>();
    protected final List<ProgressCallback<? super P>> progressCallbacks = new CopyOnWriteArrayList<ProgressCallback<? super P>>();
    protected final List<AlwaysCallback<? super D, ? super F>> alwaysCallbacks = new CopyOnWriteArrayList<AlwaysCallback<? super D, ? super F>>();

    protected D resolveResult;
    protected F rejectResult;

    @Override
    public State state() {
        return state;
    }

    @Override
    public Promise<D, F, P> done(DoneCallback<? super D> callback) {
        synchronized (this) {
            if (isResolved()) {
                triggerDone(callback, resolveResult);
            } else {
                doneCallbacks.add(callback);
            }
        }
        return this;
    }

    @Override
    public Promise<D, F, P> fail(FailCallback<? super F> callback) {
        synchronized (this) {
            if (isRejected()) {
                triggerFail(callback, rejectResult);
            } else {
                failCallbacks.add(callback);
            }
        }
        return this;
    }

    protected void triggerDone(D resolved) {
        for (DoneCallback<? super D> callback : doneCallbacks) {
            triggerDone(callback, resolved);
        }
        doneCallbacks.clear();
    }

    protected void triggerDone(DoneCallback<? super D> callback, D resolved) {
        try {
            callback.onDone(resolved);
        } catch (Exception e) {
            handleException(CallbackExceptionHandler.CallbackType.DONE_CALLBACK, e);
        }
    }

    protected void triggerFail(F rejected) {
        for (FailCallback<? super F> callback : failCallbacks) {
            triggerFail(callback, rejected);
        }
        failCallbacks.clear();
    }

    protected void triggerFail(FailCallback<? super F> callback, F rejected) {
        try {
            callback.onFail(rejected);
        } catch (Exception e) {
            handleException(CallbackExceptionHandler.CallbackType.FAIL_CALLBACK, e);
        }
    }

    protected void triggerProgress(P progress) {
        for (ProgressCallback<? super P> callback : progressCallbacks) {
            triggerProgress(callback, progress);
        }
    }

    protected void triggerProgress(ProgressCallback<? super P> callback, P progress) {
        try {
            callback.onProgress(progress);
        } catch (Exception e) {
            handleException(CallbackExceptionHandler.CallbackType.PROGRESS_CALLBACK, e);
        }
    }

    protected void triggerAlways(State state, D resolve, F reject) {
        for (AlwaysCallback<? super D, ? super F> callback : alwaysCallbacks) {
            triggerAlways(callback, state, resolve, reject);
        }
        alwaysCallbacks.clear();

        synchronized (this) {
            this.notifyAll();
        }
    }

    protected void triggerAlways(AlwaysCallback<? super D, ? super F> callback, State state, D resolve, F reject) {
        try {
            callback.onAlways(state, resolve, reject);
        } catch (Exception e) {
            handleException(CallbackExceptionHandler.CallbackType.ALWAYS_CALLBACK, e);
        }
    }

    @Override
    public Promise<D, F, P> progress(ProgressCallback<? super P> callback) {
        progressCallbacks.add(callback);
        return this;
    }

    @Override
    public Promise<D, F, P> then(DoneCallback<? super D> callback) {
        return done(callback);
    }

    @Override
    public Promise<D, F, P> then(DoneCallback<? super D> doneCallback, FailCallback<? super F> failCallback) {
        done(doneCallback);
        fail(failCallback);
        return this;
    }

    @Override
    public Promise<D, F, P> then(DoneCallback<? super D> doneCallback, FailCallback<? super F> failCallback,
                                 ProgressCallback<? super P> progressCallback) {
        done(doneCallback);
        fail(failCallback);
        progress(progressCallback);
        return this;
    }


    @Override
    public boolean isPending() {
        return state == State.PENDING;
    }

    @Override
    public boolean isResolved() {
        return state == State.RESOLVED;
    }

    @Override
    public boolean isRejected() {
        return state == State.REJECTED;
    }

    public void waitSafely() throws InterruptedException {
        waitSafely(-1);
    }

    public void waitSafely(long timeout) throws InterruptedException {
        final long startTime = System.currentTimeMillis();
        synchronized (this) {
            while (this.isPending()) {
                try {
                    if (timeout <= 0) {
                        wait();
                    } else {
                        final long elapsed = (System.currentTimeMillis() - startTime);
                        final long waitTime = timeout - elapsed;
                        wait(waitTime);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw e;
                }

                if (timeout > 0 && ((System.currentTimeMillis() - startTime) >= timeout)) {
                    return;
                } else {
                    continue; // keep looping
                }
            }
        }
    }

    protected void handleException(CallbackExceptionHandler.CallbackType callbackType, Exception e) {
        GlobalConfiguration.getGlobalCallbackExceptionHandler().handleException(callbackType, e);
    }
}
