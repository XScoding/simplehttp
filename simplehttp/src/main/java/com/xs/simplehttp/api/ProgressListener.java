package com.xs.simplehttp.api;

/**
 * progress listener
 *
 * Created by xs code on 2019/3/12.
 */

public interface ProgressListener {

    /**
     * progress
     *
     * @param p
     * @param total
     */
    void onProgress(float p, long total);
}
