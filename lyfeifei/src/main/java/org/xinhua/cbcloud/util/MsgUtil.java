package org.xinhua.cbcloud.util;

import org.junit.jupiter.api.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MsgUtil {

    class CurMsgTasks implements Runnable {
        BlockingQueue blockingQueue;
        public CurMsgTasks(BlockingQueue blockingQueue) {
            this.blockingQueue = blockingQueue;
        }
        @Override
        public void run() {
            try {
                while (!blockingQueue.isEmpty()) {
                    System.out.println("消费者线程 - " + Thread.currentThread().getName() + "消费消息：" + blockingQueue.take());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class ProMsgTasks implements Runnable {
        BlockingQueue blockingQueue;
        public ProMsgTasks(BlockingQueue blockingQueue) {
            this.blockingQueue = blockingQueue;
        }
        @Override
        public void run() {
            try {
                for (int i = 1; i < 11; i++) {
                    System.out.println("生产者线程 - " + Thread.currentThread().getName() + "生产消息：" + i);
                    blockingQueue.put("任务" + i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void MsgTasks() {
        // 任务队列
        BlockingQueue blockingQueue = new LinkedBlockingQueue<>();

        // 消息生产
        for (int i = 0; i < 1; i++) {
            ThreadUtil.getInstance().addTask(new ProMsgTasks(blockingQueue));
        }

        // 消息消费
        for (int i = 0; i < 1; i++) {
            ThreadUtil.getInstance().addTask(new CurMsgTasks(blockingQueue));
        }
    }
}
