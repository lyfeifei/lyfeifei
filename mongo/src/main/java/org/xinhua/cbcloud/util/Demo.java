package org.xinhua.cbcloud.util;

import com.alibaba.fastjson.JSONObject;
import org.bson.BSON;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryAction;
import org.elasticsearch.index.reindex.UpdateByQueryRequestBuilder;
import org.elasticsearch.script.Script;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class Demo {
    public static void main(String[] args) {
//        String str = "/dagdata/data1/DAG_unfinished_plate/2020/0721/4118ceb4c0fd457e945d0d2c0399b472.jpeg";
//        boolean isMatch = Pattern.matches(".*2020.*", str);
//        System.out.println("result：" + isMatch);

//        List<String> resultList = new ArrayList<>();
//
//        List<String> list = ReadTxtFile.readTxtForList();
//        System.out.println("读取原数据大小：" + list.size());
//        Set set = new HashSet();
//
//        for (int i = 0; i < list.size(); i++) {
//            boolean add = set.add(list.get(i));
//            if (add) {
//                resultList.add(list.get(i));
//            }
//        }
//        WriteTxtFile.writeTxt(resultList);
//        System.out.println("排重后数据大小：" + set.size());

//        List<String> list = ReadTxtFile.read("es-data.txt");
//        System.out.println(list.size());

//        for (int i = 0; i < 6090000; i += 20000) {
//            System.out.println("limit: " + (20000+i) + ", spik: " + i);
//        }

        checkData();
    }

    public static void checkData() {

        String photoPlateNum = null;
        List<String> docIds = new ArrayList<>();
        JSONObject json = new JSONObject();

        List<Object> result = new ArrayList<>();

        String tmp = null;
        List<String> list = ReadTxtFile.read("DAG_FINISHED_PLATE_0824.json");
        System.out.println("原始数据大小：" + list.size());

        int temp = 1;
        for (int i = 0; i < list.size(); i += 50000) {
            System.out.println("======================进行第" + temp + "次批处理=======================");
            if (list.size() - i > 50000) {
                List<String> strings = list.subList(i, i + 50000);
                for (int j = 0; j < strings.size(); j++) {
                    json = JSONObject.parseObject(strings.get(j));
                    docIds = (List<String>) json.get("docIds");
                    photoPlateNum = json.getString("photoPlateNum");
                    if (docIds != null && docIds.size() == 1) {
                        tmp = docIds.get(0) + "#" + photoPlateNum;
                        result.add(tmp);
                    } else if (docIds != null && docIds.size() > 1) {
                        for (int s = 0; s < docIds.size(); s++) {
                            tmp = docIds.get(s) + "#" + photoPlateNum;
                            result.add(tmp);
                        }
                    }
                }
            } else {
                if (list.size() > i) {
                    List<String> strings = list.subList(i, list.size());
                    for (int j = 0; j < strings.size(); j++) {
                        json = JSONObject.parseObject(strings.get(j));
                        docIds = (List<String>) json.get("docIds");
                        photoPlateNum = json.getString("photoPlateNum");
                        if (docIds != null && docIds.size() == 1) {
                            tmp = docIds.get(0) + "#" + photoPlateNum;
                            result.add(tmp);
                        } else if (docIds != null && docIds.size() > 1) {
                            for (int s = 0; s < docIds.size(); s++) {
                                tmp = docIds.get(s) + "#" + photoPlateNum;
                                result.add(tmp);
                            }
                        }
                    }
                }
            }
            temp += 1;
        }

        System.out.println("提取后数据大小：" + result.size());
        WriteTxtFile.write(result, "DAG_FINISHED_PLATE.txt");
    }

    /**
     * 获取当前日期
     * @return
     */
    public static String getCurrentDate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        return df.format(new Date());
    }

    /**
     * 获取过去第几天的日期
     *
     * @param past
     * @return
     */
    public static String getPastDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        return format.format(today);
    }
}
