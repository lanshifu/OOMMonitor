package com.lanshifu.asm_plugin_library.thread;

import android.util.Log;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lanxiaobin
 * @date 2021/12/26
 */
class CustomThreadPool {

    private static final RejectedExecutionHandler sRunOnSerialPolicy =
            (r, e) -> {
                Log.w("ShadowThread", "Exceeded ThreadPoolExecutor pool size");
                //队列满了，兜底，创建新线程处理
                new Thread(r).start();
            };
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "CustomThreadPool #" + mCount.getAndIncrement());
        }
    };
    public static final Executor THREAD_POOL_EXECUTOR;

    static {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                0, 128, 60, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(), sThreadFactory);
        threadPoolExecutor.setRejectedExecutionHandler(sRunOnSerialPolicy);
        THREAD_POOL_EXECUTOR = threadPoolExecutor;
    }


}
