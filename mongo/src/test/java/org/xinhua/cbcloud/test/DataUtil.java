package org.xinhua.cbcloud.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;
import org.xinhua.cbcloud.ftp.FtpJSch;
import org.xinhua.cbcloud.pojo.ExifPropertiesVO;
import org.xinhua.cbcloud.pojo.ReporterTemp;
import org.xinhua.cbcloud.pojo.User;
import org.xinhua.cbcloud.util.DateUtil;
import org.xinhua.cbcloud.util.ReadTxtFile;
import org.xinhua.cbcloud.util.WriteTxtFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DataUtil {

    private int count = 0;

    private Logger log = LoggerFactory.getLogger(DataUtil.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void test() throws Exception {
        // 任务队列
        BlockingQueue blockingQueue = new LinkedBlockingQueue<>();
        // 读取待操作数据
        ReadTxtFile.readTxt(blockingQueue);
        // 提取底片号
        List<String> list = ReadTxtFile.extract(blockingQueue);

        /*int temp = 1;
        for (int i = 0; i < list.size(); i += 500) {
            System.out.println("======================进行第" + temp + "次批处理=======================");
            if (list.size() - i > 500) {
                List<String> strings = list.subList(i, i + 500);
                concurrent(strings);
            } else {
                if (list.size() > i) {
                    List<String> strings = list.subList(i, list.size());
                    concurrent(strings);
                }
            }
            temp += 1;
        }*/
    }

    public synchronized void concurrent(List<String> list) throws Exception {
        System.out.println("数据处理 - 开始");
        List<ReporterTemp> insertList = new ArrayList<>();
        Criteria criteria = Criteria.where("photoPlateNum").in(list);
        List<JSONObject> resultList = mongoTemplate.find(Query.query(criteria), JSONObject.class, "DAG_UNFINISHED_PLATE");
        System.out.println("匹配结果：" + resultList.size());
        count += resultList.size();
        for (int j = 0; j < resultList.size(); j++) {
            JSONObject obj = resultList.get(j);
            JSONObject exifProperties = JSONObject.parseObject(JSON.toJSONString(obj.get("exifProperties")));
            ReporterTemp tmp = new ReporterTemp();
            tmp.set_id(new ObjectId(new Date()));
            tmp.setDocId(obj.getString("docId"));
            tmp.setPhotoPlateNum(obj.getString("photoPlateNum"));
            if (exifProperties != null) {
                tmp.setDateTime(exifProperties.getString("dateTime"));
            }
            tmp.setFilePath(obj.getString("filePath"));
            insertList.add(tmp);
        }
        System.out.println("过程统计结果：" + count);
        BulkOperations ops = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, "reporter_temp");
        ops.insert(insertList);
        ops.execute();
        System.out.println("数据处理 - 结束");
    }

    @Test
    public void queryFilePath() throws Exception {
        Criteria criteria = Criteria.where("dateTime").is(null);
        List<JSONObject> resultList = mongoTemplate.find(Query.query(criteria), JSONObject.class, "reporter_temp");
        System.out.println("匹配结果：" + resultList.size());

        String path = null;
        String filePath = null;
        List<String> list = new ArrayList<>();
        for (int i = 0; i < resultList.size(); i++) {
            System.out.println("======================进行第" + i + "次处理=======================");
            JSONObject obj = resultList.get(i);
            path = obj.getString("filePath");
            if (path.contains("publicStore")) {
                filePath = path.replace("publicStore,", "/dagdata/data1");
            }
            if (path.contains("privateStore")) {
                filePath = path.replace("publicStore,", "/dagdata/data2");
            }
        }
    }

    @Test
    public void createDateTime() throws Exception {
        List<String> list = ReadTxtFile.readTxtForList();
        int temp = 1;
        for (int i = 0; i < list.size(); i += 100) {
            System.out.println("======================进行第" + temp + "次批处理=======================");
            if (list.size() - i > 100) {
                List<String> strings = list.subList(i, i + 100);
                FtpJSch.downloadFiles(list);
            } else {
                if (list.size() > i) {
                    List<String> strings = list.subList(i, list.size());
                    FtpJSch.downloadFiles(list);
                }
            }
            temp += 1;
        }
    }

    @Test
    public void checkIsNull() throws Exception {
        Criteria criteria = Criteria.where("dateTime").is(null);
        List<JSONObject> resultList = mongoTemplate.find(Query.query(criteria), JSONObject.class, "reporter_temp");
        System.out.println("匹配结果：" + resultList.size());

        String docId = null;
        String filePath = null;
        String photoPlateNum = null;
        List<String> list = new ArrayList<>();
        for (int i = 0; i < resultList.size(); i++) {
            JSONObject obj = resultList.get(i);
            docId = obj.getString("docId");
            filePath = obj.getString("filePath");
            photoPlateNum = obj.getString("photoPlateNum");
            String tmp = "/dagdata/reporter_temp/dag_pic/" + docId + "_" + photoPlateNum + "." + filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length());
            list.add(tmp);
        }
        WriteTxtFile.writeTxt(list);
    }

    @Test
    public void testCount() throws Exception {
        // 任务队列
        BlockingQueue blockingQueue = new LinkedBlockingQueue<>();
        // 读取待操作数据
        ReadTxtFile.readTxt(blockingQueue);
        // 提取底片号
        List<String> list = ReadTxtFile.extract(blockingQueue);
        int temp = 1;
        for (int i = 0; i < list.size(); i += 500) {
            System.out.println("======================进行第" + temp + "次批处理=======================");
            if (list.size() - i > 500) {
                List<String> strings = list.subList(i, i + 500);
                Criteria criteria = Criteria.where("photoPlateNum").in(strings);
                List<JSONObject> resultList = mongoTemplate.find(Query.query(criteria), JSONObject.class, "DAG_UNFINISHED_PLATE");
                System.out.println("匹配结果：" + resultList.size());
                count += resultList.size();
            } else {
                if (list.size() > i) {
                    List<String> strings = list.subList(i, list.size());
                    Criteria criteria = Criteria.where("photoPlateNum").in(strings);
                    List<JSONObject> resultList = mongoTemplate.find(Query.query(criteria), JSONObject.class, "DAG_UNFINISHED_PLATE");
                    System.out.println("匹配结果：" + resultList.size());
                    count += resultList.size();
                }
            }
            temp += 1;
        }
        System.out.println("过程统计结果：" + count);
    }

    @Test
    public void BulkQuery() {
        List<String> list = new ArrayList<>();
        list.add("INFMX-PEA00836989");
//        list.add("INFMX-PEA00836996");
//        list.add("INFMX-PEA00889882");
//        list.add("INFMX-PEA00789093");
//        list.add("INFMX-PEA00789094");
        Criteria criteria = Criteria.where("photoPlateNum").in(list);
        List<JSONObject> resultList = mongoTemplate.find(Query.query(criteria), JSONObject.class, "DAG_UNFINISHED_PLATE");
        for (int i = 0; i < resultList.size(); i++) {
            JSONObject obj = resultList.get(i);
            JSONObject exifProperties = JSONObject.parseObject(JSON.toJSONString(obj.get("exifProperties")));
            System.out.println(exifProperties.get("dateTime"));
            System.out.println(obj.get("docId"));
            System.out.println(obj.get("photoPlateNum"));
            System.out.println(obj.get("filePath"));
        }
        System.out.println(resultList.size());
    }

    @Test
    public void BulkOperations() {
        List<ReporterTemp> insertList = new ArrayList<ReporterTemp>();
        ReporterTemp tmp1 = new ReporterTemp();
        tmp1.setDocId("101002020031090001111");
        tmp1.setPhotoPlateNum("INFMX-PEA00753891");
        tmp1.setDateTime(DateUtil.getCurrentDateNew());
        tmp1.setFilePath("/dagdata/data1/real/a4dc071a832e4b99b0f0cec3dcee8d2c.jpg");
        insertList.add(tmp1);
        ReporterTemp tmp2 = new ReporterTemp();
        tmp2.setDocId("101002020031090001111");
        tmp2.setPhotoPlateNum("INFMX-PEA00753891");
        tmp2.setDateTime(DateUtil.getCurrentDateNew());
        tmp2.setFilePath("/dagdata/data1/real/a4dc071a832e4b99b0f0cec3dcee8d2c.jpg");
        insertList.add(tmp2);
        ReporterTemp tmp3 = new ReporterTemp();
        tmp3.setDocId("101002020031090001111");
        tmp3.setPhotoPlateNum("INFMX-PEA00753891");
        tmp3.setDateTime(DateUtil.getCurrentDateNew());
        tmp3.setFilePath("/dagdata/data1/real/a4dc071a832e4b99b0f0cec3dcee8d2c.jpg");
        insertList.add(tmp3);
        /**
         * BulkMode.UNORDERED:表示并行处理，遇到错误时能继续执行不影响其他操作；
         * BulkMode.ORDERED：表示顺序执行，遇到错误时会停止所有执行
         */
        BulkOperations ops = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, "reporter_temp");
        ops.insert(insertList);
        ops.execute();
    }

    @Test
    public void testAdd() {
        ReporterTemp tmp = new ReporterTemp();
        tmp.setDocId("101002020031090001111");
        tmp.setPhotoPlateNum("INFMX-PEA00753891");
        tmp.setDateTime(DateUtil.getCurrentDateNew());
        tmp.setFilePath("/dagdata/data1/real/a4dc071a832e4b99b0f0cec3dcee8d2c.jpg");
        mongoTemplate.save(tmp, "reporter_temp");
    }

    @Test
    public void testFindOne() {
        Query query = new Query();
        Criteria criteria = Criteria.where("name").is("admin");
        criteria.and("address").is("bj");
        query.addCriteria(criteria);
        User user = mongoTemplate.findOne(query, User.class,"user");
        System.out.println(user);
    }
}
