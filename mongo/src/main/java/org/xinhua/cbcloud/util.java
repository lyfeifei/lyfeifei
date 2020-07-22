package org.xinhua.cbcloud;

import org.xinhua.cbcloud.pojo.User;

import java.util.ArrayList;
import java.util.List;

public class util {
    public static void main(String[] args) {
        List<User> list = new ArrayList<User>();
        User user1 = new User();
        user1.setId(1);
        user1.setName("name1");
        user1.setAddress("address1");
        User user2 = new User();
        user2.setId(2);
        user2.setName("name2");
        user2.setAddress("address2");
        list.add(user1);
        list.add(user2);
        System.out.println(list.toString());
        for (User usr : list) {
            usr.setName("USERNAME");
        }

        System.out.println(list.toString());
    }
}
