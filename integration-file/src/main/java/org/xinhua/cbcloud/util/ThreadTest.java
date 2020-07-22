package org.xinhua.cbcloud.util;

public class ThreadTest {

    static {
        // 装载库，保证JVM在启动的时候就会装载，故而一般是也给static
        System.loadLibrary( "ThreadTestNative" );
    }

    public static void main(String[] args) {
        ThreadTest threadTest =new ThreadTest();
        threadTest.start0();
    }

    public void run(){
        System.out.println("I am java Thread !!");
    }

    private native void start0();
}
