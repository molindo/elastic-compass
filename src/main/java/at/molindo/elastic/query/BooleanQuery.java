package at.molindo.elastic.query;

import org.elasticsearch.index.query.xcontent.BoolQueryBuilder;
import org.elasticsearch.index.query.xcontent.QueryBuilders;
import org.elasticsearch.index.query.xcontent.XContentQueryBuilder;

import at.molindo.elastic.query.BooleanClause.Occur;

public class BooleanQuery extends BoostQuery<BooleanQuery> {

	private BoolQueryBuilder _builder;

	public BooleanQuery() {
		_builder = QueryBuilders.boolQuery();
	}
	
	public void add(Query query, Occur occur) {
		switch (occur) {
		case MUST:
			_builder.must(query.getBuilder());
			break;
		case MUST_NOT:
			_builder.mustNot(query.getBuilder());
			break;
		case SHOULD:
			_builder.should(query.getBuilder());
			break;
		default:
			throw new RuntimeException("unknown occur: " + occur);
		}
	}

	public XContentQueryBuilder getBuilder() {
		return _builder;
	}

	@Override
	public BooleanQuery setBoost(float boost) {
		_builder.boost(boost);
		return this;
	}
}
