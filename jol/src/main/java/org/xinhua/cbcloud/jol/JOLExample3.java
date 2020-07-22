package org.xinhua.cbcloud.jol;

import org.openjdk.jol.info.ClassLayout;
import static java.lang.System.out;

// 偏向锁
public class JOLExample3 {
    static Test test;
    public static void main(String[] args) throws InterruptedException {
        //Thread.sleep(5000);
        test = new Test();
        out.println("befre lock");
        out.println(ClassLayout.parseInstance(test).toPrintable());
        sync();
        out.println("after lock");
        out.println(ClassLayout.parseInstance(test).toPrintable());
    }

    public static void sync() throws InterruptedException {
        synchronized (test) {
            System.out.println("我也不知道要打印什么");
        }
    }
}
