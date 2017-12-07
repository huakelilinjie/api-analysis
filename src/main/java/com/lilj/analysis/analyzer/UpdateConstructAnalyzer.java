package com.lilj.analysis.analyzer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lilj.analysis.construct.Construct;
import com.lilj.analysis.construct.UpdateConstruct;
import com.lilj.analysis.core.AnalyzerContext;
import com.lilj.analysis.exception.ConstructException;

import java.util.Set;

/**
 * Created by lilj on 2017/5/17.
 */
public class UpdateConstructAnalyzer extends ConstructAnalyzer {

    protected static final String JSON_FIELD_PRODUCT = "product";

    public UpdateConstructAnalyzer(AnalyzerContext context) {
        super(context);
    }


    @Override
    protected Construct analysisConstruct(String construct) throws ConstructException {
        try {
            this.originConstruct = construct;
            construct = checkConstructStrBeforeUse(construct);
            this.construct = construct;
            JSONObject paramJson = JSON.parseObject(construct);
            boolean hasProduct = false;
            if (paramJson.containsKey(JSON_FIELD_PRODUCT)) {
                hasProduct = true;
            }
            UpdateConstruct updateConstruct = new UpdateConstruct();

            Set<String> otherSet = paramJson.keySet();
            if (hasProduct) {
                otherSet.remove(JSON_FIELD_PRODUCT);
            }
            updateConstruct.getOtherItemList().addAll(otherSet);

            if (hasProduct) {
                JSONObject productJson = paramJson.getJSONObject(JSON_FIELD_PRODUCT);
                Set<String> productSet = productJson.keySet();
                updateConstruct.getProductItemList().addAll(productSet);
            }

            return updateConstruct;

        } catch (Exception e) {
            throw new ConstructException("######################## analysis update construct :" + this.construct, this.originConstruct, e);
        }
    }


}
