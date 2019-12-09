package org.xinhua.cbcloud.util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadUtil {

    private ThreadPoolExecutor threadPool;

    private static ThreadUtil INSTANCE = null;

    public ThreadUtil() {
        threadPool = new ThreadPoolExecutor(10, 20, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    }

    public static ThreadUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ThreadUtil();
        }
        return INSTANCE;
    }

    public void addTask(Runnable runTask) {
        threadPool.execute(runTask);
    }

    public void stop() {
        threadPool.shutdown();
        threadPool.getQueue().clear();
    }

}
