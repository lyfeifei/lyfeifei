package org.xinhua.cbcloud.jol;

import org.openjdk.jol.info.ClassLayout;
import static java.lang.System.out;

public class JOLExample2 {
    public static void main(String[] args) throws Exception {
        Test test = new Test();
        out.println("befor hash");
        //没有计算HASHCODE之前的对象头
        out.println(ClassLayout.parseInstance(test).toPrintable());
        //JVM 计算的hashcode
        out.println("jvm------------0x"+Integer.toHexString(test.hashCode()));
        HashUtil.countHash(test);
        //当计算完hashcode之后，我们可以查看对象头的信息变化
        out.println("after hash");
        out.println(ClassLayout.parseInstance(test).toPrintable());

    }
}
