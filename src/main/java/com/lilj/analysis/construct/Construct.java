package com.lilj.analysis.construct;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by lilj on 2017/5/17.
 */
public class Construct {

    protected List<String> otherItemList;

    public Construct() {
        otherItemList = new LinkedList<>();
    }

    protected boolean compareList(List<String> originList, List<String> targetList) {
        if (null == originList && null == targetList) {
            return true;
        } else if (null == originList && null != targetList || null != originList && null == targetList) {
            return false;
        } else {
            if (originList.size() != targetList.size()) {
                return false;
            }
            for (String originStr : originList) {
                if (!targetList.contains(originStr)) {
                    return false;
                }
            }
            for (String targetStr : targetList) {
                if (!originList.contains(targetStr)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Construct that = (Construct) o;
        if (!compareList(otherItemList, that.otherItemList)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return otherItemList != null ? otherItemList.hashCode() : 0;
    }

    public List<String> getOtherItemList() {
        return otherItemList;
    }
}
