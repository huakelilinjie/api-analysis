package com.lilj.analysis.core;

import com.alibaba.fastjson.JSONArray;

public class AnalyzerContext {

    private String tenantId;        // 分析的租户id
    private String api;             // 分析的请求url
    private Long count;             // 分析对应租户，对应请求的条数
    private ApiEntity apiEntity;        // 需要分析的实体
    private JSONArray array;                // 存储consumer线程分析完成后的所有结构信息

    public AnalyzerContext(String tenantId, String api, ApiEntity apiEntity) {
        this.tenantId = tenantId;
        this.api = api;
        this.count = 0L;
        this.apiEntity = apiEntity;
        this.array = new JSONArray();
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public ApiEntity getApiEntity() {
        return apiEntity;
    }

    public void setApiEntity(ApiEntity apiEntity) {
        this.apiEntity = apiEntity;
    }

    public JSONArray getArray() {
        return array;
    }

    public void setArray(JSONArray array) {
        this.array = array;
    }
}
