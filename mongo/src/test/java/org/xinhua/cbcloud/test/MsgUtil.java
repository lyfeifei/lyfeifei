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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.util.Pair;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;
import org.xinhua.cbcloud.ftp.FtpJSch;
import org.xinhua.cbcloud.ftp.ReadMetadata;
import org.xinhua.cbcloud.pojo.DataTemp;
import org.xinhua.cbcloud.pojo.ReporterTemp;
import org.xinhua.cbcloud.pojo.User;
import org.xinhua.cbcloud.util.DateUtil;
import org.xinhua.cbcloud.util.ReadTxtFile;
import org.xinhua.cbcloud.util.ThreadUtil;
import org.xinhua.cbcloud.util.WriteTxtFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MsgUtil {

    private Logger log = LoggerFactory.getLogger(MsgUtil.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    class ProMsgTasks implements Runnable {
        BlockingQueue blockingQueue;
        public ProMsgTasks(BlockingQueue blockingQueue) {
            this.blockingQueue = blockingQueue;
        }
        @Override
        public void run() {
            try {
                String path = null;
                String filePath = null;
                List<String> list = new ArrayList<>();
                Criteria criteria = Criteria.where("dateTime").is(null);
                List<JSONObject> resultList = mongoTemplate.find(Query.query(criteria), JSONObject.class, "reporter_temp");
                System.out.println("匹配结果：" + resultList.size());
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
                    list.add(filePath);
                    //blockingQueue.put(filePath);
                }
                WriteTxtFile.writeTxtForError(list);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class CurMsgTasks implements Runnable {
        BlockingQueue blockingQueue;
        public CurMsgTasks(BlockingQueue blockingQueue) {
            this.blockingQueue = blockingQueue;
        }
        @Override
        public void run() {
            try {
                List<String> list = new ArrayList<>();
                while (!blockingQueue.isEmpty()) {
                    String file = (String) blockingQueue.take();
                    String fileName = file.substring(file.lastIndexOf("/") + 1, file.length());
                    String filePath = file.substring(0, file.lastIndexOf("/"));
                    System.out.println("处理路径：" + filePath + "/" + fileName);
                    boolean result = FtpJSch.downloadFile(filePath, fileName);
                    if (!result) {
                        list.add(file);
                    }
                }
                WriteTxtFile.writeTxtForError(list);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void MsgTasks() throws Exception {
        // 任务队列
        BlockingQueue blockingQueue = new LinkedBlockingQueue<>();

        // 消息生产
        for (int i = 0; i < 1; i++) {
            ThreadUtil.getInstance().addTask(new ProMsgTasks(blockingQueue));
        }

        Thread.sleep(5000L);

        // 消息消费
        for (int i = 0; i < 4; i++) {
            ThreadUtil.getInstance().addTask(new CurMsgTasks(blockingQueue));
        }
    }

    @Test
    public void readTxt() {
        String path = null;
        String filePath = null;
        String photoPlateNum = null;
        List<String> list = new ArrayList<>();
        Criteria criteria = Criteria.where("dateTime").is(null);
        List<JSONObject> resultList = mongoTemplate.find(Query.query(criteria), JSONObject.class, "reporter_temp");
        System.out.println("匹配结果：" + resultList.size());
        for (int i = 0; i < resultList.size(); i++) {
            System.out.println("======================进行第" + i + "次处理=======================");
            JSONObject obj = resultList.get(i);
            path = obj.getString("filePath");
            photoPlateNum = obj.getString("photoPlateNum");
            if (path.contains("publicStore")) {
                filePath = path.replace("publicStore,", "/dagdata/data1");
            }
            if (path.contains("privateStore")) {
                filePath = path.replace("publicStore,", "/dagdata/data2");
            }
            list.add(photoPlateNum);
        }
        WriteTxtFile.writeTxtForError(list);
    }

    @Test
    public void findDocId() {
        Date date1 = DateUtil.StringToDate("2020:05:25 15:08:35");
        Date date2 = DateUtil.StringToDate("2020:05:25 18:08:35");
        Criteria criteria = Criteria.where("dateTime").gte(date1).lte(date2);
        Query query = Query.query(criteria);
        query.with(new Sort(Sort.Direction.DESC, "dateTime"));
        List<JSONObject> resultList = mongoTemplate.find(query, JSONObject.class, "data_temp");
        System.out.println("匹配结果：" + resultList.toString());
    }

    @Test
    public void dataInput() {
        List<ReporterTemp> resultList = mongoTemplate.find(new Query(), ReporterTemp.class, "reporter_temp");
        System.out.println("匹配结果：" + resultList.size());

        boolean flag = false;
        ObjectId _id = null;
        String docId = null;
        String dateTime = null;
        String filePath = null;
        String photoPlateNum = null;
        ReporterTemp tmp = new ReporterTemp();
        List<DataTemp> insertList = new ArrayList<>();
        for (int i = 0; i < resultList.size(); i++) {
            tmp = resultList.get(i);
            _id = (ObjectId) tmp.get_id();
            docId = tmp.getDocId();
            dateTime = tmp.getDateTime();
            filePath = tmp.getFilePath();
            photoPlateNum = tmp.getPhotoPlateNum();


            // 准备数据
            DataTemp dataTemp = new DataTemp();
            dataTemp.set_id(_id);
            dataTemp.setDocId(docId);
            try {
                dataTemp.setDateTime(DateUtil.StringToDate(dateTime));
            } catch (Exception e){
                continue;
            }
            dataTemp.setFilePath(filePath);
            dataTemp.setPhotoPlateNum(photoPlateNum);
            insertList.add(dataTemp);

            if (i == resultList.size() -1) {
                flag = true;
            }
        }

        if (flag) {
            System.out.println("处理数据：" + insertList);
            System.out.println("照片信息提取结束，开始更新数据...");
            int temp = 1;
            BulkOperations ops = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, "data_temp");
            ops.insert(insertList);
            ops.execute();
            /*for (int i = 0; i < insertList.size(); i += 1000) {
                System.out.println("======================进行第" + temp + "次批处理=======================");
                if (insertList.size() - i > 1000) {
                    List<DataTemp> strings = insertList.subList(i, i + 1000);
                    ops.insert(strings);
                    ops.execute();
                } else {
                    if (insertList.size() > i) {
                        List<DataTemp> strings = insertList.subList(i, insertList.size());
                        ops.insert(strings);
                        ops.execute();
                    }
                }
                temp += 1;
            }*/
        }
    }

    @Test
    public void readExifInfo() {
        boolean flag = false;
        ObjectId _id = null;
        String path = null;
        String docId = null;
        String dateTime = null;
        String filePath = null;
        JSONObject obj = new JSONObject();
        List<String> errorData = new ArrayList<>();
        List<Pair<Query, Update>> updateList = new ArrayList<>();
        Criteria criteria = Criteria.where("dateTime").is(null);
        List<JSONObject> resultList = mongoTemplate.find(Query.query(criteria), JSONObject.class, "reporter_temp");
        for (int i = 0; i < resultList.size(); i++) {
            System.out.println("======================进行第" + i + "次处理=======================");
            obj = resultList.get(i);
            _id = (ObjectId) obj.get("_id");
            docId = obj.getString("docId");
            path = obj.getString("filePath");
            filePath = "D:\\tmp\\" + path.substring(path.lastIndexOf("/") + 1, path.length());
            try {
                dateTime = ReadMetadata.exifInfo(filePath);
            } catch (Exception e) {
                errorData.add(docId);
                continue;
            }

            // 准备数据
            Query query = new Query(new Criteria("_id").is(_id));
            Pair<Query, Update> updatePair = Pair.of(query, new Update().set("dateTime", dateTime));
            updateList.add(updatePair);
            if (i == resultList.size() -1) {
                flag = true;
            }
        }

        WriteTxtFile.writeTxtForError(errorData);

        if (flag) {
            System.out.println("照片信息提取结束，开始更新数据...");
            int temp = 1;
            BulkOperations ops = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, "reporter_temp");
            for (int i = 0; i < updateList.size(); i += 500) {
                System.out.println("======================进行第" + temp + "次批处理=======================");
                if (updateList.size() - i > 500) {
                    List<Pair<Query, Update>> strings = updateList.subList(i, i + 500);
                    ops.upsert(strings);
                    ops.execute();
                } else {
                    if (updateList.size() > i) {
                        List<Pair<Query, Update>> strings = updateList.subList(i, updateList.size());
                        ops.upsert(strings);
                        ops.execute();
                    }
                }
                temp += 1;
            }
        }
    }

    @Test
    public void readExifInfoWithNoDocId() {
        boolean flag = false;
        String dateTime = null;
        String fileName = null;
        String filePath = null;
        List<String> errorData = new ArrayList<>();
        List<String> resultList = ReadTxtFile.readTxtForList();
        List<ReporterTemp> insertList = new ArrayList<ReporterTemp>();
        for (int i = 0; i < resultList.size(); i++) {
            System.out.println("======================进行第" + i + "次处理=======================");
            fileName = resultList.get(i);
            filePath = "D:\\tmp\\" + resultList.get(i);
            try {
                dateTime = ReadMetadata.exifInfo(filePath);
            } catch (Exception e) {
                errorData.add("/dagdata/data1/real/" + fileName);
                continue;
            }

            ReporterTemp tmp = new ReporterTemp();
            tmp.set_id(new ObjectId(new Date()));
            tmp.setFilePath("/dagdata/data1/real/" + fileName);
            tmp.setDateTime(dateTime);
            insertList.add(tmp);

            if (i == resultList.size() -1) {
                flag = true;
            }
        }

        WriteTxtFile.writeTxtForError(errorData);

        if (flag) {
            System.out.println("照片信息提取结束，开始更新数据...");
            int temp = 1;
            BulkOperations ops = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, "reporter_temp");
            ops.insert(insertList);
            ops.execute();
            /*for (int i = 0; i < insertList.size(); i += 500) {
                System.out.println("======================进行第" + temp + "次批处理=======================");
                if (insertList.size() - i > 500) {
                    List<ReporterTemp> strings = insertList.subList(i, i + 500);
                    ops.insert(strings);
                    ops.execute();
                } else {
                    if (insertList.size() > i) {
                        List<ReporterTemp> strings = insertList.subList(i, insertList.size());
                        ops.insert(strings);
                        ops.execute();
                    }
                }
                temp += 1;
            }*/
        }
    }
}
