package org.xinhua.cbcloud.proxy;

import net.logstash.logback.encoder.org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public class Test {
    /*public static void main(String[] args) {
        //实例化目标对象（创造一个力宏）
        WangLiHong liHong = new WangLiHong();
        //实例化调用处理类（编好的故事）
        MyStoryInvocationHandler handler = new MyStoryInvocationHandler(liHong);

        //接下来创建代理（经纪人）

        //准备一个类加载器
        ClassLoader loader =   Test.class.getClassLoader();
        //获取目标对象的接口类对象数组
        Class<?>[] interfaces = liHong.getClass().getInterfaces();

        //创建代理
        Singer proxy = (Singer) Proxy.newProxyInstance(loader, interfaces, handler);

        //开始点歌
        proxy.orderSong("就是现在");
        System.out.println("****** 歌唱中......********");
        //歌唱完了，say goodBye吧
        proxy.sayGoodBye("zhangchengzi");
    }*/

    public static void main(String[] args) {

        String str = "INFMX-TPA01284640";
        String str2 = null;
        if(StringUtils.isNotEmpty(str) && str.contains("-")) {
            System.out.println(str.substring(0, str.indexOf("-")));
            System.out.println(str.substring(str.indexOf("-") + 1, str.length()));
        }
    }
}
