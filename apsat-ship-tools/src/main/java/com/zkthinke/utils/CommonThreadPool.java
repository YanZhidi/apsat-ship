package com.zkthinke.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Title: CommonThreadPool Description: 单例线程池
 *
 * @author huqijun
 * @date 2020/1/13 19:15
 */
public final class CommonThreadPool {

    /**
     * 线程池维护线程的最少数量
     */
    private static final int SIZE_CORE_POOL = 1;

    /**
     * 线程池维护线程的最大数量
     */
    private static final int SIZE_MAX_POOL = 1;

    /**
     * ThreadPoolExecutor
     */
    private final ThreadPoolExecutor mthreadPool = new ThreadPoolExecutor(SIZE_CORE_POOL, SIZE_MAX_POOL, 30L,
            TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    /**
     * 构造方法私有化
     */
    private CommonThreadPool() {
    }

    /**
     * 内部类
     *
     */
    private static class ThreadPoolHolder {
        /**
         * 公共池
         */
        private static CommonThreadPool INSTANCE = new CommonThreadPool();
    }

    public static CommonThreadPool getInstance() {
        return ThreadPoolHolder.INSTANCE;
    }

    /**
     * 向线程池中添加任务方法
     *
     * @param task task
     */
    public void addExecuteTask(Runnable task) {
        if (task != null) {
            mthreadPool.execute(task);
        }
    }

    /**
     * 向线程池中添加任务方法,可以拿到线程的执行结果
     *
     * @param      <T> T
     * @param task task
     * @return T
     */
    public <T> Future<T> addExecuteTask(Callable<T> task) {
        if (task != null) {
            Future<T> future = mthreadPool.submit(task);
            return future;
        }
        return null;
    }

    /**
     * 销毁
     */
    public void destroy() {
        mthreadPool.shutdown();
    }
}
