package org.xinhua.cbcloud.sync;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Demo2 {
    volatile List lists = new ArrayList();

    public void add(Object o){
        lists.add(o);
    }

    public int size(){
        return lists.size();
    }

    public static void main(String[] args) {
        Demo2 c = new Demo2();

        CountDownLatch latch = new CountDownLatch(5);

        new Thread(()->{
            System.out.println("t2启动");
            if (c.size() != 5) {
                try {
                    latch.await();//准备
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("t2结束");
            }
        }," t2").start();

        new Thread(()->{
            System.out.println("t1启动");
            for (int i = 0; i < 10; i++) {
                c.add(new Object());
                System.out.println("add " + i);
                if (c.size() == 5) {

                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "t1").start();
    }
}
