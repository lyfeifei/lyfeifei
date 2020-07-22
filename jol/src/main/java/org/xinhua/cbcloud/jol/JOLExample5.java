package org.xinhua.cbcloud.jol;

import org.openjdk.jol.info.ClassLayout;
import static java.lang.System.out;

// 重量级锁
public class JOLExample5 {
    static Test test;
    public static void main(String[] args) throws Exception {
        test = new Test();
        out.println("befre lock");
        out.println(ClassLayout.parseInstance(test).toPrintable());//无锁

        Thread t1= new Thread(){
            public void run() {
                synchronized (test){
                    try {
                        Thread.sleep(5000);
                        System.out.println("t1 release");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t1.start();
        Thread.sleep(1000);
        out.println("t1 lock ing");
        out.println(ClassLayout.parseInstance(test).toPrintable());//轻量锁
        sync();
        out.println("after lock");
        out.println(ClassLayout.parseInstance(test).toPrintable());//重量锁
        System.gc();
        out.println("after gc()");
        out.println(ClassLayout.parseInstance(test).toPrintable());//无锁---gc
    }

    public  static  void sync() throws InterruptedException {
        synchronized (test){
            System.out.println("t1 main lock");
            out.println(ClassLayout.parseInstance(test).toPrintable());//重量锁
        }
    }
}
