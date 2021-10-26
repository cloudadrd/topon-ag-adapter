package com.business.support.deferred;


/**
 *
 * @param <D> Type used for {@link #done(DoneCallback)}
 * @param <F> Type used for {@link #fail(FailCallback)}
 * @param <P> Type used for {@link #progress(ProgressCallback)}
 */
public interface Promise<D, F, P>  {

    enum State {
        /**
         * The Promise is still pending - it could be created, submitted for execution,
         * or currently running, but not yet finished.
         */
        PENDING,

        /**
         * The Promise has finished running and a failure occurred.
         * Thus, the Promise is rejected.
         *
         * @see Deferred#reject(Object)
         */
        REJECTED,

        /**
         * The Promise has finished running successfully.
         * Thus, the Promise is resolved.
         *
         * @see Deferred#resolve(Object)
         */
        RESOLVED
    }

    /**
     * @return the state of this promise.
     */
    State state();

    /**
     * Queries the state of this promise, returning {@code true} iff it is {@code State.PENDING}.
     *
     * @see State#PENDING
     * @return {@code true} if the current state of this promise is {@code State.PENDING}, {@code false} otherwise.
     */
    boolean isPending();

    /**
     * Queries the state of this promise, returning {@code true} iff it is {@code State.RESOLVED}.
     *
     * @see State#RESOLVED
     * @return {@code true} if the current state of this promise is {@code State.RESOLVED}, {@code false} otherwise.
     */
    boolean isResolved();

    /**
     * Queries the state of this promise, returning {@code true} iff it is {@code State.REJECTED}.
     *
     * @see State#REJECTED
     * @return {@code true} if the current state of this promise is {@code State.REJECTED}, {@code false} otherwise.
     */
    boolean isRejected();


    /**
     * Equivalent to {@link #done(DoneCallback)}
     *
     * @param doneCallback see {@link #done(DoneCallback)}
     * @return {@code this} for chaining more calls
     */
    Promise<D, F, P> then(DoneCallback<? super D> doneCallback);

    /**
     * Equivalent to {@link #done(DoneCallback)}.{@link #fail(FailCallback)}
     *
     * @param doneCallback see {@link #done(DoneCallback)}
     * @param failCallback see {@link #fail(FailCallback)}
     * @return {@code this} for chaining more calls
     */
    Promise<D, F, P> then(DoneCallback<? super D> doneCallback, FailCallback<? super F> failCallback);

    /**
     * Equivalent to {@link #done(DoneCallback)}.{@link #fail(FailCallback)}.{@link #progress(ProgressCallback)}
     *
     * @param doneCallback see {@link #done(DoneCallback)}
     * @param failCallback see {@link #fail(FailCallback)}
     * @param progressCallback see {@link #progress(ProgressCallback)}
     * @return {@code this} for chaining more calls
     */
    Promise<D, F, P> then(DoneCallback<? super D> doneCallback,
                          FailCallback<? super F> failCallback, ProgressCallback<? super P> progressCallback);

    /**
     * This method will register {@link DoneCallback} so that when a Deferred object
     * is resolved ({@link Deferred#resolve(Object)}), {@link DoneCallback} will be triggered.
     * If the Deferred object is already resolved then the {@link DoneCallback} is triggered immediately.
     *
     * You can register multiple {@link DoneCallback} by calling the method multiple times.
     * The order of callback trigger is based on the order they have been registered.
     *
     * <pre>
     * <code>
     * promise.progress(new DoneCallback(){
     *   public void onDone(Object done) {
     *     ...
     *   }
     * });
     * </code>
     * </pre>
     *
     * @see Deferred#resolve(Object)
     * @param callback the callback to be triggered
     * @return {@code this} for chaining more calls
     */
    Promise<D, F, P> done(DoneCallback<? super D> callback);

    /**
     * This method will register {@link FailCallback} so that when a Deferred object
     * is rejected ({@link Deferred#reject(Object)}), {@link FailCallback} will be triggered.
     * If the Deferred object is already rejected then the {@link FailCallback} is triggered immediately.
     *
     * You can register multiple {@link FailCallback} by calling the method multiple times.
     * The order of callback trigger is based on the order they have been registered.
     *
     * <pre>
     * <code>
     * promise.fail(new FailCallback(){
     *   public void onFail(Object rejection) {
     *     ...
     *   }
     * });
     * </code>
     * </pre>
     *
     * @see Deferred#reject(Object)
     * @param callback the callback to be triggered
     * @return {@code this} for chaining more calls
     */
    Promise<D, F, P> fail(FailCallback<? super F> callback);


    /**
     * This method will register {@link ProgressCallback} so that when a Deferred object
     * is notified of progress ({@link Deferred#notify(Object)}), {@link ProgressCallback} will be triggered.
     *
     * You can register multiple {@link ProgressCallback} by calling the method multiple times.
     * The order of callback trigger is based on the order they have been registered.
     *
     * <pre>
     * <code>
     * promise.progress(new ProgressCallback(){
     *   public void onProgress(Object progress) {
     *     // e.g., update progress in the GUI while the background task is still running.
     *   }
     * });
     * </code>
     * </pre>
     *
     * @see Deferred#notify(Object)
     * @param callback the callback to be triggered
     * @return {@code this} for chaining more calls
     */
    Promise<D, F, P> progress(ProgressCallback<? super P> callback);

    /**
     * This method will wait as long as the State is Pending.  This method will return fast
     * when State is not Pending.
     *
     * @throws InterruptedException if thread is interrupted while waiting
     */
    void waitSafely() throws InterruptedException;

    /**
     * This method will wait when the State is Pending, and return when timeout has reached.
     * This method will return fast when State is not Pending.
     *
     * @param timeout the maximum time to wait in milliseconds
     * @throws InterruptedException if thread is interrupted while waiting
     */
    void waitSafely(long timeout) throws InterruptedException;
}
