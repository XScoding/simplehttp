package com.xs.simplehttp.util;

import android.os.Handler;
import android.os.Looper;

/**
 * thread change
 *
 * Created by xs code on 2019/1/24.
 */

public enum ThreadChange {

    INSTANCE;

    /**
     * main thread
     */
    public static final int MAIN = 1001;
    /**
     * thread pool
     */
    public static final int POOL= 1003;

    /**
     * main handle
     */
    private Handler main;

    /**
     * init
     */
    private void init() {
        if (main == null) {
            main = new Handler(Looper.getMainLooper());
        }
    }

    /**
     * execute runnable
     *
     * @param thread
     * @param runnable
     */
    public void execute(int thread,Runnable runnable) {
        init();
        if (thread == MAIN) {
            main.post(runnable);
        } else if(thread == POOL) {
            ThreadPool.INSTANCE.execute(runnable);
        }
    }

}
