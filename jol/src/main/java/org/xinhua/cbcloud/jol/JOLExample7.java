package org.xinhua.cbcloud.jol;

import java.util.concurrent.CountDownLatch;

// 性能对比轻量锁和重量锁
public class JOLExample7 {
    static CountDownLatch countDownLatch = new CountDownLatch(1000000000);
    public static void main(String[] args) throws Exception {
        final Test test = new Test();
        long start = System.currentTimeMillis();
        //调用同步方法1000000000L 来计算1000000000L的++，对比偏向锁和轻量级锁的性能
        for(int i=0; i<2; i++){
            new Thread(){
                @Override
                public void run() {
                    while (countDownLatch.getCount() > 0) {
                        test.parse();
                    }
                }
            }.start();
        }
        countDownLatch.await();
        long end = System.currentTimeMillis();
        System.out.println(String.format("%sms", end - start));
    }
}
