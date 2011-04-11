package at.molindo.elastic.query;

import org.elasticsearch.index.query.xcontent.QueryBuilders;
import org.elasticsearch.index.query.xcontent.QueryStringQueryBuilder;
import org.elasticsearch.index.query.xcontent.QueryStringQueryBuilder.Operator;
import org.elasticsearch.index.query.xcontent.XContentQueryBuilder;

public class StringQuery extends BoostQuery<StringQuery> {

	private QueryStringQueryBuilder _builder;

	public StringQuery(String query) {
		_builder = QueryBuilders.queryString(query);
	}

	public StringQuery setDefaultField(String defaultField) {
		_builder.defaultField(defaultField);
		return this;
	}

	public StringQuery setBoost(float boost) {
		_builder.boost(boost);
		return this;
	}
	
	public StringQuery setAnalyzer(String analyzer) {
		_builder.analyzer(analyzer);
		return this;
	}
	
	public StringQuery setDefaultOperator(Operator defaultOperator) {
		_builder.defaultOperator(defaultOperator);
		return this;
	}
	
	@Override
	public XContentQueryBuilder getBuilder() {
		return _builder;
	}



}
