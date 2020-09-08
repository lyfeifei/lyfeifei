package org.xinhua.cbcloud.util;

import com.alibaba.fastjson.JSONObject;
import org.bson.BSON;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryAction;
import org.elasticsearch.index.reindex.UpdateByQueryRequestBuilder;
import org.elasticsearch.script.Script;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class Demo {

    public static void main(String[] args) throws Exception {
//        List list = new ArrayList();
//        test();

//        File file = new File("D:\\data-distinct-1171539.txt");
//        System.out.println(file);

//        for (int i = 0; i < 5; i++) {
//            String fileName = i + ".txt";
//
//            Date date = new Date();
//            String dataForm = new SimpleDateFormat("yyyy-MM-dd").format(date);
//            String path = "D:\\" + dataForm;
//            File f = new File(path);
//            if(!f.exists()){
//                f.mkdirs();
//            }
//
//            FileWriter out = new FileWriter(path + "\\" + fileName);
//            out.write(path + fileName);
//            out.flush();
//            out.close();
//        }

        String sourceFilePath = "/dagdata/data1/video/2020/0907/1027bdecfabc48219d7cb06173ea0b6a_P.jpg";

        final File htmlFile = File.createTempFile("temp", ".html");//创建临时文件
        System.out.println("临时文件所在的本地路径：" + htmlFile.getCanonicalPath());
        FileOutputStream fos = new FileOutputStream(htmlFile);
        try {
            //这里处理业务逻辑
        } finally {
            //关闭临时文件
            fos.flush();
            fos.close();
            htmlFile.deleteOnExit();//程序退出时删除临时文件
        }
    }

    public static void test() {
        List<String> list = ReadTxtFile.read("data-distinct-1171539.txt");
        System.out.println("原始数据大小：" + list.subList(0, 10));
    }

    public static void checkData() {

        String photoPlateNum = null;
        List<String> docIds = new ArrayList<>();
        JSONObject json = new JSONObject();

        List<Object> result = new ArrayList<>();

        String tmp = null;
        List<String> strings = new ArrayList<>();
        List<String> list = ReadTxtFile.read("DAG_FINISHED_PLATE_0824.json");
        System.out.println("原始数据大小：" + list.size());

        int temp = 1;
        for (int i = 0; i < list.size(); i += 50000) {
            System.out.println("======================进行第" + temp + "次批处理=======================");
            if (list.size() - i > 50000) {
                strings = list.subList(i, i + 50000);
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
                    strings = list.subList(i, list.size());
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

            if (result.size() == 3000000) {

            }
        }

        System.out.println("提取后数据大小：" + result.size());
        WriteTxtFile.write(result, "DAG_FINISHED_PLATE.txt");
    }

    public static void bulkRead() {
        String photoPlateNum = null;
        List<String> docIds = new ArrayList<>();
        JSONObject json = new JSONObject();

        List<Object> result = new ArrayList<>();

        String tmp = null;
        List<String> strings = new ArrayList<>();
        List<String> list = ReadTxtFile.read("DAG_FINISHED_PLATE_0824.json");
        System.out.println("原始数据大小：" + list.size());

        strings = list.subList(6000000, list.size());
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

        System.out.println("提取后数据大小：" + result.size());
        WriteTxtFile.write(result, "DAG_FINISHED_PLATE3.txt");
    }

    public static void bulkCheck() {

        List<Object> result = new ArrayList<>();

        List<String> es = ReadTxtFile.read("es-data.txt");
        System.out.println("es原始数据大小" + es.size());

        Set<String> set = new HashSet<>(es);
        System.out.println("set原始数据大小" + set.size());

//        List<String> list1 = ReadTxtFile.read("DAG_UNFINISHED_PLATE-2760365.txt");
//        for (int i = 0; i < list1.size(); i++) {
//            boolean tmp = set.add(list1.get(i));
//            if (tmp) {
//                result.add(list1.get(i));
//            }
//        }
//        System.out.println("DAG_UNFINISHED_PLATE 处理完毕");

//        List<String> list2 = ReadTxtFile.read("DAG_FINISHED_PLATE1-3000809.txt");
//        for (int i = 0; i < list2.size(); i++) {
//            boolean tmp = set.add(list2.get(i));
//            if (tmp) {
//                result.add(list2.get(i));
//            }
//        }
//        System.out.println("DAG_FINISHED_PLATE1 处理完毕");

//        List<String> list3 = ReadTxtFile.read("DAG_FINISHED_PLATE2-3000692.txt");
//        for (int i = 0; i < list3.size(); i++) {
//            boolean tmp = set.add(list3.get(i));
//            if (tmp) {
//                result.add(list3.get(i));
//            }
//        }
//        System.out.println("DAG_FINISHED_PLATE2 处理完毕");

        List<String> list4 = ReadTxtFile.read("DAG_FINISHED_PLATE3-1212035.txt");
        for (int i = 0; i < list4.size(); i++) {
            boolean tmp = set.add(list4.get(i));
            if (tmp) {
                result.add(list4.get(i));
            }
        }
        System.out.println("DAG_FINISHED_PLATE3 处理完毕");

        System.out.println("提取后数据大小：" + result.size());
        WriteTxtFile.write(result, "data-distinct3.txt");
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
