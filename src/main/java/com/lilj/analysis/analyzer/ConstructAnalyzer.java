package com.lilj.analysis.analyzer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lilj.analysis.construct.Construct;
import com.lilj.analysis.construct.ConstructResult;
import com.lilj.analysis.core.AnalyzerContext;
import com.lilj.analysis.exception.ConstructException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

public class ConstructAnalyzer {

    protected static final Logger logger = LoggerFactory.getLogger("analysisFailed");

    protected static final String JSON_FIELD_PARAM = "param";

    protected AnalyzerContext context;

    protected JSONArray originArr;

    protected List<ConstructResult> constructResultList;
    protected String construct;
    protected String originConstruct;

    public ConstructAnalyzer(AnalyzerContext context) {
        this.context = context;
        this.constructResultList = new LinkedList<>();
    }

    public void addOriginArr(JSONObject addJson) {
        if (null == addJson) {
            return;
        }
        if (null == originArr) {
            originArr = new JSONArray();
        }
        originArr.add(addJson);
    }

    /**
     * 校验从es中获取的json格式是否正确
     *
     * @return
     */
    private boolean checkOriginJson() {
        if (null == originArr) {
            return false;
        }
        if (originArr.size() == 0) {
            return false;
        }
        JSONObject tempJson = originArr.getJSONObject(0);
        if (!tempJson.containsKey(JSON_FIELD_PARAM)) {
            return false;
        }
        return true;
    }

    protected String checkConstructStrBeforeUse(String constructStr) {
        if (constructStr.contains("json:") && constructStr.endsWith(",;")) {
            if (constructStr.contains("},;")) {
                constructStr = constructStr.substring(constructStr.indexOf("json:") + 5, constructStr.indexOf("},;") + 1);
            } else {
                constructStr = constructStr.substring(constructStr.indexOf("json:") + 5, constructStr.length() - 2);
            }
        } else if (constructStr.contains("{\"json\":\"") && constructStr.endsWith("\"},;")) {
            if (constructStr.contains("},;")) {
                constructStr = constructStr.substring(constructStr.indexOf("{\"json\":\"") + 9, constructStr.indexOf("},;") + 1);
            } else {
                constructStr = constructStr.substring(constructStr.indexOf("{\"json\":\"") + 9, constructStr.length() - 4);
            }
        } else if (constructStr.contains("objectInfo:") && constructStr.endsWith(",;")) {
            constructStr = constructStr.substring(constructStr.indexOf("objectInfo:") + 11, constructStr.length() - 2);
        } else if (constructStr.contains("q:")) {
            constructStr = constructStr.substring(constructStr.indexOf("q:") + 2);
            if (constructStr.contains(",;")) {
                constructStr = constructStr.substring(0, constructStr.indexOf(",;"));
            }
        }
        return constructStr;

    }

    protected Construct analysisConstruct(String construct) throws ConstructException {
        return null;
    }

    private int getConstructIndex(Construct construct) throws ConstructException {
        for (int i = 0; i < constructResultList.size(); i++) {
            if (construct.equals(constructResultList.get(i).getConstruct())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 暂存该结构
     *
     * @param constructStr
     */
    private void pushConstruct(String constructStr, Construct construct) throws ConstructException {
        constructResultList.add(new ConstructResult(checkConstructStrBeforeUse(constructStr), construct));
    }


    public void getConstructs() {
        if (!checkOriginJson()) {
            return;
        }
        System.out.println("originArr size=" + originArr.size());
        for (int i = 0; i < originArr.size(); i++) {
            try {
                JSONObject everyJsonObj = originArr.getJSONObject(i);
                String constructStr = everyJsonObj.getString(JSON_FIELD_PARAM);

                Construct construct = analysisConstruct(constructStr);
                if (null == construct) {
                    continue;
                }

                int index = getConstructIndex(construct);
                if (index <= -1) {
                    pushConstruct(constructStr, construct);
                } else {
                    constructResultList.get(index).addCount();
                }
            } catch (ConstructException e) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("construct", e.getConstruct());
                logger.error(jsonObject.toJSONString(), e);
            }
        }
    }

    public List<ConstructResult> getConstructResultList() {
        return constructResultList;
    }
}
