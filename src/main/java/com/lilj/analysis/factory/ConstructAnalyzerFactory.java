package com.lilj.analysis.factory;

import com.lilj.analysis.analyzer.ConstructAnalyzer;
import com.lilj.analysis.analyzer.CreateConstructAnalyzer;
import com.lilj.analysis.analyzer.QueryConstructAnalyzer;
import com.lilj.analysis.analyzer.UpdateConstructAnalyzer;
import com.lilj.analysis.core.AnalyzerContext;
import com.lilj.analysis.core.RequestType;

/**
 * Created by lilj on 2017/6/16.
 */
public class ConstructAnalyzerFactory {

    private static ConstructAnalyzerFactory INSTANCE;

    private ConstructAnalyzerFactory() {
    }

    public synchronized static ConstructAnalyzerFactory getInstance() {
        if (null == INSTANCE)
            INSTANCE = new ConstructAnalyzerFactory();
        return INSTANCE;
    }

    public ConstructAnalyzer getConstructAnalyzer(AnalyzerContext context) {
        ConstructAnalyzer constructAnalyzer = null;
        String urlSuffix = RequestType.getUrlSuffix(context.getApi());
        switch (urlSuffix) {
            case RequestType.OperatorSuffix.QUERY_SUFFIX:
                constructAnalyzer = new QueryConstructAnalyzer(context);
                break;
            case RequestType.OperatorSuffix.CREATE_SUFFIX:
            case RequestType.OperatorSuffix.ADD_SUFFIX:
                constructAnalyzer = new CreateConstructAnalyzer(context);
                break;
            case RequestType.OperatorSuffix.UPDATE_SUFFIX:
                constructAnalyzer = new UpdateConstructAnalyzer(context);
                break;
            default:
                break;
        }
        return constructAnalyzer;
    }

}
