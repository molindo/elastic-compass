package at.molindo.elastic.filter;

import org.elasticsearch.index.query.xcontent.FilterBuilders;
import org.elasticsearch.index.query.xcontent.QueryFilterBuilder;
import org.elasticsearch.index.query.xcontent.XContentFilterBuilder;

import at.molindo.elastic.query.Query;

public class QueryFilter extends Filter {

	private QueryFilterBuilder _builder;

	public QueryFilter(Query query) {
		_builder = FilterBuilders.queryFilter(query.getBuilder());
	}
	
	@Override
	public XContentFilterBuilder getBuilder() {
		return _builder;
	}

}
