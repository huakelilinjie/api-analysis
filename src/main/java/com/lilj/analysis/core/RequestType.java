package com.lilj.analysis.core;

/**
 * Created by lilj on 2017/6/20.
 */

/**
 * 可供分析的类型 可以单个选用create，update，query使用，也可以使用create+update，或者使用全集all
 */
public enum RequestType {

    create(1, new String[]{OperatorSuffix.CREATE_SUFFIX, OperatorSuffix.ADD_SUFFIX}),
    update(2, new String[]{OperatorSuffix.UPDATE_SUFFIX}),
    query(3, new String[]{OperatorSuffix.QUERY_SUFFIX}),
    notQuery(4, new String[]{OperatorSuffix.CREATE_SUFFIX, OperatorSuffix.ADD_SUFFIX, OperatorSuffix.UPDATE_SUFFIX}),
    all(5, new String[]{OperatorSuffix.CREATE_SUFFIX, OperatorSuffix.ADD_SUFFIX, OperatorSuffix.UPDATE_SUFFIX, OperatorSuffix.QUERY_SUFFIX});

    private String[] urlSuffixes;
    private int value;

    RequestType(int value, String[] urlSuffixes) {
        this.urlSuffixes = urlSuffixes;
        this.value = value;
    }

    public static String getUrlSuffix(String url) {
        String urlSuffix = url.substring(url.lastIndexOf("/"));
        return urlSuffix;
    }

    public static boolean containsRequestType(String url, RequestType requestType) {
        String[] urlSuffixes = requestType.urlSuffixes;
        boolean containsFlag = false;
        for (String urlSuffix : urlSuffixes) {
            if (url.endsWith(urlSuffix)) {
                containsFlag = true;
                break;
            }
        }
        return containsFlag;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public interface OperatorSuffix {
        String QUERY_SUFFIX = "/query";
        String CREATE_SUFFIX = "/create";
        String ADD_SUFFIX = "/add";
        String UPDATE_SUFFIX = "/update";
    }
}
