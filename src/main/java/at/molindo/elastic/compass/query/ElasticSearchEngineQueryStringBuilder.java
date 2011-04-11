package at.molindo.elastic.compass.query;

import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.QueryParser.Operator;
import org.compass.core.engine.SearchEngineQuery;
import org.compass.core.engine.SearchEngineQueryBuilder;
import org.compass.core.engine.SearchEngineQueryBuilder.SearchEngineQueryStringBuilder;
import org.compass.core.lucene.engine.queryparser.QueryHolder;
import org.elasticsearch.common.base.Strings;
import org.elasticsearch.index.query.xcontent.QueryBuilders;
import org.elasticsearch.index.query.xcontent.QueryStringQueryBuilder;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import at.molindo.elastic.compass.ElasticSearchEngineFactory;
import at.molindo.elastic.compass.ElasticSearchEngineQuery;
import at.molindo.elastic.query.Query;
import at.molindo.elastic.query.StringQuery;
import at.molindo.utils.data.StringUtils;

public class ElasticSearchEngineQueryStringBuilder implements SearchEngineQueryStringBuilder {

	private ElasticSearchEngineFactory _searchEngineFactory;
	private String _queryString;
	private String _analyzer;
	private String _defaultSearchProperty;
	private Operator _operator;

	public ElasticSearchEngineQueryStringBuilder(ElasticSearchEngineFactory searchEngineFactory, String queryString) {
		_searchEngineFactory = searchEngineFactory;
		_queryString = queryString;
	}

	@Override
	public SearchEngineQueryStringBuilder setAnalyzer(String analyzer) {
		_analyzer = analyzer;
		return this;
	}

	@Override
	public SearchEngineQueryStringBuilder setAnalyzerByAlias(String alias) {
		setAnalyzer(alias);
		return this;
	}

	@Override
	public SearchEngineQueryStringBuilder setDefaultSearchProperty(String defaultSearchProperty) {
		_defaultSearchProperty = defaultSearchProperty;
		return this;
	}

	public SearchEngineQueryBuilder.SearchEngineQueryStringBuilder useAndDefaultOperator() {
		_operator = Operator.AND;
		return this;
	}

	public SearchEngineQueryBuilder.SearchEngineQueryStringBuilder useOrDefaultOperator() {
		_operator = Operator.OR;
		return this;
	}

	@Override
	public SearchEngineQueryStringBuilder forceAnalyzer() {
		// TODO heck, what's that doing? :)
		throw new NotImplementedException();
	}

	@Override
	public SearchEngineQueryStringBuilder setQueryParser(String queryParser) {
		throw new NotImplementedException();
	}

	@Override
	public SearchEngineQueryStringBuilder useSpellCheck() {
		throw new NotImplementedException();
	}

	@Override
	public SearchEngineQuery toQuery() {
		String defaultField = !StringUtils.empty(_defaultSearchProperty) ? _defaultSearchProperty : _searchEngineFactory
				.getElasticSettings().getDefaultSearchPropery();

		StringQuery query = new StringQuery(_queryString).setAnalyzer(_analyzer).setDefaultField(defaultField);
		return new ElasticSearchEngineQuery(_searchEngineFactory, query);
	}
}
