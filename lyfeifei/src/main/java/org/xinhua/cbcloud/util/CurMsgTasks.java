package org.xinhua.cbcloud.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.xinhua.cbcloud.pojo.DocLog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class CurMsgTasks implements Runnable {

    private Logger logger = LoggerFactory.getLogger(CurMsgTasks.class);

    private BlockingQueue<DocLog> blockingQueue;

    private BlockingQueue<List<IndexQuery>> blockingQueue2 = new ArrayBlockingQueue<>(10000);

    ElasticsearchTemplate elasticsearchTemplate;

    DocLog docLog;

    public CurMsgTasks(BlockingQueue<DocLog> blockingQueue, ElasticsearchTemplate elasticsearchTemplate, DocLog docLog) {
        this.docLog = docLog;
        this.blockingQueue = blockingQueue;
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public void run() {

        try {
            while (!blockingQueue.isEmpty()) {
                logger.info("当前线程：" + Thread.currentThread().getName() + " - 队列大小：" + blockingQueue.size());

                // 封装索引对象
                List<IndexQuery> indexQueries = new ArrayList<IndexQuery>();
                IndexQuery indexQuery = new IndexQuery();
                indexQuery.setId("11111");
                indexQuery.setObject(blockingQueue.take());
                indexQuery.setIndexName("doclog");
                indexQuery.setType("docs");
                indexQueries.add(indexQuery);

                if (blockingQueue2.size() != 10000) {
                    blockingQueue2.add(indexQueries);
                } else {
                    elasticsearchTemplate.bulkIndex(blockingQueue2.take());
                    System.out.println("成功插入10000条数据");
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
