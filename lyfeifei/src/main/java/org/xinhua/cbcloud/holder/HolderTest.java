package org.xinhua.cbcloud.holder;

import javax.xml.ws.Holder;

public class HolderTest {

    public static void main(String[] args) {
        User user = new User();
        user.setId("1");
        user.setName("LLS");
        user.setPassWord("123456");

        Holder<User> holder = new Holder<>(user);  //使用Holder对User进行包装

        testHolder(holder);
        System.out.println("新增的主页是： " + holder.value.getHomePage());
    }

    public static void testHolder(Holder<User> uHolder){
        User user = new User();
        user.setId(uHolder.value.getId());
        user.setName(uHolder.value.getName());
        user.setPassWord(uHolder.value.getPassWord());
        user.setHomePage("https://www.zifangsky.cn");  //新增
        uHolder.value = user;
    }
}
