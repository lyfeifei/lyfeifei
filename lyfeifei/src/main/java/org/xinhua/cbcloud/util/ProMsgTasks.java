package org.xinhua.cbcloud.util;

import org.xinhua.cbcloud.pojo.DocLog;

import java.util.concurrent.BlockingQueue;

public class ProMsgTasks implements Runnable {

    private BlockingQueue<DocLog> blockingQueue;

    DocLog docLog;

    public ProMsgTasks(BlockingQueue<DocLog> blockingQueue, DocLog docLog) {
        this.docLog = docLog;
        this.blockingQueue = blockingQueue;
    }

    @Override
    public void run() {
        try {
            // 数据入队
            for (int i = 0; i < 10000; i++) {
                blockingQueue.put(docLog);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
