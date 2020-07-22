package org.xinhua.cbcloud.jol;

import org.openjdk.jol.info.ClassLayout;
import static java.lang.System.out;

// 轻量级锁
public class JOLExample4 {
    static Test test;
    public static void main(String[] args) {
        test = new Test();
        out.println("befre lock");
        out.println(ClassLayout.parseInstance(test).toPrintable());
        synchronized (test){
            out.println("lock ing");
            out.println(ClassLayout.parseInstance(test).toPrintable());
        }
        out.println("after lock");
        out.println(ClassLayout.parseInstance(test).toPrintable());
    }
}
