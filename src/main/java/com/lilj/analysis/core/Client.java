package com.lilj.analysis.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class Client {

    public static final String AGGREGATION_PARAM_TENANT_ID = "tenantId";
    public static final String AGGREGATION_PARAM_API = "api";

    private static final String LOCAL_IP = "127.0.0.1";
    private static final String LOCAL_INDEX = "api";
    private static final String LOCAL_TYPE = "request";
    private static final Integer PORT = 9300;
    private static final Integer MAX_SIZE = 10000;
    private static final Long SAVE_TIME = 20L * 1000L;
    private static final Integer SCROLL_SIZE = 10000;

    private TransportClient client;
    private String index;
    private String type;

    public Client() throws UnknownHostException {
        this(LOCAL_IP, LOCAL_INDEX, LOCAL_TYPE);
        System.out.println("##### thread=" + Thread.currentThread().getName() + ",name=" + client.toString() + ",new Client");
    }

    public Client(String ip, String index, String type) throws UnknownHostException {
        // 实例化ES配置信息
        Settings settings = Settings.builder().put("cluster.name", "elasticsearch").build();
        // 实例化ES客户端
        this.client = new PreBuiltTransportClient(settings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ip), PORT));
        this.index = index;
        this.type = type;
    }

    public String getTenantUrlAggInfo(String firstAgg, String secondAgg) {
        return getTenantUrlAggInfo(null, null, firstAgg, secondAgg);
    }

    public String getTenantUrlAggInfo(String beginTime, String endTime, String firstAgg, String secondAgg) {
        return getTenantUrlAggInfo(null, null, beginTime, endTime, firstAgg, secondAgg);
    }

    public String getTenantUrlAggInfo(String tenantId, String api, String beginTime, String endTime, String firstAgg, String secondAgg) {

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if (null != tenantId)
            boolQuery.must(QueryBuilders.matchQuery("tenantId", tenantId));
        if (null != api)
            boolQuery.must(QueryBuilders.matchQuery("api", api));
        if (null != beginTime)
            boolQuery.must(QueryBuilders.rangeQuery("date").gte(beginTime));
        if (null != endTime)
            boolQuery.must(QueryBuilders.rangeQuery("date").lte(endTime));
        AggregationBuilder aggregationBuilder = AggregationBuilders
                .terms("agg1").field(firstAgg).size(MAX_SIZE).subAggregation(AggregationBuilders
                        .terms("agg2").field(secondAgg).size(MAX_SIZE));
        SearchResponse aggResp = client.
                prepareSearch(index).
                setTypes(type).
                setQuery(boolQuery).
                addAggregation(aggregationBuilder).
                setSize(0).
                execute().actionGet();

        JSONArray array = new JSONArray();
        Aggregations aggregations = aggResp.getAggregations();
        Terms aggregation = (Terms) aggregations.iterator().next();
        List<Terms.Bucket> buckets = aggregation.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            Object bucketTenantId = bucket.getKey();
            Object count = bucket.getDocCount();
            Aggregations subAggregations = bucket.getAggregations();
            Terms subAggregation = (Terms) subAggregations.iterator().next();
            List<Terms.Bucket> subBuckets = subAggregation.getBuckets();
            for (Terms.Bucket subBucket : subBuckets) {
                Object bucketApi = subBucket.getKey();
                Object subCount = subBucket.getDocCount();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("tenantId", bucketTenantId);
                jsonObject.put("api", bucketApi);
                jsonObject.put("count", subCount);
                array.add(jsonObject);
            }
        }

        return array.toString();
    }

    public void getAllIdByScroll(BlockingQueue<String> queue) {
        getAllIdByScroll(queue, null);
    }

    public void getAllIdByScroll(BlockingQueue<String> queue, ApiEntity apiEntity) {
        getAllIdByScroll(null, null, queue, apiEntity);
    }

    public void getAllIdByScroll(String beginTime, String endTime, BlockingQueue<String> queue, ApiEntity apiEntity) {
        getAllIdByScroll(null, null, beginTime, endTime, queue, apiEntity);
    }

    public void getAllIdByScroll(String tenantId, String api, String beginTime, String endTime, BlockingQueue<String> queue, ApiEntity apiEntity) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if (null != tenantId)
            boolQuery.must(QueryBuilders.matchQuery("tenantId", tenantId));
        if (null != api)
            boolQuery.must(QueryBuilders.matchQuery("api", api));
        if (null != beginTime)
            boolQuery.must(QueryBuilders.rangeQuery("date").gte(beginTime));
        if (null != endTime)
            boolQuery.must(QueryBuilders.rangeQuery("date").lte(endTime));

        if (null != apiEntity) {
            if (RequestType.OperatorSuffix.QUERY_SUFFIX.equals(RequestType.getUrlSuffix(api))) {
                boolQuery.must(QueryBuilders.regexpQuery("param", ".*" + getFirstCharUpperCase("from") + " +" + getFirstCharUpperCase(apiEntity.getEntityName()) + ".*|"
                        + ".*" + getFirstCharLowerCase("from") + " +" + getFirstCharUpperCase(apiEntity.getEntityName()) + ".*|"
                        + ".*" + getFirstCharUpperCase("from") + " +" + getFirstCharLowerCase(apiEntity.getEntityName()) + ".*|"
                        + ".*" + getFirstCharLowerCase("from") + " +" + getFirstCharLowerCase(apiEntity.getEntityName()) + ".*"));
            }
        }

        SortBuilder sort = SortBuilders.fieldSort("timeMills").order(SortOrder.ASC);

        SearchResponse scrollResp = client.prepareSearch(index)
                .setTypes(type).setQuery(boolQuery).addSort(sort)
                .setScroll(new TimeValue(SAVE_TIME))
                .setSize(SCROLL_SIZE).execute().actionGet();
        //Scroll until no hits are returned
        do {
            for (SearchHit hit : scrollResp.getHits().getHits()) {
                try {
                    String obj = JSON.toJSONString(hit.getSource());
                    queue.put(obj);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).
                    setScroll(new TimeValue(SAVE_TIME)).execute().actionGet();
        }
        while (scrollResp.getHits().getHits().length != 0); // Zero hits mark the end of the scroll and the while loop.
    }

    private String getFirstCharUpperCase(String str) {
        String result = str.substring(0, 1).toUpperCase() + str.substring(1);
        return result;
    }

    private String getFirstCharLowerCase(String str) {
        String result = str.substring(0, 1).toLowerCase() + str.substring(1);
        return result;
    }

    public String getEsApiRequestById(String id) {
        IdsQueryBuilder idsQuery = QueryBuilders.idsQuery();
        idsQuery.addIds(id);

        SearchResponse scrollResp = client.prepareSearch(index)
                .setTypes(type).setQuery(idsQuery)
                .setScroll(new TimeValue(SAVE_TIME))
                .setSize(SCROLL_SIZE).execute().actionGet();

        for (SearchHit hit : scrollResp.getHits().getHits()) {
            String obj = JSON.toJSONString(hit.getSource());
            return obj;
        }
        return null;
    }

    public void close() {
        if (null == client)
            return;
        client.close();
        System.out.println("##### thread=" + Thread.currentThread().getName() + ",name=" + client.toString() + ",client close");
    }
}
