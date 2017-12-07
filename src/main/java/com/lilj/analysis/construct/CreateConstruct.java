package com.lilj.analysis.construct;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by lilj on 2017/5/17.
 */
public class CreateConstruct extends Construct {

    protected List<String> recordItemList;
    protected List<String> productItemList;

    public CreateConstruct() {
        super();
        recordItemList = new LinkedList<>();
        productItemList = new LinkedList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CreateConstruct that = (CreateConstruct) o;

        if (!compareList(recordItemList, that.recordItemList)) {
            return false;
        }
        if (!compareList(productItemList, that.productItemList)) {
            return false;
        }
        if (!compareList(otherItemList, that.otherItemList)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = recordItemList != null ? recordItemList.hashCode() : 0;
        result = 31 * result + (productItemList != null ? productItemList.hashCode() : 0);
        result = 31 * result + (otherItemList != null ? otherItemList.hashCode() : 0);
        return result;
    }

    public List<String> getRecordItemList() {
        return recordItemList;
    }

    public void setRecordItemList(List<String> recordItemList) {
        this.recordItemList = recordItemList;
    }

    public List<String> getProductItemList() {
        return productItemList;
    }

    public void setProductItemList(List<String> productItemList) {
        this.productItemList = productItemList;
    }
}
