//package org.xinhua.cbcloud.util;
//
//import com.alibaba.fastjson.JSONObject;
//import org.bson.Document;
//import org.elasticsearch.action.search.SearchRequestBuilder;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.client.Client;
//import org.elasticsearch.common.unit.TimeValue;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.search.SearchHits;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ConfigurableApplicationContext;
//import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
//import org.springframework.data.mongodb.core.MongoTemplate;
//
//import javax.annotation.PostConstruct;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//
//public class DataClean {
//
//    private final ElasticsearchTemplate elasticsearchTemplate;
//    private final Client client;
//    private final ApplicationContext context;
//    private final MongoTemplate mongoTemplate;
//
//    public static int CURRENT_PAGE = 0;
//    public static final int SCROLL_TIME = 30000;
//    private static SearchRequestBuilder responsebuilder;
//    private static int MAX_PAGE;
//    private static String SCROLL_ID;
//    public static final String COLLECTION_NAME = "DAG_FINISHED";
//
//    private static Logger log = LoggerFactory.getLogger(DataClean.class);
//
//    @Autowired
//    public DataClean(ElasticsearchTemplate elasticsearchTemplate,
//                     ApplicationContext context, MongoTemplate mongoTemplate) {
//        this.elasticsearchTemplate = elasticsearchTemplate;
//        this.client = elasticsearchTemplate.getClient();
//        this.context = context;
//        this.mongoTemplate = mongoTemplate;
//    }
//
//    @PostConstruct
//    public void init() {
//        log.info("HistoryServiceImpl开始初始化");
//        responsebuilder = client.prepareSearch("archieves_bus_input")
//                .setTypes("archieve_type").
//                        setSize(30).setScroll(new TimeValue(SCROLL_TIME)).
//                        setQuery(QueryBuilders.boolQuery().
//                                must(QueryBuilders.wildcardQuery("entityId", "null*"))
//                                .must(QueryBuilders.termQuery("articleType", "child"))
//                                .must(QueryBuilders.termQuery("storePosition", 1)))
//                .setFetchSource("docId", "_id");
//        SearchResponse response = responsebuilder.execute().actionGet();
//        SCROLL_ID = response.getScrollId();
//        long totalCount = response.getHits().getTotalHits();
//        log.info("totalCount:" + totalCount);
//        MAX_PAGE = (int) totalCount / 30;
//        CURRENT_PAGE = 0;
//        repairEsData(scrollOutput(response));
//    }
//
//    public void repair(JSONObject key) {
//        if (CURRENT_PAGE >= MAX_PAGE) {
//            ConfigurableApplicationContext ctx = (ConfigurableApplicationContext) context;
//            log.info("已无符合条件的数据,关闭程序");
//            ctx.close();
//            return;
//        }
//        List<EsDTO> esData = findEsData();
//        repairEsData(esData);
//
//    }
//
//    public List<EsDTO> findEsData() {
//        SearchResponse response = client.prepareSearchScroll(SCROLL_ID)
//                .setScroll(new TimeValue(SCROLL_TIME)).execute()
//                .actionGet();
//        return scrollOutput(response);
//    }
//
//    public void repairEsData(List<EsDTO> esData) {
//        log.info("进入repairEsData");
//        for (EsDTO esDatum : esData) {
//            JSONObject params = new JSONObject();
//            params.put("tableName", COLLECTION_NAME);
//            params.put("docLibId", 1);
//            params.put("docId", esDatum.getDocId());
//            task.retryPreterament(params);
//        }
//    }
//
//    public void repairMongoData(List<Document> mongoData) {
//
//    }
//
//    public List<Document> findMongoData(Document query) {
//        return null;
//    }
//
//    public static List<EsDTO> scrollOutput(SearchResponse response) {
//        log.info("进入scrollOutput");
//        SearchHits hits = response.getHits();
//        List<EsDTO> esData = new ArrayList<>();
//        JSONObject temp = new JSONObject();
//        for (int j = 0; j < hits.getHits().length; j++) {
//            try {
//                Map<String, Object> data = hits.getHits()[j].getSource();
//                temp.putAll(data);
//                EsDTO es = new EsDTO();
//                es.setDocId(temp.getString("docId"));
//                esData.add(es);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
//        CURRENT_PAGE++;
//        return esData;
//    }
//}
