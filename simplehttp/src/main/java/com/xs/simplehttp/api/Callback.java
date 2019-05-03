package com.xs.simplehttp.api;


import com.xs.simplehttp.core.HttpException;

/**
 * callback
 *
 * Created by xs code on 2019/3/11.
 */

public interface Callback {

    /**
     * success
     * @param response
     */
    void onResponse(Response response);

    /**
     * fail
     * @param e
     */
    void onFailure(HttpException e);
}
