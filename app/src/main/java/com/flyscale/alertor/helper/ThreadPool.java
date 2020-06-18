package com.flyscale.alertor.helper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by bian on 2018/12/6.
 */

public class ThreadPool {

    public static ExecutorService getInstance() {
        return SingletonHolder.fixedThreadPool;
    }

    /**
     * 获取同步线程实例
     * @return
     */
    public static ExecutorService getSyncInstance(){
        return SingletonHolder.singleThreadPool;
    }

    private static class SingletonHolder {
        private static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
        private static ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();
    }
}
