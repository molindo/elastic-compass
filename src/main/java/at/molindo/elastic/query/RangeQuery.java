package at.molindo.elastic.query;

import org.elasticsearch.index.query.xcontent.QueryBuilders;
import org.elasticsearch.index.query.xcontent.RangeQueryBuilder;
import org.elasticsearch.index.query.xcontent.XContentQueryBuilder;

public class RangeQuery extends Query {

	private RangeQueryBuilder _builder;

	public RangeQuery(String property) {
		_builder = QueryBuilders.rangeQuery(property);
	}

	public RangeQuery setFrom(Term from) {
		_builder.from(from.getValue());
		return this;
	}

	public RangeQuery setTo(Term to) {
		_builder.to(to.getValue());
		return this;
	}

	public RangeQuery setIncludeLower(boolean includeLower) {
		_builder.includeLower(includeLower);
		return this;
	}

	public RangeQuery setIncludeUpper(boolean includeUpper) {
		_builder.includeUpper(includeUpper);
		return this;
	}

	public RangeQuery setIncludeBoth(boolean includeBoth) {
		_builder.includeUpper(includeBoth).includeLower(includeBoth);
		return this;
	}
	
	@Override
	public XContentQueryBuilder getBuilder() {
		return _builder;
	}

}
