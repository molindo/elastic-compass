package at.molindo.elastic.query;

import org.elasticsearch.index.query.xcontent.XContentQueryBuilder;

public abstract class Query {
	
	public abstract XContentQueryBuilder getBuilder();

}
