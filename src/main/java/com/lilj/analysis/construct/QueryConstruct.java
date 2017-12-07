package com.lilj.analysis.construct;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by lilj on 2017/5/17.
 */
public class QueryConstruct extends Construct {

    protected String entityName;
    protected List<String> selectItemList;
    protected List<String> conditionItemList;
    protected List<String> orderItemList;

    public QueryConstruct() {
        super();
        entityName = "";
        selectItemList = new LinkedList<>();
        conditionItemList = new LinkedList<>();
        orderItemList = new LinkedList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        QueryConstruct that = (QueryConstruct) o;

        if (entityName != null ? !entityName.equals(that.entityName) : that.entityName != null) return false;
        if (!compareList(selectItemList, that.selectItemList)) {
            return false;
        }
        if (!compareList(conditionItemList, that.conditionItemList)) {
            return false;
        }
        if (!compareList(orderItemList, that.orderItemList)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (entityName != null ? entityName.hashCode() : 0);
        result = 31 * result + (selectItemList != null ? selectItemList.hashCode() : 0);
        result = 31 * result + (conditionItemList != null ? conditionItemList.hashCode() : 0);
        result = 31 * result + (orderItemList != null ? orderItemList.hashCode() : 0);
        return result;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public List<String> getSelectItemList() {
        return selectItemList;
    }

    public void setSelectItemList(List<String> selectItemList) {
        this.selectItemList = selectItemList;
    }

    public List<String> getConditionItemList() {
        return conditionItemList;
    }

    public void setConditionItemList(List<String> conditionItemList) {
        this.conditionItemList = conditionItemList;
    }

    public List<String> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<String> orderItemList) {
        this.orderItemList = orderItemList;
    }
}
