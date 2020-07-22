package org.xinhua.cbcloud.sync;

public class Example4Start {

    Object o= new Object();

    static {
        System.loadLibrary( "SyncThreadNative" );
    }

    public static void main(String[] args) {
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        Example4Start example4Start = new Example4Start();
        example4Start.start();
    }

    public void start(){
        Thread thread = new Thread(){
            public void run() {
                while (true){
                    try {
                        sync();
                    } catch (InterruptedException e) {

                    }
                }
            }
        };

        Thread thread2 = new Thread(){
            @Override
            public void run() {
                while (true){
                    try {
                        sync();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        thread.setName("t1");
        thread2.setName("t2");

        thread.start();
        thread2.start();
    }

    public native void tid();

    public  void sync() throws InterruptedException {
        synchronized(o) {
            tid();
        }
    }
}
