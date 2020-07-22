package org.xinhua.cbcloud.sync;

public class Demo3 {
    public static void main(String[] args) {
        try {
            System.out.println(1/0);
        } catch (Exception e) {
            if (e instanceof ArithmeticException) {
                try {
                    for (int i = 0; i < 3; i++) {
                        Thread.sleep(1000);
                        System.out.println("第 " + i + " 次重试");
                        try {
                            System.out.println(1/0);
                            System.out.println("重试成功");
                            break;
                        } catch (Exception e1) {
                            if (e instanceof ArithmeticException)
                                System.out.println("重试失败");
                                continue;
                        }
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
