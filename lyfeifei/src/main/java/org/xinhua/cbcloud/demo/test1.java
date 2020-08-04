package org.xinhua.cbcloud.demo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class test1 {
    public static void main(String[] args) {
        JSONArray array = new JSONArray();
        for (int i = 0; i < 3; i++) {
            JSONObject object = new JSONObject();
            object.put("dataTime", "2020-07-22 11:23:20");
            array.add(object);
        }

        JSONObject jb = new JSONObject();
        jb.put("recDateTimeArr", array);
        System.out.println(array.toString());
    }
}
