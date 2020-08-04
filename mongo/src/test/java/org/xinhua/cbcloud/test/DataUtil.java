package org.xinhua.cbcloud.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.QueryOperators;
import org.apache.lucene.document.Document;
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

        int temp = 1;
        for (int i = 0; i < list.size(); i++) {
            System.out.println("======================进行第" + temp + "次批处理=======================");
            if (list.size() - i > 10) {
                System.out.println("@" + list.subList(i, i + 10).toString());
            } else {
                if (list.size() > i) {
                    System.out.println(list.subList(i, list.size()).toString());
                }
            }
            temp += 1;
        }

    }

    @Test
    public void concurrent() throws Exception {

        // 任务队列
        BlockingQueue blockingQueue = new LinkedBlockingQueue<>();

        // 读取待操作数据
        ReadTxtFile.readTxt(blockingQueue);

        // 提取底片号
        List<String> list = ReadTxtFile.extract(blockingQueue);

        List<ReporterTemp> insertList = new ArrayList<>();

        // 批量操作
        BulkOperations ops = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, "reporter_temp");

        List<String> errorData = new ArrayList<>();

        List<String> tmpList = new ArrayList<>();

        String photoPlateNum = null;
        for (int i = 0; i < list.size(); i++) {
            photoPlateNum = list.get(i);
            //tmpList.add(list.get(i));
            //if (tmpList.size() == 100) {
                Criteria criteria = Criteria.where("photoPlateNum").is(photoPlateNum);
                List<JSONObject> resultList = mongoTemplate.find(Query.query(criteria), JSONObject.class, "DAG_UNFINISHED_PLATE");
                for (int j = 0; j < resultList.size(); j++) {
                    JSONObject obj = resultList.get(j);
                    JSONObject exifProperties = JSONObject.parseObject(JSON.toJSONString(obj.get("exifProperties")));

                    ReporterTemp tmp = new ReporterTemp();
                    tmp.set_id(new ObjectId(new Date()));
                    tmp.setDocId(obj.getString("docId"));
                    tmp.setPhotoPlateNum(obj.getString("photoPlateNum"));
                    if (exifProperties != null) {
                        tmp.setDate(exifProperties.getString("dateTime"));
                    }
                    tmp.setFilePath(obj.getString("filePath"));
                    mongoTemplate.save(tmp, "reporter_temp");
                    //insertList.add(tmp);
                    /*if (insertList.size() == 10) {
                        ops.insert(insertList);
                        ops.execute();
                        insertList.clear();
                        System.out.println("批量插入10条成功");
                    }*/
                }
                //ops.insert(insertList);
                //ops.execute();
                //insertList.clear();
                //tmpList.clear();
            //}
        }
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
        tmp1.setDate(DateUtil.getCurrentDateNew());
        tmp1.setFilePath("/dagdata/data1/real/a4dc071a832e4b99b0f0cec3dcee8d2c.jpg");
        insertList.add(tmp1);
        ReporterTemp tmp2 = new ReporterTemp();
        tmp2.setDocId("101002020031090001111");
        tmp2.setPhotoPlateNum("INFMX-PEA00753891");
        tmp2.setDate(DateUtil.getCurrentDateNew());
        tmp2.setFilePath("/dagdata/data1/real/a4dc071a832e4b99b0f0cec3dcee8d2c.jpg");
        insertList.add(tmp2);
        ReporterTemp tmp3 = new ReporterTemp();
        tmp3.setDocId("101002020031090001111");
        tmp3.setPhotoPlateNum("INFMX-PEA00753891");
        tmp3.setDate(DateUtil.getCurrentDateNew());
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
        tmp.setDate(DateUtil.getCurrentDateNew());
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
