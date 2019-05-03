package com.xs.simplehttp.api;

/**
 * interceptor
 * Created by xs code on 2019/3/13.
 */

public interface Interceptor {

    /**
     * intercept
     *
     * @param chain
     * @return
     */
    Response intercept(Chain chain);

    interface Chain {

        /**
         * request
         *
         * @return
         */
        Request request();

        /**
         * proceed
         *
         * @param request
         * @return
         */
        Response proceed(Request request);
    }
}
