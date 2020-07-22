package org.xinhua.cbcloud.util;

public class ThreadGroupTest {

    int i = 0;

    public static void main(String[] args) {

        ThreadGroup tg1 = new ThreadGroup("线程组1");
        ThreadGroup tg2 = new ThreadGroup("线程组2");

        Thread t1 = new Thread(tg1, new Thread1(), "张三");
        Thread t2 = new Thread(tg2, new Thread2(), "李四");

        /*System.out.println(t1.getThreadGroup().getName());
        System.out.println(t2.getThreadGroup().getName());*/

        t1.start();
        t2.start();

        tg1.destroy();
    }
}

class Thread1 implements Runnable {
    String str = "abc";
    @Override
    public void run() {
        while (true) {
            System.out.println("线程1：" + str);
        }
    }
}

class Thread2 implements Runnable {
    String str = "abc";
    @Override
    public void run() {
        while (true) {
            System.out.println("线程2：" + str);
        }
    }
}