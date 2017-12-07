package com.lilj.analysis.construct;

/**
 * Created by lilj on 2017/6/20.
 */
public class ConstructResult {

    protected static final String NOT_QUERY_RESULT = "{\"contructStr\":%s,\"count\":%d}";
    protected static final String QUERY_RESULT = "{\"contructStr\":\"%s\",\"count\":%d}";

    protected String contructStr;
    protected Construct construct;
    protected int count;

    public ConstructResult(String contructStr, Construct construct) {
        this.contructStr = contructStr;
        this.construct = construct;
        this.count = 1;
    }

    public Construct getConstruct() {
        return construct;
    }

    public void addCount() {
        this.count++;
    }

    protected void dealConstructStr() {
        contructStr = contructStr.replaceAll("\"","'");
        contructStr = contructStr.replaceAll("\\\"","'");
    }

    @Override
    public String toString() {
        dealConstructStr();
        return String.format(QUERY_RESULT, contructStr, count);
    }
}
