package org.xinhua.cbcloud;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.test.context.junit4.SpringRunner;
import org.xinhua.cbcloud.pojo.DocLog;
import org.xinhua.cbcloud.repository.DocLogRepository;
import org.xinhua.cbcloud.util.IDUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LyfeifeiApplicationTests {

    private Logger logger = LoggerFactory.getLogger(LyfeifeiApplicationTests.class);

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    DocLogRepository docLogRepository;

    @Test
    public void createIndex() {
        // 创建索引
        System.out.println(elasticsearchTemplate.createIndex(DocLog.class));
    }

    @Test
    public void deleteIndex() throws Exception {
        System.out.println(elasticsearchTemplate.deleteIndex(DocLog.class));
    }

    @Test
    public void insert() throws Exception {

        int counter = 0;

        logger.info("开始执行");

        List<DocLog> docLogs = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            DocLog docLog = new DocLog();
            docLog.setId(IDUtil.getId());
            docLog.setDocId(String.valueOf(IDUtil.getId()));
            docLog.setMessageId("1569482824826#b807cc4a-6258-4f61-a445-c97006d9512c");
            docLog.setContext("111111");
            docLogs.add(docLog);
        }

        // 索引队列
        List<IndexQuery> indexQueries = new LinkedList<>();

        for (DocLog docLog : docLogs) {
            IndexQuery indexQuery = new IndexQuery();
            indexQuery.setId(String.valueOf(IDUtil.getId()));
            indexQuery.setObject(docLog);
            indexQuery.setIndexName("doclog");
            indexQuery.setType("docs");
            indexQueries.add(indexQuery);

            if (counter % 5000 == 0) {
                elasticsearchTemplate.bulkIndex(indexQueries);
                indexQueries.clear();
            }
            counter++;
        }

        if (indexQueries.size() > 0) {
            elasticsearchTemplate.bulkIndex(indexQueries);
        }

        logger.info("执行结束");

        /*************************************************************************************************************************/

        /*DocLog docLog = new DocLog();
        docLog.setId(IDUtil.getId());
        docLog.setDocId(String.valueOf(IDUtil.getId()));
        docLog.setMessageId("1569482824826#b807cc4a-6258-4f61-a445-c97006d9512c");
        docLog.setContext("111111");

        BlockingQueue<DocLog> blockingQueue = new LinkedBlockingQueue<>();

        // 消息生产
        for (int i = 0; i < 5; i++) {
            ThreadUtil.getInstance().addTask(new ProMsgTasks(blockingQueue, docLog));
        }

        Thread.sleep(30000);
        // 消息消费
        for (int i = 0; i < 1; i++) {
            ThreadUtil.getInstance().addTask(new CurMsgTasks(blockingQueue, elasticsearchTemplate, docLog));
        }*/

    }

    @Test
    public void concurrent() throws Exception {

        /*// 任务队列
        BlockingQueue blockingQueue = new LinkedBlockingQueue<>();

        // 消息生产
        for (int i = 0; i < 1; i++) {
            ThreadUtil.getInstance().addTask(new ProMsgTasks(blockingQueue));
        }

        // 消息消费
        for (int i = 0; i < 1; i++) {
            ThreadUtil.getInstance().addTask(new CurMsgTasks(blockingQueue));
        }*/
    }
}
