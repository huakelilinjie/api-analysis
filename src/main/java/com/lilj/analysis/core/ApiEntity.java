package com.lilj.analysis.core;

/**
 * Created by lilj on 2017/7/26.
 */
public enum ApiEntity {

    lead("objects/lead", "lead"),
    contract("objects/contract", "contract"),
    quotationEntity("objects/quotationentity", "quotationentity"),
    quotationDetailEntity("objects/quotationdetailentity", "quotationdetailentity"),
    activityRecord("feed/activityRecord", "activityrecord"),
    customize("objects/customize", "customize"),
    bulkEntity("/async/v1/objects", null);

    private String urlInterception;
    private String entityName;

    ApiEntity(String urlInterception, String entityName) {
        this.urlInterception = urlInterception;
        this.entityName = entityName;
    }

    public static boolean notNeedRecord(String url) {
        if (url.contains(activityRecord.getUrlInterception())) {
            return true;
        }
        return false;
    }

    public static boolean isBulkApi(String url) {
        if (url.contains(bulkEntity.getUrlInterception())) {
            return true;
        }
        return false;
    }

    public static boolean contains(String url, ApiEntity apiEntity) {
        if (url.contains(apiEntity.getUrlInterception())) {
            return true;
        }
        return false;
    }

    public String getUrlInterception() {
        return urlInterception;
    }

    public String getEntityName() {
        return entityName;
    }
}
