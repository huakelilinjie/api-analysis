package com.lilj.analysis.construct;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by lilj on 2017/5/17.
 */
public class UpdateConstruct extends Construct {

    protected List<String> productItemList;

    public UpdateConstruct() {
        super();
        productItemList = new LinkedList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UpdateConstruct that = (UpdateConstruct) o;

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
        int result = super.hashCode();
        result = 31 * result + (productItemList != null ? productItemList.hashCode() : 0);
        return result;
    }

    public List<String> getProductItemList() {
        return productItemList;
    }

    public void setProductItemList(List<String> productItemList) {
        this.productItemList = productItemList;
    }

}
