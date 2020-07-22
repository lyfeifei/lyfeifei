package org.xinhua.cbcloud.jol;

import org.openjdk.jol.info.ClassLayout;
import static java.lang.System.out;

// 如果调用wait方法则立刻变成重量锁
public class JOLExample8 {
    static Test test;

    public static void main(String[] args) throws Exception {
        //Thread.sleep(5000);
        test = new Test();
        out.println("befre lock");
        out.println(ClassLayout.parseInstance(test).toPrintable());
        Thread t1 = new Thread() {
            public void run() {
                synchronized (test) {
                    try {
                        synchronized (test) {
                            System.out.println("before wait");
                            out.println(ClassLayout.parseInstance(test).toPrintable());
                            test.wait();
                            System.out.println(" after wait");
                            out.println(ClassLayout.parseInstance(test).toPrintable());
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t1.start();
        Thread.sleep(5000);
        synchronized (test) {
            test.notifyAll();
        }
    }

    public static void sync() throws InterruptedException {
        synchronized (test) {
            System.out.println("t1 main lock");
            out.println(ClassLayout.parseInstance(test).toPrintable());
        }
    }
}
