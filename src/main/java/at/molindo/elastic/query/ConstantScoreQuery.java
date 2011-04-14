package at.molindo.elastic.query;

import org.elasticsearch.index.query.xcontent.ConstantScoreQueryBuilder;
import org.elasticsearch.index.query.xcontent.FilterBuilders;
import org.elasticsearch.index.query.xcontent.QueryBuilders;
import org.elasticsearch.index.query.xcontent.XContentQueryBuilder;

import at.molindo.elastic.filter.Filter;

public class ConstantScoreQuery extends Query {

	private ConstantScoreQueryBuilder _builder;

	public ConstantScoreQuery(Query query) {
		_builder = QueryBuilders.constantScoreQuery(FilterBuilders.queryFilter(query.getBuilder()));
	}
	
	public ConstantScoreQuery(Filter filter) {
		_builder = QueryBuilders.constantScoreQuery(filter.getBuilder());
	}
	
	@Override
	public XContentQueryBuilder getBuilder() {
		return _builder;
	}

}
