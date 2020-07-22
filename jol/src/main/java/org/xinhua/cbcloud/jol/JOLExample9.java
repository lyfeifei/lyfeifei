package org.xinhua.cbcloud.jol;

import org.openjdk.jol.info.ClassLayout;
import static java.lang.System.out;

// 如果对象已经计算了hashcode就不能偏向了
public class JOLExample9 {
    static Test test;
    public static void main(String[] args) throws Exception {
        Thread.sleep(5000);
        test = new Test();
        test.hashCode();
        out.println("befor lock");
        out.println(ClassLayout.parseInstance(test).toPrintable());
        synchronized (test){
            out.println("lock ing");
            out.println(ClassLayout.parseInstance(test).toPrintable());
        }
        out.println("after lock");
        out.println(ClassLayout.parseInstance(test).toPrintable());
    }
}
