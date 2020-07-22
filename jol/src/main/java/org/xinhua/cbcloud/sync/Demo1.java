package org.xinhua.cbcloud.sync;

public class Demo1 {

    static boolean runIng = true;

    public static void main(String[] args) throws InterruptedException {
        test();
//        Thread.sleep(2000); // 添加睡眠之后进行了指令重排序
        runIng = false;
    }

    public static void test() {
        Thread thread = new Thread() {
            @Override
            public void run() {

            }
        };
        thread.start();
    }
}
