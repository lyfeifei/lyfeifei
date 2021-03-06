package org.xinhua.cbcloud;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.ExtendedBounds;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.Cardinality;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.valuecount.InternalValueCount;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.junit4.SpringRunner;
import org.xinhua.cbcloud.pojo.DocLog;
import org.xinhua.cbcloud.repository.DocLogRepository;
import org.xinhua.cbcloud.util.DateUtil;
import org.xinhua.cbcloud.util.IDUtil;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LyfeifeiApplicationTests {

    private Logger logger = LoggerFactory.getLogger(LyfeifeiApplicationTests.class);

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    DocLogRepository docLogRepository;

    private static String indexName = "archieves_bus_search";

    private static String indexType = "archieve_type";

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

    @Test
    public void count1() throws Exception {
        SearchRequestBuilder searchRequestBuilder =
                elasticsearchTemplate.getClient().prepareSearch("archieves_bus_search_v5").setTypes("archieve_type");
        //todo 查询条件
        //可以增加where的查询条件
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.termQuery("articleType", "child"));
        queryBuilder.must(QueryBuilders.rangeQuery("insertTime").gte("2020-01-01 00:00:00").lte("2020-02-01 00:00:00"));
        //todo 根据筛选条件获取总量
       //如果只获取查询总数  可直接执行
        SearchResponse search = searchRequestBuilder.setQuery(queryBuilder).execute().actionGet();
        //通过search 中的total获取总数
        long totalHits = search.getHits().getTotalHits();
        System.out.println("查询统计：" + totalHits);
     /*  todo 不分组按照时间维度统计
     //不分组统计
        TermsBuilder tremsAggs = AggregationBuilders.terms("languageIdGroup").field("languageId").size(Integer.MAX_VALUE);
        DateHistogramBuilder dateHistogramAggregationBuilder= AggregationBuilders.dateHistogram("dateBucket").field("createDateTime");
//            DateHistogramInterval.MONTH //月份
//            DateHistogramInterval.QUARTER //季度
//            DateHistogramInterval.YEAR //年
        dateHistogramAggregationBuilder.interval(DateHistogramInterval.days(1))
                .format("yyyy-MM-dd HH");
        //执行查询
        SearchResponse response = searchRequestBuilder.setQuery(queryBuilder).addAggregation(dateHistogramAggregationBuilder)
                .execute().actionGet();
        Histogram histogram = response.getAggregations().get("dateBucket");
        for (Histogram.Bucket entry : histogram.getBuckets()) {
            String keyAsString = entry.getKeyAsString();
            System.out.println("时间" + keyAsString +  "总数" + entry.getDocCount());
        }*/

       /*  todo 分组统计
       TermsBuilder tremsAggs = AggregationBuilders.terms("languageIdGroup").field("languageId").size(Integer.MAX_VALUE);
        ValueCountBuilder countBuilder = AggregationBuilders.count("languageIdCount").field("languageId");
        tremsAggs.subAggregation(countBuilder);

        SearchResponse searchResponse = searchRequestBuilder.addAggregation(tremsAggs).execute().actionGet();
        Aggregation languageIdGroup = searchResponse.getAggregations().get("languageIdGroup");
        Terms timeAvgTerms;
        if (languageIdGroup instanceof Terms) {
            timeAvgTerms = (Terms) languageIdGroup;
            List<? extends Terms.Bucket> buckets = timeAvgTerms.getBuckets();
            for (Terms.Bucket elem : buckets) {
                InternalValueCount languageIdCount = elem.getAggregations().get("languageIdCount");
                String keyAsString = elem.getKeyAsString();
                System.out.println("语言：" + keyAsString + "总数" + languageIdCount);
            }
        }*/

      /* todo
      //分组增量统计
            TermsBuilder tremsAggs = AggregationBuilders.terms("languageIdGroup").field("languageId").size(Integer.MAX_VALUE);
            ValueCountBuilder countBuilder = AggregationBuilders.count("languageIdCount").field("languageId");
            tremsAggs.subAggregation(countBuilder);
        DateHistogramBuilder dateHistogramAggregationBuilder= AggregationBuilders.dateHistogram("dateBucket").field("createDateTime");
        dateHistogramAggregationBuilder.interval(DateHistogramInterval.days(1))
                .format("yyyy-MM-dd HH").subAggregation(tremsAggs);
        SearchResponse response = searchRequestBuilder.addAggregation(dateHistogramAggregationBuilder)
                .execute().actionGet();
        Histogram histogram = response.getAggregations().get("dateBucket");
        for (Histogram.Bucket entry : histogram.getBuckets()) {
            String keyAsString = entry.getKeyAsString();
            Aggregation languageIdGroup = entry.getAggregations().get("languageIdGroup");
            Terms timeAvgTerms;
            if (languageIdGroup instanceof Terms) {
                timeAvgTerms = (Terms) languageIdGroup;
                List<? extends Terms.Bucket> buckets = timeAvgTerms.getBuckets();
                for (Terms.Bucket elem : buckets) {
                    InternalValueCount languageIdCount = elem.getAggregations().get("languageIdCount");
                    String elemKey = elem.getKeyAsString();
                    System.out.println("时间" + keyAsString + "语言：" + elemKey + "总数" + languageIdCount.getValue());
                }
            }
        }*/
    }

    @Test
    public void count() throws Exception {

        long startTime = System.currentTimeMillis();

        SearchRequestBuilder searchRequestBuilder =
                elasticsearchTemplate.getClient().prepareSearch("archieves_bus_search").setTypes("archieve_type");

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.termQuery("articleType", "child"));
        queryBuilder.must(QueryBuilders.termQuery("plateSource", "0"));
        queryBuilder.must(QueryBuilders.rangeQuery("insertTime").gte("2020-01-01 00:00:00").lte("2020-12-30 23:59:59"));

        // 执行聚合
        DateHistogramAggregationBuilder dateHistogramAggregationBuilder =
                AggregationBuilders.dateHistogram("dateBucket").field("insertTime");

        Integer queryType = 1;
        if (queryType != null) {
            if (queryType == 1) {
                dateHistogramAggregationBuilder.dateHistogramInterval(DateHistogramInterval.WEEK).format("yyyy-MM-dd");
            } else if (queryType == 2) {
                dateHistogramAggregationBuilder.dateHistogramInterval(DateHistogramInterval.MONTH).format("yyyy-MM");
            } else if (queryType == 3) {
                dateHistogramAggregationBuilder.dateHistogramInterval(DateHistogramInterval.YEAR).format("yyyy");
            }
        }

        // 执行查询
        SearchResponse response = searchRequestBuilder.setQuery(queryBuilder).addAggregation(dateHistogramAggregationBuilder)
                .execute().actionGet();

        List<String> dates = new ArrayList<String>();

        int count = 0;

        Map<String, Object> resultMap = new HashMap<>();

        // 获取并遍历聚合结果，目前只按周、月、年来统计
        Histogram histogram = response.getAggregations().get("dateBucket");
        for (Histogram.Bucket entry : histogram.getBuckets()) {
            if (queryType == 1) {
                System.out.println(entry.getKeyAsString());
                dates.add(entry.getKeyAsString());
            }
            if (queryType == 2) {
                System.out.println(entry.getKeyAsString());
                dates.add(entry.getKeyAsString());
            }
            if (queryType == 3) {
                System.out.println(entry.getKeyAsString());
                dates.add(entry.getKeyAsString());
            }

            count += entry.getDocCount();
            System.out.println("遍历聚合结果： " + entry.getKeyAsString() + " 共：" + entry.getDocCount() + "个");
        }
        long endTime = System.currentTimeMillis();

        resultMap.put("dates", dates);
        resultMap.put("count", count);

        System.out.println("打印底片工作台总统计结果：dates = {}" + dates.toString() + "，count = {}" + count);

        System.out.println("执行统计耗时：" + (endTime - startTime));
    }

    @Test
    public void main() throws Exception {
        List<String> dates1 = new ArrayList<String>();
        dates1.add("2020-06-08");
        List<String> dates2 = new ArrayList<String>();
        dates2.add("2020-06-08");

        Set<Object> set = new HashSet<>();
        set.add(dates1.get(0));
        set.add(dates2.get(0));
        System.out.println(set.size());
    }

    @Test
    public void countUnFinishedPlateByDate() throws Exception {
        SearchRequestBuilder searchRequestBuilder =
                elasticsearchTemplate.getClient().prepareSearch("archieves_bus_search_v6").setTypes("archieve_type");

        String operationEnName = "Establish";
        String startTime = "2020-09-01 00:00:00";
        String endTime = "2020-09-07 00:00:00";

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.termQuery("articleType", "child"));
        queryBuilder.must(QueryBuilders.termQuery("operationEnName", operationEnName));
        queryBuilder.must(QueryBuilders.rangeQuery("insertTime").gte(startTime).lte(endTime));

        // 执行聚合
        DateHistogramAggregationBuilder dateHistogramAggregationBuilder =
                AggregationBuilders.dateHistogram("dateBucket").field("insertTime");

        dateHistogramAggregationBuilder.dateHistogramInterval(DateHistogramInterval.DAY).format("yyyy-MM-dd");

        // 底片号排重
        CardinalityAggregationBuilder cardinality = AggregationBuilders.cardinality("numCardinality").field("plateNum");

        // 执行查询
        SearchResponse response = searchRequestBuilder.setQuery(queryBuilder)
                .addAggregation(dateHistogramAggregationBuilder.subAggregation(cardinality)).execute().actionGet();

        Map<String, Object> tmpMap = new HashMap<>();
        List<Map<String, Object>> tmpList = new ArrayList<>();
        Histogram histogram = response.getAggregations().get("dateBucket");
        for (Histogram.Bucket entry : histogram.getBuckets()) {
            Cardinality sub = entry.getAggregations().get("numCardinality");
            tmpMap.put(entry.getKeyAsString(), sub.getValue());
        }
        tmpList.add(tmpMap);

        List<String> times = DateUtil.getBetweenTime(startTime, endTime);
        System.out.println(times.toString());

        for (int i = 0; i < times.size(); i++) {
            if (tmpMap.get(times.get(i)) == null) {
                tmpMap.put(times.get(i), "0");
                tmpList.get(0).put(times.get(i), "0");
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry map : tmpList.get(0).entrySet()) {
            Map<String, Object> resultTmp = new HashMap<>();
            resultTmp.put("date", map.getKey());
            resultTmp.put("value", map.getValue());
            result.add(resultTmp);
        }

        System.out.println("打印待编数据统计结果：{}" + result.toString());
    }

    @Test
    public void countByPlateSource() throws Exception {
        SearchRequestBuilder searchRequestBuilder =
                elasticsearchTemplate.getClient().prepareSearch("archieves_bus_search_v6").setTypes("archieve_type");

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        //queryBuilder.must(QueryBuilders.rangeQuery("plateSource").gte("1").lte("2"));
        //queryBuilder.must(QueryBuilders.termQuery("plateSource", "0"));
        //queryBuilder.should(QueryBuilders.termQuery("plateSource", "4"));
        queryBuilder.must(QueryBuilders.termQuery("articleType", "child"));
        queryBuilder.should(QueryBuilders.termQuery("plateLibId", "DAG_FINISHED_PLATE"));
        queryBuilder.should(QueryBuilders.termQuery("plateLibId", "DAG_UNFINISHED_PLATE"));

        /*//如果只获取查询总数  可直接执行
        SearchResponse search = searchRequestBuilder.setQuery(queryBuilder).execute().actionGet();
        //通过search 中的total获取总数
        long totalHits = search.getHits().getTotalHits();
        System.out.println("统计总量：" + totalHits);*/

        TermsAggregationBuilder tremsAggs = AggregationBuilders.terms("plateSourceGroup").field("plateSource").size(Integer.MAX_VALUE);
        ValueCountAggregationBuilder countBuilder = AggregationBuilders.count("plateSourceCount").field("plateSource");
        tremsAggs.subAggregation(countBuilder);

        SearchResponse searchResponse = searchRequestBuilder.setQuery(queryBuilder).addAggregation(tremsAggs).execute().actionGet();
        Aggregation languageIdGroup = searchResponse.getAggregations().get("plateSourceGroup");
        Terms timeAvgTerms;

        Map<String, Object> map = new HashMap<>();

        if (languageIdGroup instanceof Terms) {
            timeAvgTerms = (Terms) languageIdGroup;
            List<? extends Terms.Bucket> buckets = timeAvgTerms.getBuckets();
            String keyAsString = null;
            Long plateSourceCount = null;
            for (Terms.Bucket elem : buckets) {
                keyAsString = elem.getKeyAsString();
                plateSourceCount = elem.getDocCount();

                map.put(keyAsString, String.valueOf(plateSourceCount));
                System.out.println("来源：" + keyAsString + "， 总数：" + plateSourceCount);
            }
        }

        // 处理前端格式要求
        int count = 0;
        JSONObject countOneJso = new JSONObject();
        JSONObject countTwoJso = new JSONObject();
        JSONObject countThreeJso = new JSONObject();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String mapKey = entry.getKey();
            Long mapValue = Long.valueOf(entry.getValue().toString());
            // 社会征集：征集电子版、征集实体版
            if ("1".equals(mapKey) || "2".equals(mapKey)) {
                count += mapValue;
                countOneJso.put("name", "社会征集");
                countOneJso.put("value", count);
            }
            // 新华社记者
            if ("0".equals(mapKey)) {
                countTwoJso.put("name", "新华社记者");
                countTwoJso.put("value", mapValue);
            }
            // 底片著录
            if ("4".equals(mapKey)) {
                countThreeJso.put("name", "底片著录");
                countThreeJso.put("value", mapValue);
            }
        }

        // 拼装结果数据
        Map<String, Object> resultMap = new HashMap<>();
        List<JSONObject> plateSourceValue = new ArrayList<>();
        plateSourceValue.add(countOneJso);
        plateSourceValue.add(countTwoJso);
        plateSourceValue.add(countThreeJso);
        resultMap.put("plateSourceValue", plateSourceValue);
        List<String> plateSourceName = new ArrayList<>();
        plateSourceName.add("社会征集");
        plateSourceName.add("新华社记者");
        plateSourceName.add("底片著录");
        resultMap.put("plateSourceName", plateSourceName);

        System.out.println(resultMap.toString());
    }

    @Test
    public void testObjectArray() throws Exception {

        SearchRequestBuilder searchRequestBuilder =
                elasticsearchTemplate.getClient().prepareSearch("archieves_bus_search_v6").setTypes("archieve_type");

        String startTime = "2020-01-01 00:00:00";
        String endTime = "2020-08-01 00:00:00";

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.termQuery("articleType", "parent"));

        // 匹配数组对象
        NestedQueryBuilder nestedQuery = new NestedQueryBuilder("recDateTimeArr",
                new RangeQueryBuilder("recDateTimeArr.dataTime").gte(startTime).lte(endTime), ScoreMode.None);
        queryBuilder.must(nestedQuery);

        SearchResponse searchResponse = searchRequestBuilder.setQuery(queryBuilder).execute().actionGet();
        SearchHits searchHits = searchResponse.getHits();
        long totalHits = searchHits.getTotalHits();
        if (totalHits > 0) {
            Iterator<SearchHit> iterator = searchHits.iterator();
            while (iterator.hasNext()) {
                SearchHit searchHit = iterator.next();
                Map<String, Object> entityMap = searchHit.getSourceAsMap();
                System.out.println(entityMap.toString());
                //System.out.println("docId：" + entityMap.get("docId").toString());
                //System.out.println("recDateTimeArr：" + entityMap.get("recDateTimeArr").toString());
            }
        }
    }

    @Test
    public void testArray() throws Exception {

//        SearchRequestBuilder searchRequestBuilder =
//                elasticsearchTemplate.getClient().prepareSearch("archieves_bus_search").setTypes("archieve_type");

        String startTime = "2020-01-01 00:00:00";
        String endTime = "2020-12-31 00:00:00";

        List<String> list = new ArrayList<>();
        list.add("null");
        //list.add("Establish");
        //list.add("auditReturn");

        List<String> list2 = new ArrayList<>();
        list2.add("薛晓佳");
        list2.add("袁卫国");
        list2.add("庞元元");
        list2.add("安玉宏");
        list2.add("王晓洁");
        list2.add("孙敏");
        list2.add("蔡国兆");
        list2.add("陕西分社智库");
        list2.add("高增双");
        list2.add("盖博铭");
        list2.add("王勇军");
        list2.add("丁磊");
        list2.add("王明浩");
        list2.add("王阳");
        list2.add("才扬");
        list2.add("赵戈");
        list2.add("郭春菊");
        list2.add("马曹冉");
        list2.add("刘颖");
        list2.add("董季");
        list2.add("王阳");

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        //queryBuilder.must(QueryBuilders.termQuery("size", "10"));
        //queryBuilder.must(QueryBuilders.termQuery("page", "1"));
        //queryBuilder.must(QueryBuilders.termQuery("queryWay", "1"));
        queryBuilder.must(QueryBuilders.termQuery("articleType", "parent"));
        queryBuilder.must(QueryBuilders.termQuery("docLibId", "DAG_FINISHED"));
        queryBuilder.must(QueryBuilders.termsQuery("upLoader", list2));
        queryBuilder.must(
                QueryBuilders.boolQuery()
                        .should(QueryBuilders.termQuery("operationEnName", list))
                        .should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("operationEnName")))
        );
        //queryBuilder.must(QueryBuilders.rangeQuery("insertTime").gte(startTime).lte(endTime));

//        SearchResponse searchResponse =
//                searchRequestBuilder.addSort("issueDateTime", SortOrder.DESC).setFrom(1).setSize(10).setQuery(queryBuilder).execute().actionGet();
//        System.out.println(queryBuilder.toString());

        SearchRequestBuilder searchRequestBuilder =
                elasticsearchTemplate.getClient().prepareSearch("archieves_bus_search").setTypes("archieve_type").setQuery(queryBuilder);

        // 按发稿时间倒序
        searchRequestBuilder.addSort("issueDateTime", SortOrder.DESC);

        SearchResponse searchResponse = searchRequestBuilder.setFrom(1).setSize(10).execute().actionGet();


        SearchHits searchHits = searchResponse.getHits();
        long totalHits = searchHits.getTotalHits();
        System.out.println("数据总量：" + totalHits);
        if (totalHits > 0) {
            Iterator<SearchHit> iterator = searchHits.iterator();
            while (iterator.hasNext()) {
                SearchHit searchHit = iterator.next();
                Map<String, Object> entityMap = searchHit.getSourceAsMap();
                System.out.println("打印查询结果：" + entityMap.toString());
            }
        } else {
            System.out.println("暂无数据");
        }
    }

    @Test
    public void testArray2() throws Exception {

        String startTime = "2020-01-01 00:00:00";
        String endTime = "2020-12-31 00:00:00";

        List<String> list = new ArrayList<>();
        //list.add("120");
        //list.add("135");
        //list.add("150");
        //list.add("其他");
        list.add("null");
        //list.add("recall");
        list.add("Establish");
        //list.add("Return");
        //list.add("Audited");


        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        //queryBuilder.must(QueryBuilders.termQuery("size", "10"));
        //queryBuilder.must(QueryBuilders.termQuery("page", "1"));
        //queryBuilder.must(QueryBuilders.termQuery("queryWay", "1"));
        //queryBuilder.must(QueryBuilders.termQuery("articleType", "child"));
//        queryBuilder.must(
//            QueryBuilders.boolQuery()
//                .should(QueryBuilders.termQuery("operationEnName", "Establish"))
//                .should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("operationEnName"))));
        //queryBuilder.must(QueryBuilders.termQuery("operationEnName", "Establish"));
        //queryBuilder.mustNot(QueryBuilders.existsQuery("operationEnName"));

        //queryBuilder.must(QueryBuilders.rangeQuery("fileSize").gte(0).lte(3145728));

        if (list.contains("null") || list.contains(null)) {
            list.remove("null");
            System.out.println(list);
            queryBuilder.must(
                    QueryBuilders.boolQuery()
                            .should(QueryBuilders.termsQuery("operationEnName", list))
                            .should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("operationEnName")))
            );
        } else {
            queryBuilder.must(QueryBuilders.termsQuery("operationEnName", list));
        }

        SearchRequestBuilder searchRequestBuilder =
                elasticsearchTemplate.getClient().prepareSearch("archieves_bus_search").setTypes("archieve_type").setQuery(queryBuilder);

        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

        SearchHits searchHits = searchResponse.getHits();
        long totalHits = searchHits.getTotalHits();
        System.out.println("数据总量：" + totalHits);
        if (totalHits > 0) {
            Iterator<SearchHit> iterator = searchHits.iterator();
            while (iterator.hasNext()) {
                SearchHit searchHit = iterator.next();
                Map<String, Object> entityMap = searchHit.getSourceAsMap();
                System.out.println("打印查询结果：" + entityMap.toString());
            }
        } else {
            System.out.println("暂无数据");
        }
    }

    @Test
    public void selectOne() throws Exception {

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.termQuery("articleType", "child"));
        queryBuilder.must(QueryBuilders.termQuery("docLibId", "DAG_FINISHED"));
        queryBuilder.must(QueryBuilders.termQuery("plateNum", "PEA00888919"));

        SearchRequestBuilder searchRequestBuilder =
                elasticsearchTemplate.getClient().prepareSearch("archieves_bus_search").setTypes("archieve_type").setQuery(queryBuilder);

        SearchResponse searchResponse = searchRequestBuilder.setQuery(queryBuilder).execute().actionGet();

        JSONObject doc = new JSONObject();

        SearchHits searchHits = searchResponse.getHits();
        long totalHits = searchHits.getTotalHits();
        System.out.println("数据总量：" + totalHits);
        if (totalHits > 0) {
            Iterator<SearchHit> iterator = searchHits.iterator();
            while (iterator.hasNext()) {
                SearchHit searchHit = iterator.next();
                doc = JSONObject.parseObject(searchHit.getSourceAsString());
            }
        } else {
            System.out.println("暂无数据");
        }

        System.out.println("打印查询结果：" + doc.toJSONString());
    }

    @Test
    public void test1() {

        String insertTimeStart = "2020-01-01 00:00:00";
        String insertTimeEnd = "2020-12-30 00:00:00";

        SearchRequestBuilder searchRequestBuilder =
                elasticsearchTemplate.getClient().prepareSearch(indexName).setTypes(indexType);

        List<String> list = new ArrayList<>();
        list.add("Establish");
        list.add("ToAudit");
        list.add("Audited");

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.termQuery("plateSource", "0"));
        queryBuilder.must(QueryBuilders.termQuery("articleType", "child"));
        queryBuilder.must(QueryBuilders.termsQuery("operationEnName", list));
        queryBuilder.must(QueryBuilders.rangeQuery("insertTime").gte(insertTimeStart).lte(insertTimeEnd));

        // 执行聚合
        DateHistogramAggregationBuilder dateHistogramAggregationBuilder =
                AggregationBuilders.dateHistogram("dateBucket").field("insertTime");

        Integer queryType = 3;
        if (queryType != null) {
            if (queryType == 1) {
                dateHistogramAggregationBuilder.dateHistogramInterval(DateHistogramInterval.WEEK).format("yyyy-MM-dd");
            } else if (queryType == 2) {
                dateHistogramAggregationBuilder.dateHistogramInterval(DateHistogramInterval.MONTH).format("yyyy-MM");
            } else if (queryType == 3) {
                dateHistogramAggregationBuilder.dateHistogramInterval(DateHistogramInterval.YEAR).format("yyyy");
            }
        }

        SearchResponse response = searchRequestBuilder.setQuery(queryBuilder).addAggregation(dateHistogramAggregationBuilder)
                .execute().actionGet();

        List<String> dates = new ArrayList<String>();

        List<Object> count = new ArrayList<>();

        Map<String, Object> resultMap = new HashMap<>();

        int sum = 0;
        // 获取并遍历聚合结果，目前只按周、月、年来统计
        Histogram histogram = response.getAggregations().get("dateBucket");
        for (Histogram.Bucket entry : histogram.getBuckets()) {
            if (queryType == 1) {
                dates.add(entry.getKeyAsString());
            }
            if (queryType == 2) {
                dates.add(entry.getKeyAsString());
            }
            if (queryType == 3) {
                dates.add(entry.getKeyAsString());
            }

            sum += entry.getDocCount();
            count.add(entry.getDocCount());
        }

        System.out.println("总计数：" + sum);

        resultMap.put("dates", dates);
        resultMap.put("count", count);

        System.out.println("打印底片工作台记者上传统计结果：" + resultMap.toString());
    }

    @Test
    public void AggregationBuilders() {

        SearchRequestBuilder searchRequestBuilder =
                elasticsearchTemplate.getClient().prepareSearch(indexName).setTypes(indexType);

        String operationEnName = "ToAudit";
        String startTime = "2020-01-01 00:00:00";
        String endTime = "2020-12-31 00:00:00";

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        // INFMX-PEA00959028
        queryBuilder.must(QueryBuilders.termQuery("plateNum", "PEA00959028"));
        //queryBuilder.must(QueryBuilders.termQuery("articleType", "child"));
        //queryBuilder.must(QueryBuilders.termQuery("operationEnName", operationEnName));
        queryBuilder.must(QueryBuilders.rangeQuery("toAuditTime").gte(startTime).lte(endTime));

        // 执行聚合
        DateHistogramAggregationBuilder dateHistogramAggregationBuilder =
                AggregationBuilders.dateHistogram("dateBucket").field("toAuditTime");

        dateHistogramAggregationBuilder.dateHistogramInterval(DateHistogramInterval.DAY).format("yyyy-MM-dd");

        // 底片号排重
        CardinalityAggregationBuilder cardinality = AggregationBuilders.cardinality("numCardinality").field("plateNum");

        // .addAggregation(cardinality)
        SearchResponse response = searchRequestBuilder.setQuery(queryBuilder)
                .addAggregation(dateHistogramAggregationBuilder.subAggregation(cardinality)).execute().actionGet();

        int count = 0;

//        Cardinality cardinalityResponse = response.getAggregations().get("numCardinality");
//        System.out.println("排重统计结果：" + cardinalityResponse.getValue());

        Histogram histogram = response.getAggregations().get("dateBucket");
        for (Histogram.Bucket entry : histogram.getBuckets()) {
            Cardinality sub = entry.getAggregations().get("numCardinality");
            System.out.println(entry.getKeyAsString() + " # " + sub.getValue());
        }

    }

    @Test
    public void demo() {

        SearchRequestBuilder searchRequestBuilder =
                elasticsearchTemplate.getClient().prepareSearch(indexName).setTypes(indexType);

        String operationEnName = "ToAudit";
        String startTime = "2020-01-01 00:00:00";
        String endTime = "2020-12-31 00:00:00";

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        // INFMX-PEA00959028
        queryBuilder.must(QueryBuilders.termQuery("plateNum", "PEA00959028"));
        queryBuilder.must(QueryBuilders.rangeQuery("toAuditTime").gte(startTime).lte(endTime));

        // 底片号排重
        CardinalityAggregationBuilder cardinality = AggregationBuilders.cardinality("numCardinality").field("plateNum");

        // .addAggregation(cardinality)
        SearchResponse response = searchRequestBuilder.setQuery(queryBuilder).addAggregation(cardinality).execute().actionGet();

        Cardinality cardinalityResponse = response.getAggregations().get("numCardinality");
        System.out.println("排重统计结果：" + cardinalityResponse.getValue());
    }

    // 原始数据上传模块
    @Test
    public void demo2() {
        SearchRequestBuilder searchRequestBuilder =
                elasticsearchTemplate.getClient().prepareSearch(indexName).setTypes(indexType);

        String insertTimeStart = "2020-01-01 00:00:00";
        String insertTimeEnd = "2020-12-31 00:00:00";

        List<String> list = new ArrayList<>();
        list.add("Establish");
        list.add("ToAudit");
        list.add("Audited");

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.termQuery("plateSource", "0"));
        queryBuilder.must(QueryBuilders.termQuery("articleType", "child"));
        queryBuilder.must(QueryBuilders.termsQuery("operationEnName", list));
        queryBuilder.must(QueryBuilders.rangeQuery("issueDateTime").gte(insertTimeStart).lte(insertTimeEnd));

        // 执行聚合
        DateHistogramAggregationBuilder dateHistogramAggregationBuilder =
                AggregationBuilders.dateHistogram("dateBucket").field("issueDateTime");

        // 底片号排重
        CardinalityAggregationBuilder cardinality = AggregationBuilders.cardinality("numCardinality").field("plateNum");

        Integer queryType = 1;
        if (queryType != null) {
            if (queryType == 1) {
                dateHistogramAggregationBuilder.dateHistogramInterval(DateHistogramInterval.WEEK).format("yyyy-MM-dd");
            } else if (queryType == 2) {
                dateHistogramAggregationBuilder.dateHistogramInterval(DateHistogramInterval.MONTH).format("yyyy-MM");
            } else if (queryType == 3) {
                dateHistogramAggregationBuilder.dateHistogramInterval(DateHistogramInterval.YEAR).format("yyyy");
            }
        }

        long value = 0L;

        SearchResponse response = searchRequestBuilder.setQuery(queryBuilder)
                .addAggregation(dateHistogramAggregationBuilder.subAggregation(cardinality)).execute().actionGet();

        // year 2020 # 187,  week  193
        Histogram histogram = response.getAggregations().get("dateBucket");
        for (Histogram.Bucket entry : histogram.getBuckets()) {
            Cardinality sub = entry.getAggregations().get("numCardinality");
            value += sub.getValue();
            System.out.println(entry.getKeyAsString() + " # " + sub.getValue());
        }
        System.out.println(value);
    }

    // 记者上传模块
    @Test
    public void demo3() {
        SearchRequestBuilder searchRequestBuilder =
                elasticsearchTemplate.getClient().prepareSearch(indexName).setTypes(indexType);

        List<String> upOperationEnNameArray = new ArrayList<>();
        upOperationEnNameArray.add("no-plate");

        String startTime = "2020-01-01 00:00:00";
        String endTime = "2020-12-31 00:00:00";

        // 组织机构
        List<String> sourceArray = new ArrayList<>();

        // 记者
        List<String> upLoaderArray = new ArrayList<>();

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.termQuery("plateSource", "0"));
        queryBuilder.must(QueryBuilders.termQuery("articleType", "child"));
        queryBuilder.must(QueryBuilders.termsQuery("operationEnName", upOperationEnNameArray));
        queryBuilder.must(QueryBuilders.rangeQuery("issueDateTime").gte(startTime).lte(endTime));
        /*if (sourceArray != null && sourceArray.size() > 0) {
            queryBuilder.must(QueryBuilders.termsQuery("source", sourceArray));
        }
        if (upLoaderArray != null && upLoaderArray.size() > 0) {
            queryBuilder.must(QueryBuilders.termsQuery("upLoader", upLoaderArray));
        }*/

        // 执行聚合
        DateHistogramAggregationBuilder dateHistogramAggregationBuilder =
                AggregationBuilders.dateHistogram("dateBucket").field("issueDateTime");

        dateHistogramAggregationBuilder.dateHistogramInterval(DateHistogramInterval.WEEK).format("yyyy-MM-dd");

        // 底片号排重
        CardinalityAggregationBuilder cardinality = AggregationBuilders.cardinality("numCardinality").field("plateNum");

        SearchResponse response = searchRequestBuilder.setQuery(queryBuilder)
                .addAggregation(dateHistogramAggregationBuilder.subAggregation(cardinality)).execute().actionGet();

        long value = 0L;

        // year 2020 # 186,  week  193
        Histogram histogram = response.getAggregations().get("dateBucket");
        long totalShards = response.getHits().getTotalHits();
        System.out.println(totalShards);
        for (Histogram.Bucket entry : histogram.getBuckets()) {
            Cardinality sub = entry.getAggregations().get("numCardinality");
            value += sub.getValue();
            System.out.println(entry.getKeyAsString() + " # " + sub.getValue());
        }
        System.out.println(value);
    }
}
