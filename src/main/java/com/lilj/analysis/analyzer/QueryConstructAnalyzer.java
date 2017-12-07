package com.lilj.analysis.analyzer;

import com.lilj.analysis.construct.Construct;
import com.lilj.analysis.construct.QueryConstruct;
import com.lilj.analysis.core.AnalyzerContext;
import com.lilj.analysis.exception.ConstructException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;

import java.io.StringReader;
import java.util.List;

/**
 * Created by lilj on 2017/5/17.
 */
public class QueryConstructAnalyzer extends ConstructAnalyzer {

    public QueryConstructAnalyzer(AnalyzerContext context) {
        super(context);
    }

    @Override
    protected Construct analysisConstruct(String construct) throws ConstructException {
        try {
            this.originConstruct = construct;
            construct = checkConstructStrBeforeUse(construct);
            this.construct = construct;

            StringReader reader = new StringReader(construct);
            Select stat = (Select) new CCJSqlParserManager().parse(reader);
            PlainSelect body = (PlainSelect) stat.getSelectBody();
            FromItem fromItem = body.getFromItem();
            Table table = (Table) fromItem;
            String entityName = table.getName();

            List<SelectItem> selectItemList = body.getSelectItems();
            // FIXME: 2017/5/17 先不处理condition中的item，也不比较condition中的item
            // Expression expWhere = body.getWhere();
            List<OrderByElement> expOrder = body.getOrderByElements();

            QueryConstruct queryConstruct = new QueryConstruct();
            queryConstruct.setEntityName(entityName.toLowerCase());

            if (selectItemList != null) {
                for (SelectItem selectItem : selectItemList) {
                    SelectExpressionItem expItem = (SelectExpressionItem) selectItem;
                    Expression column = expItem.getExpression();
                    queryConstruct.getSelectItemList().add(column.toString());
                }
            }

            if (expOrder != null) {
                for (OrderByElement orderByElement : expOrder) {
                    Column column = (Column) orderByElement.getExpression();
                    String columnName = column.getColumnName();
                    boolean isAsc = orderByElement.isAsc();
                    queryConstruct.getOrderItemList().add(columnName + " " + (isAsc ? "asc" : "desc"));
                }
            }

            return queryConstruct;
        } catch (Exception e) {
//            e.printStackTrace();
            throw new ConstructException("######################## analysis query construct :" + this.construct, this.originConstruct, e);
        }
    }
}
