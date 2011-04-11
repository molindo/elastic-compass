package at.molindo.elastic.query;

import org.elasticsearch.index.query.xcontent.XContentQueryBuilder;

public class TermQuery extends Query {

	private XContentQueryBuilder _builder;

	public TermQuery(Term<?> term) {
		_builder = term.buildQuery();
	}
	
	@Override
	public XContentQueryBuilder getBuilder() {
		return _builder;
	}
	
	

}
