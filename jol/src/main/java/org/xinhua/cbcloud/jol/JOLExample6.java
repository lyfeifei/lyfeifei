package org.xinhua.cbcloud.jol;

// 性能对比偏向锁和轻量级锁：
public class JOLExample6 {
    public static void main(String[] args) throws Exception {
        Test test = new Test();
        long start = System.currentTimeMillis();
        //调用同步方法1000000000L 来计算1000000000L的++，对比偏向锁和轻量级锁的性能
        for(int i=0;i<1000000000L;i++){
            test.parse();
        }
        long end = System.currentTimeMillis();
        System.out.println(String.format("%sms", end - start));
    }
}
