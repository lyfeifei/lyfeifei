package org.xinhua.cbcloud.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryAction;
import org.elasticsearch.index.reindex.UpdateByQueryRequestBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.util.Pair;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;
import org.xinhua.cbcloud.ftp.FtpJSch;
import org.xinhua.cbcloud.pojo.ReporterTemp;
import org.xinhua.cbcloud.pojo.User;
import org.xinhua.cbcloud.util.ReadTxtFile;
import org.xinhua.cbcloud.util.WriteTxtFile;

import java.util.*;
import java.util.function.Consumer;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ESUtil {

    private static String indexName = "archieves_bus_search";

    private static String indexType = "archieve_type";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void testAdd() {
        User user = new User();
        user.setName("admin");
        user.setAddress("bj");
        mongoTemplate.save(user, "user");
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

    @Test
    public void select() throws Exception {

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.termQuery("articleType", "child"));
        queryBuilder.must(QueryBuilders.termQuery("operationEnName", "Establish"));

        SearchRequestBuilder searchRequestBuilder =
                elasticsearchTemplate.getClient().prepareSearch("archieves_bus_search").setTypes("archieve_type").setQuery(queryBuilder);

        SearchResponse searchResponse = searchRequestBuilder.setFrom(1).setSize(15000).execute().actionGet();

        int i = 0;
        JSONObject doc = new JSONObject();
        List<String> list = new ArrayList<>();

        SearchHits searchHits = searchResponse.getHits();
        long totalHits = searchHits.getTotalHits();
        System.out.println("数据总量：" + totalHits);
        if (totalHits > 0) {
            Iterator<SearchHit> iterator = searchHits.iterator();
            while (iterator.hasNext()) {
                i++;
                SearchHit searchHit = iterator.next();
                doc = JSONObject.parseObject(searchHit.getSourceAsString());
                list.add(doc.getString("docId"));
                System.out.println("当前处理第" + i + "次");
            }
        } else {
            System.out.println("暂无数据");
        }

        WriteTxtFile.writeTxt(list);
        //System.out.println("打印查询结果：" + doc.toJSONString());
    }

    @Test
    public void checkNull() throws Exception {
        List<String> data = new ArrayList<>();
        List<String> list = ReadTxtFile.readTxtForList();
        List<JSONObject> resultList = new ArrayList<>();
        for (int i = 0; i < list.size(); i ++) {
            System.out.println("======================进行第" + i + "次处理=======================");
            Criteria criteria = Criteria.where("docIds").is(list.get(i));
            criteria.and("filePath").exists(false);
            criteria.and("insertTime").exists(false);
            resultList = mongoTemplate.find(Query.query(criteria), JSONObject.class, "DAG_UNFINISHED_PLATE");
            if (resultList.size() > 0) {
                data.add(list.get(i));
            } else if (resultList.size() == 0) {
                resultList = mongoTemplate.find(Query.query(criteria), JSONObject.class, "DAG_FINISHED_PLATE");
                if (resultList.size() > 0) {
                    data.add(list.get(i));
                }
            }
            System.out.println(resultList);
        }
        System.out.println("统计数据：" + data.size());
        WriteTxtFile.writeTxt(data);
    }

    @Test
    public void txt() throws Exception {
        Criteria criteria = Criteria.where("docIds").is("101002020040190001628");
        criteria.and("filePath").exists(false);
        criteria.and("insertTime").exists(false);
        List<JSONObject> resultList = mongoTemplate.find(Query.query(criteria), JSONObject.class, "DAG_UNFINISHED_PLATE");
        System.out.println(resultList.size() > 0);
    }

    @Test
    public void dataPadding() {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.termQuery("docId", "101002020071190001042"));
        queryBuilder.must(QueryBuilders.termQuery("articleType", "child"));

        SearchRequestBuilder searchRequestBuilder =
                elasticsearchTemplate.getClient().prepareSearch("archieves_bus_search").setTypes("archieve_type").setQuery(queryBuilder);

        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

        JSONObject doc = new JSONObject();
        JSONObject mongo = new JSONObject();

        SearchHits searchHits = searchResponse.getHits();
        long totalHits = searchHits.getTotalHits();
        System.out.println("数据总量：" + totalHits);
        if (totalHits > 0) {
            Iterator<SearchHit> iterator = searchHits.iterator();
            while (iterator.hasNext()) {
                SearchHit searchHit = iterator.next();
                doc = JSONObject.parseObject(searchHit.getSourceAsString());

                String docId = doc.getString("docId");
                List list = new ArrayList();
                list.add(docId);
                mongo.put("docId", docId);
                mongo.put("docIds", list);
                mongo.put("plateId", doc.getString("plateId"));
                mongo.put("plateNum", doc.getString("plateNum"));
                mongo.put("plateType", doc.getString("plateType"));
                mongo.put("photoPlateNum", doc.getString("photoPlateNum"));
                mongo.put("picId", doc.getString("picId"));
                mongo.put("plateLibId", doc.getString("plateLibId"));
                mongo.put("plateSource", doc.getString("plateSource"));
                System.out.println("抽取出的数据：" + mongo.toJSONString());
                mongoTemplate.save(mongo, "DAG_UNFINISHED_PLATE");
            }
        } else {
            System.out.println("暂无数据");
        }
    }

    @Test
    public void selectTest() {

        List<String> list = new ArrayList<>();
        list.add("11");
        list.add("22");

        Client client = elasticsearchTemplate.getClient();

        UpdateByQueryRequestBuilder updateByQuery = UpdateByQueryAction.INSTANCE.newRequestBuilder(client);
        updateByQuery.source("archieves_bus_search")
                //查询要修改的结果集
                .filter(QueryBuilders.termQuery("articleType", "child"))
                .filter(QueryBuilders.termsQuery("docId", list))
                //修改操作
                .script(new Script( "ctx._source['operationEnName']="+ null));
        //响应结果集
        BulkByScrollResponse response = updateByQuery.get();
    }

    @Test
    public void bulkIndexUpate() {

        List<String> list = ReadTxtFile.readTxtForList();

        Client client = elasticsearchTemplate.getClient();

        int temp = 1;
        for (int i = 0; i < list.size(); i += 1000) {
            System.out.println("======================进行第" + temp + "次批处理=======================");
            if (list.size() - i > 1000) {
                List<String> strings = list.subList(i, i + 1000);
                UpdateByQueryRequestBuilder updateByQuery = UpdateByQueryAction.INSTANCE.newRequestBuilder(client);
                updateByQuery.source("archieves_bus_search")
                        //查询要修改的结果集
                        .filter(QueryBuilders.termQuery("articleType", "child"))
                        .filter(QueryBuilders.termsQuery("docId", strings))
                        //修改操作 new Script( "ctx._source['operationEnName']="+ "'Establish'")
                        .script(new Script( "ctx._source['operationEnName']="+ null));
                //响应结果集
                BulkByScrollResponse response = updateByQuery.get();
            } else {
                if (list.size() > i) {
                    List<String> strings = list.subList(i, list.size());
                    UpdateByQueryRequestBuilder updateByQuery = UpdateByQueryAction.INSTANCE.newRequestBuilder(client);
                    updateByQuery.source("archieves_bus_search")
                            //查询要修改的结果集
                            .filter(QueryBuilders.termQuery("articleType", "child"))
                            .filter(QueryBuilders.termsQuery("docId", strings))
                            //修改操作
                            .script(new Script( "ctx._source['operationEnName']="+ null));
                    //响应结果集
                    BulkByScrollResponse response = updateByQuery.get();
                }
            }
            temp += 1;
        }
    }

    @Test
    public void tmp() {

        List<String> list = ReadTxtFile.readTxtForList();

        SearchRequestBuilder searchRequestBuilder =
                elasticsearchTemplate.getClient().prepareSearch("archieves_bus_search").setTypes("archieve_type");

        List<Object> resultList1 = new ArrayList<>();

        List<Object> resultList2 = new ArrayList<>();

        int temp = 1;
        for (int i = 0; i < list.size(); i++) {
            System.out.println("======================进行第" + temp + "次处理=======================");
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            queryBuilder.must(QueryBuilders.termQuery("articleType", "child"));
            queryBuilder.must(QueryBuilders.termQuery("docId", list.get(i)));

            SearchResponse searchResponse = searchRequestBuilder.setQuery(queryBuilder).execute().actionGet();
            SearchHits searchHits = searchResponse.getHits();
            long totalHits = searchHits.getTotalHits();
            //System.out.println("数据总量：" + totalHits);
            if (totalHits > 0) {
                String docId = null;
                Iterator<SearchHit> iterator = searchHits.iterator();
                while (iterator.hasNext()) {
                    SearchHit searchHit = iterator.next();
                    docId = searchHit.getSourceAsMap().get("docId").toString();
                    String photoPlateNum = searchHit.getSourceAsMap().get("photoPlateNum").toString();
                    resultList1.add(docId + "#" + photoPlateNum);
                }
            } else {
                System.out.println("暂无数据：" + list.get(i));
            }

            //WriteTxtFile.write(resultList2, "error_data2.txt");
            temp += 1;
        }
        WriteTxtFile.write(resultList1, "error_data1.txt");
    }

    @Test
    public void selectErrorData() {

        List<String> list = ReadTxtFile.readTxtForList();

        SearchRequestBuilder searchRequestBuilder =
                elasticsearchTemplate.getClient().prepareSearch("archives_log_index").setTypes("log_type");

        List<Object> resultList1 = new ArrayList<>();

        List<Object> resultList2 = new ArrayList<>();

        int temp = 1;
        for (int i = 0; i < list.size(); i++) {
            System.out.println("======================进行第" + temp + "次处理=======================");
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            queryBuilder.must(QueryBuilders.termQuery("docId", list.get(i)));

            SearchResponse searchResponse = searchRequestBuilder.setQuery(queryBuilder).execute().actionGet();
            SearchHits searchHits = searchResponse.getHits();
            long totalHits = searchHits.getTotalHits();
            //System.out.println("数据总量：" + totalHits);
            if (totalHits > 0) {
                String docId = null;
                List tmp = new ArrayList();
                Iterator<SearchHit> iterator = searchHits.iterator();
                while (iterator.hasNext()) {
                    SearchHit searchHit = iterator.next();
                    docId = searchHit.getSourceAsMap().get("docId").toString();
                    String subModule = searchHit.getSourceAsMap().get("subModule").toString();
                    tmp.add(subModule);
                }
                if (tmp.contains("记者工作台-我的发稿照片")) {
                    resultList1.add(docId);
                } else {
                    resultList2.add(docId);
                }
            } else {
                System.out.println("暂无数据：" + list.get(i));
            }
            WriteTxtFile.write(resultList1, "error_data1.txt");
            WriteTxtFile.write(resultList2, "error_data2.txt");
            temp += 1;
        }
    }

    @Test
    public void selectFetchFile() {
        String[] fields = new String[2];
        fields[0] = "docId";
        fields[1] = "photoPlateNum";

        SearchRequestBuilder searchRequestBuilder =
                elasticsearchTemplate.getClient()
                        .prepareSearch("archieves_bus_search").setTypes("archieve_type").setFetchSource(fields, null);

        List<String> resultList = new ArrayList<>();

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.termQuery("articleType", "child"));

        String docId = null;
        String photoPlateNum = null;
        SearchResponse searchResponse = searchRequestBuilder.setQuery(queryBuilder).setFrom(5).setSize(10).execute().actionGet();
        SearchHits searchHits = searchResponse.getHits();
        long totalHits = searchHits.getTotalHits();
        System.out.println("数据总量：" + totalHits);
        if (totalHits > 0) {
            for (SearchHit hit : searchHits) {
                docId = (String) hit.getSourceAsMap().get("docId");
                photoPlateNum = (String) hit.getSourceAsMap().get("photoPlateNum");
                System.out.println(hit.getSourceAsMap());
                //System.out.println("docId: " + docId + ", photoPlateNum: " + photoPlateNum);
            }
        } else {
            System.out.println("暂无数据");
        }
        WriteTxtFile.writeTxt(resultList);
    }

    @Test
    public void EsScroll() {

        int i = 1;
        long begin = System.currentTimeMillis();

        SearchRequestBuilder searchRequestBuilder = elasticsearchTemplate.getClient().prepareSearch(indexName).setTypes(indexType);

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.termQuery("articleType", "child"));

        Object[] obj = null;

        SearchResponse response = searchRequestBuilder.setFetchSource(new String[]{"docId", "photoPlateNum"}, null)
                .setFrom(0).setSize(20000).addSort("docId", SortOrder.DESC).setQuery(queryBuilder).execute().actionGet();

        String docId = null;
        String photoPlateNum = null;
        List<Object> resultList = new ArrayList<>();

        SearchHit[] hits = null;

        do {
            System.out.println("======================进行第" + i + "次处理=======================");
            hits = response.getHits().getHits();

            if (hits != null && hits.length == 20000) {

                // 查询mongo进行数据校验
                for (int j = 0; j < hits.length; j++) {
                    SearchHit hit = hits[j];
                    docId = (String) hit.getSourceAsMap().get("docId");
                    photoPlateNum = (String) hit.getSourceAsMap().get("photoPlateNum");
                    resultList.add(docId + "#" + photoPlateNum);
                }

                obj = hits[hits.length-1].getSortValues();

                response = elasticsearchTemplate.getClient().prepareSearch(indexName).setTypes(indexType)
                        .setFetchSource(new String[]{"docId", "photoPlateNum"}, null).setQuery(queryBuilder)
                        .setFrom(0).setSize(20000).addSort("docId", SortOrder.DESC).searchAfter(obj).execute().actionGet();
            } else {
                break;
            }
            i++;
        } while (true);

        WriteTxtFile.write(resultList, "es-data.txt");

        long end = System.currentTimeMillis();
        System.out.println("耗时: " + (end - begin));
    }

    @Test
    public void mongoCount() {
        List<String> docIds = null;
        String photoPlateNum = null;
        List<Object> resultList = new ArrayList<>();

        Sort sort = new Sort(Sort.Direction.DESC, "docId");

        int tmp = 1;
        for (int x = 0; x < 6090000; x+=3000) {
            System.out.println("======================进行第" + tmp + "次处理=======================");
            List<JSONObject> list1 =
                    mongoTemplate.find(new Query().limit(3000+x).skip(x).with(sort), JSONObject.class, "DAG_FINISHED_PLATE");
            /*JSONObject tmp1 = new JSONObject();
            for (int i = 0; i < list1.size(); i++) {
                tmp1 = list1.get(i);
                docIds = (List<String>) tmp1.get("docIds");
                photoPlateNum = tmp1.getString("photoPlateNum");
                for (int j = 0; j < docIds.size(); j++) {
                    resultList.add(docIds.get(j) + "#" + photoPlateNum);
                }
            }*/
            tmp++;
        }
        System.out.println("统计大小：" + resultList.size());
        WriteTxtFile.write(resultList, "mongo-data-finish.txt");
    }

    @Test
    public void mongoCount2() {

        /**
         * 1、放es排重放进去的数据过滤
         */
        List<String> list = ReadTxtFile.read("data-distinct-1171539.txt");

        SearchRequestBuilder searchRequestBuilder =
                elasticsearchTemplate.getClient().prepareSearch("archieves_bus_search").setTypes("archieve_type");

        List<Object> resultList1 = new ArrayList<>();

        List<Object> resultList2 = new ArrayList<>();

        List<Object> resultList3 = new ArrayList<>();

        String str = null;
        String photoPlateNum = null;

        int temp = 1;
        for (int i = 0; i < list.size(); i++) {
            str = list.get(i);
            System.out.println("======================进行第" + temp + "次处理=======================");
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            queryBuilder.must(QueryBuilders.termQuery("articleType", "child"));
            queryBuilder.must(QueryBuilders.termQuery("docId", str.substring(0, str.indexOf("#"))));
            try {
                SearchResponse searchResponse = searchRequestBuilder.setQuery(queryBuilder).execute().actionGet();
                SearchHits searchHits = searchResponse.getHits();
                long totalHits = searchHits.getTotalHits();
                if (totalHits > 0) {
                    Iterator<SearchHit> iterator = searchHits.iterator();
                    while (iterator.hasNext()) {
                        SearchHit searchHit = iterator.next();
                        photoPlateNum = searchHit.getSourceAsMap().get("photoPlateNum").toString();
                        if (photoPlateNum != null && photoPlateNum.length() > 0) {
                            // 根据docId能查到，底片号也存在，但是不存在底片类型
                            if (!photoPlateNum.contains("-")) {
                                resultList1.add(list.get(i));
                            }
                        } else {
                            // 根据docId能查到，但是不存在底片号
                            resultList3.add(list.get(i));
                        }
                    }
                } else {
                    // 根据docId查不到
                    resultList2.add(list.get(i));
                }
            } catch (Exception e) {
                continue;
            }
            temp += 1;
        }

        WriteTxtFile.write(resultList1, "UNFINISHED-not-plateType.txt");
        WriteTxtFile.write(resultList2, "UNFINISHED-not-in-es.txt");
        WriteTxtFile.write(resultList3, "UNFINISHED-not-photoPlateNum.txt");
    }

    @Test
    public void mongoCount3() {

        /**
         * 1、放es排重，放进去的数据过滤，结果：22781
         * 2、其中22781中存在只有parent没有child的情况
         */
        List<String> list = ReadTxtFile.read("data-distinct1-3.txt");

        SearchRequestBuilder searchRequestBuilder =
                elasticsearchTemplate.getClient().prepareSearch("archieves_bus_search").setTypes("archieve_type");

        List<Object> resultList1 = new ArrayList<>();

        List<Object> resultList2 = new ArrayList<>();

        List<Object> resultList3 = new ArrayList<>();

        String str = null;
        String photoPlateNum = null;

        int temp = 1;
        for (int i = 0; i < list.size(); i++) {
            str = list.get(i);
            System.out.println("======================进行第" + temp + "次处理=======================");
            BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
            //queryBuilder.must(QueryBuilders.termQuery("articleType", "child"));
            queryBuilder.must(QueryBuilders.termQuery("docId", str.substring(0, str.indexOf("#"))));
            try {
                SearchResponse searchResponse = searchRequestBuilder.setQuery(queryBuilder).execute().actionGet();
                SearchHits searchHits = searchResponse.getHits();
                long totalHits = searchHits.getTotalHits();
                if (totalHits > 1) {
                    Iterator<SearchHit> iterator = searchHits.iterator();
                    while (iterator.hasNext()) {
                        SearchHit searchHit = iterator.next();
                        photoPlateNum = searchHit.getSourceAsMap().get("photoPlateNum").toString();
                        if (photoPlateNum != null && photoPlateNum.length() > 0) {
                            // 根据docId能查到，底片号也存在，但是不存在底片类型
                            if (!photoPlateNum.contains("-")) {
                                resultList1.add(list.get(i));
                            }
                        } else {
                            // 根据docId能查到，但是不存在底片号
                            resultList3.add(list.get(i));
                        }
                    }
                }
            } catch (Exception e) {
                continue;
            }
            temp += 1;
        }

        WriteTxtFile.write(resultList1, "all-not-plateType1.txt");
        //WriteTxtFile.write(resultList2, "not-find-2.txt");
        WriteTxtFile.write(resultList3, "all-not-photoPlateNum1.txt");
    }
}
