package com.lilj.analysis.analyzer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lilj.analysis.construct.Construct;
import com.lilj.analysis.construct.CreateConstruct;
import com.lilj.analysis.core.AnalyzerContext;
import com.lilj.analysis.core.ApiEntity;
import com.lilj.analysis.exception.ConstructException;

import java.util.Set;

/**
 * Created by lilj on 2017/5/17.
 */
public class CreateConstructAnalyzer extends ConstructAnalyzer {

    protected static final String JSON_FIELD_RECORD = "record";
    protected static final String JSON_FIELD_PRODUCT = "product";

    public CreateConstructAnalyzer(AnalyzerContext context) {
        super(context);
    }

    @Override
    protected Construct analysisConstruct(String construct) throws ConstructException {
        try {
            this.originConstruct = construct;
            construct = checkConstructStrBeforeUse(construct);
            this.construct = construct;

            JSONObject paramJson = JSON.parseObject(construct);
            if (!ApiEntity.notNeedRecord(context.getApi()) && !paramJson.containsKey(JSON_FIELD_RECORD)) {
                throw new Exception("create construct without context 'record',paramJson=" + paramJson.toString());
            }
            // 不需要record的创建语句直接获取格式，需要record的获取record内部值
            Object recordObj;
            if (!ApiEntity.notNeedRecord(context.getApi())) {
                recordObj = paramJson.get(JSON_FIELD_RECORD);
            } else {
                recordObj = paramJson;
            }

            //
            JSONObject recordJson = JSON.parseObject(recordObj.toString());
            boolean hasProduct = false;
            if (paramJson.containsKey(JSON_FIELD_PRODUCT)) {
                hasProduct = true;
            }
            CreateConstruct createConstruct = new CreateConstruct();

            Set<String> otherSet = paramJson.keySet();
            otherSet.remove(JSON_FIELD_RECORD);
            if (hasProduct) {
                otherSet.remove(JSON_FIELD_PRODUCT);
            }
            createConstruct.getOtherItemList().addAll(otherSet);

            Set<String> recordSet = recordJson.keySet();
            createConstruct.getRecordItemList().addAll(recordSet);

            if (hasProduct) {
                JSONObject productJson = paramJson.getJSONObject(JSON_FIELD_PRODUCT);
                Set<String> productSet = productJson.keySet();
                createConstruct.getProductItemList().addAll(productSet);
            }

            return createConstruct;

        } catch (Exception e) {
            throw new ConstructException("######################## analysis create construct :" + this.construct, this.originConstruct, e);
        }
    }


}
