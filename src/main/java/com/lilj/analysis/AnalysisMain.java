package com.lilj.analysis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lilj.analysis.analyzer.ConstructAnalyzer;
import com.lilj.analysis.core.AnalyzerContext;
import com.lilj.analysis.core.Client;
import com.lilj.analysis.factory.ConstructAnalyzerFactory;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AnalysisMain {

    private static final String json = "{\n" +
            "                    \"date\": \"2017-11-08 06:31:04\",\n" +
            "                    \"timeMills\": \"2017-11-08 06:31:04 181\",\n" +
            "                    \"api\": \"http://api.xiaoshouyi.com/data/v1/query\",\n" +
            "                    \"tenantId\": \"233915\",\n" +
            "                    \"accessToken\": \"Bearer 42a8ae6cb068c311cb8340759137b4ea11726c4323092c7244374e2e497adca9\",\n" +
            "                    \"httpMethod\": \"POST\",\n" +
            "                    \"contentType\": \"application/x-www-form-urlencoded\",\n" +
            "                    \"param\": \"q:select id,accountName,dbcVarchar26 from Account where accountName = '???????????????',;\",\n" +
            "                    \"userId\": \"619081\",\n" +
            "                    \"passportId\": \"595404\",\n" +
            "                    \"departId\": \"345764\",\n" +
            "                    \"ip\": \"218.17.206.168\",\n" +
            "                    \"responseMsg\": \"{\\\"totalSize\\\":0,\\\"count\\\":0,\\\"records\\\":[]}\",\n" +
            "                    \"timeInterval\": 292,\n" +
            "                    \"result\": true\n" +
            "                }";
    private static final String json1 = "{\n" +
            "                    \"date\": \"2017-11-08 06:41:23\",\n" +
            "                    \"timeMills\": \"2017-11-08 06:41:23 054\",\n" +
            "                    \"api\": \"http://api.xiaoshouyi.com/data/v1/query\",\n" +
            "                    \"tenantId\": \"233915\",\n" +
            "                    \"accessToken\": \"Bearer 42a8ae6cb068c311cb8340759137b4ea11726c4323092c7244374e2e497adca9\",\n" +
            "                    \"httpMethod\": \"POST\",\n" +
            "                    \"contentType\": \"application/x-www-form-urlencoded\",\n" +
            "                    \"param\": \"q:select id,contactName from Contact where contactName = '???' and accountId = 18555174,;\",\n" +
            "                    \"userId\": \"619081\",\n" +
            "                    \"passportId\": \"595404\",\n" +
            "                    \"departId\": \"345764\",\n" +
            "                    \"ip\": \"218.17.206.168\",\n" +
            "                    \"responseMsg\": \"{\\\"totalSize\\\":0,\\\"count\\\":0,\\\"records\\\":[]}\",\n" +
            "                    \"timeInterval\": 161,\n" +
            "                    \"result\": true\n" +
            "                }";
    private static final String json2 = "{\n" +
            "                    \"date\": \"2017-11-08 06:31:14\",\n" +
            "                    \"timeMills\": \"2017-11-08 06:31:14 802\",\n" +
            "                    \"api\": \"http://api.xiaoshouyi.com/data/v1/query\",\n" +
            "                    \"tenantId\": \"233915\",\n" +
            "                    \"accessToken\": \"Bearer 42a8ae6cb068c311cb8340759137b4ea11726c4323092c7244374e2e497adca9\",\n" +
            "                    \"httpMethod\": \"POST\",\n" +
            "                    \"contentType\": \"application/x-www-form-urlencoded\",\n" +
            "                    \"param\": \"q:select id,accountName,dbcVarchar26 from Account where accountName = '???????????',;\",\n" +
            "                    \"userId\": \"619081\",\n" +
            "                    \"passportId\": \"595404\",\n" +
            "                    \"departId\": \"345764\",\n" +
            "                    \"ip\": \"218.17.206.168\",\n" +
            "                    \"responseMsg\": \"{\\\"totalSize\\\":0,\\\"count\\\":0,\\\"records\\\":[]}\",\n" +
            "                    \"timeInterval\": 289,\n" +
            "                    \"result\": true\n" +
            "                }";

    public static void main(String[] args) {
        try {
            Client client = new Client();
            String beginTime = null;//"2017-11-08";
            String endTime = null;//"2017-11-10";
            String buckets = client.getTenantUrlAggInfo(null, "http://api.xiaoshouyi.com/data/v1/objects/lead/create", beginTime, endTime, Client.AGGREGATION_PARAM_TENANT_ID, Client.AGGREGATION_PARAM_API);

            JSONArray tenantApiArray = JSONArray.parseArray(buckets);
            for (Object tenantApi : tenantApiArray) {
                JSONObject tenantApiJson = JSON.parseObject(tenantApi.toString());
                String tenantId = tenantApiJson.getString("tenantId");
                String api = tenantApiJson.getString("api");
                Long count = tenantApiJson.getLong("count");
                System.out.println("##### count=" + count);

                BlockingQueue<String> blockingQueue = new LinkedBlockingQueue<String>(count.intValue());
                client.getAllIdByScroll(tenantId, api, beginTime, endTime, blockingQueue, null);
                System.out.println("##### blockingQueue size=" + blockingQueue.size());


                AnalyzerContext analyzerContext = new AnalyzerContext(tenantId, api, null);
                ConstructAnalyzer constructAnalyzer = ConstructAnalyzerFactory.getInstance().getConstructAnalyzer(analyzerContext);
                while (blockingQueue.size() > 0) {
                    try {
                        String object = blockingQueue.take();
                        constructAnalyzer.addOriginArr(JSON.parseObject(object));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                constructAnalyzer.getConstructs();
                if (constructAnalyzer.getConstructResultList().size() > 0) {
                    System.out.println(JSON.parseArray(Arrays.toString(constructAnalyzer.getConstructResultList().toArray())));
                }

            }
            client.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }
}
