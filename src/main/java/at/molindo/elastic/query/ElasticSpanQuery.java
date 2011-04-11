package at.molindo.elastic.query;

import org.elasticsearch.index.query.xcontent.XContentQueryBuilder;
import org.elasticsearch.index.query.xcontent.XContentSpanQueryBuilder;

public class ElasticSpanQuery extends Query {
	
	private XContentSpanQueryBuilder _builder;

	public ElasticSpanQuery() {
		_builder = null; // TODO
	}
	
	public XContentQueryBuilder getBuilder() {
		return _builder;
	}

}