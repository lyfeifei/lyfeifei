package org.xinhua.cbcloud.sync;

import java.util.concurrent.locks.ReentrantLock;

public class AQS {

    final static ReentrantLock lock = new ReentrantLock(true);

    public static void main(String[] args) {
        Thread t1= new Thread("t1"){
            @Override
            public void run() {
                lock.lock();
                logic();
                lock.unlock();
            }
        };
        t1.start();
    }

    public static void logic() {

    }
}
