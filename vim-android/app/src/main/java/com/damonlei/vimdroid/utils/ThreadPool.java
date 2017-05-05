package com.damonlei.vimdroid.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author damonlei
 * @time 2017/5/5
 * @email danxionglei@foxmail.com
 */
public class ThreadPool {

    private static ExecutorService threadPool = Executors.newCachedThreadPool();

    public static void post(Runnable runnable) {
        threadPool.submit(runnable);
    }

    public static void destroy() {
        threadPool.shutdownNow();
        threadPool = null;
    }
}
