package org.xinhua.cbcloud.jol;

import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;

import static java.lang.System.out;

public class JOLExample1 {

    static class Test {
        //boolean flag = false; //占一个字节
        double db = 1.0;
    }

    public static void main(String[] args) {
        out.println(VM.current().details());
        out.println(ClassLayout.parseInstance(new Test()).toPrintable());
    }
}
