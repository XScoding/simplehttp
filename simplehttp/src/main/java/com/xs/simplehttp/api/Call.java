package com.xs.simplehttp.api;

/**
 * Call
 *
 * Created by xs code on 2019/3/11.
 */

public interface Call {

    /**
     * execute
     * @return response
     */
    Response execute();

    /**
     * enqueue
     * @param callback
     */
    void enqueue(Callback callback);

    /**
     * execute state
     * @return execute state
     */
    boolean isExecuted();

    /**
     * cancel
     */
    void cancel();

    /**
     * cancel state
     * @return cancel state
     */
    boolean isCanceled();

    /**
     * request
     * @return request
     */
    Request request();

}
