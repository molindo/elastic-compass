/**
 * Copyright 2011 Molindo GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.molindo.elastic.compass;

import java.io.Reader;

import org.compass.core.Resource;
import org.compass.core.engine.SearchEngine;
import org.compass.core.engine.SearchEngineQuery;
import org.compass.core.engine.SearchEngineQuery.SearchEngineSpanQuery;
import org.compass.core.engine.SearchEngineQueryBuilder;

import at.molindo.elastic.compass.ElasticSearchEngineQuery.ElasticSearchEngineSpanQuery;
import at.molindo.elastic.compass.query.ElasticSearchEngineBooleanQueryBuilder;
import at.molindo.elastic.compass.query.ElasticSearchEngineQueryStringBuilder;
import at.molindo.elastic.query.ConstantScoreQuery;
import at.molindo.elastic.query.FuzzyQuery;
import at.molindo.elastic.query.MatchAllQuery;
import at.molindo.elastic.query.PrefixQuery;
import at.molindo.elastic.query.Query;
import at.molindo.elastic.query.RangeQuery;
import at.molindo.elastic.query.SpanFirstQuery;
import at.molindo.elastic.query.SpanNotQuery;
import at.molindo.elastic.query.SpanTermQuery;
import at.molindo.elastic.query.TermQuery;
import at.molindo.elastic.query.WildcardQuery;
import at.molindo.elastic.term.Term;

public class ElasticSearchEngineQueryBuilder implements SearchEngineQueryBuilder {

	private ElasticSearchEngineFactory _searchEngineFactory;

	public ElasticSearchEngineQueryBuilder(ElasticSearchEngineFactory searchEngineFactory) {
		_searchEngineFactory = searchEngineFactory;
	}

	@Override
	public SearchEngineBooleanQueryBuilder bool() {
		return bool(false);
	}

	@Override
	public SearchEngineBooleanQueryBuilder bool(boolean disableCoord) {
		return new ElasticSearchEngineBooleanQueryBuilder(_searchEngineFactory, disableCoord);
	}

	@Override
	public SearchEngineMultiPhraseQueryBuilder multiPhrase(String resourcePropertyName) {
		throw new NotImplementedException();
	}

	@Override
	public SearchEngineQueryStringBuilder queryString(String queryString) {
		return new ElasticSearchEngineQueryStringBuilder(_searchEngineFactory, queryString);
	}

	@Override
	public SearchEngineMultiPropertyQueryStringBuilder multiPropertyQueryString(String queryString) {
		throw new NotImplementedException();
	}

	@Override
	public SearchEngineQuery wildcard(String resourcePropertyName, String wildcard) {
		return new ElasticSearchEngineQuery(_searchEngineFactory, new WildcardQuery(resourcePropertyName, wildcard));
	}

	@Override
	public SearchEngineQuery term(String resourcePropertyName, String value) {
		return new ElasticSearchEngineQuery(_searchEngineFactory, TermQuery.string(resourcePropertyName, value));
	}

	@Override
	public SearchEngineQuery matchAll() {
		return new ElasticSearchEngineQuery(_searchEngineFactory, new MatchAllQuery());
	}

	public SearchEngineQuery between(String resourcePropertyName, String low, String high, boolean inclusive, boolean constantScore) {
		Query query;
		
		Term lowTerm = null;
		if (low != null) {
			lowTerm = Term.string(resourcePropertyName, low);
		}
		Term highTerm = null;
		if (high != null) {
			highTerm = Term.string(resourcePropertyName, high);
		}
		query = new RangeQuery(resourcePropertyName).setFrom(lowTerm).setTo(highTerm).setIncludeBoth(inclusive);
		
		if (constantScore) {
			query = new ConstantScoreQuery(query);
		}
		
		return new ElasticSearchEngineQuery(_searchEngineFactory, query);
	}

	public SearchEngineQuery between(String resourcePropertyName, String low, String high, boolean inclusive) {
		return between(resourcePropertyName, low, high, inclusive, true);
	}

	public SearchEngineQuery ge(String resourcePropertyName, String value) {
		return between(resourcePropertyName, value, null, true);
	}

	public SearchEngineQuery gt(String resourcePropertyName, String value) {
		return between(resourcePropertyName, value, null, false);
	}

	public SearchEngineQuery le(String resourcePropertyName, String value) {
		return between(resourcePropertyName, null, value, true);
	}

	public SearchEngineQuery lt(String resourcePropertyName, String value) {
		return between(resourcePropertyName, null, value, false);
	}

	@Override
	public SearchEngineQuery prefix(String resourcePropertyName, String prefix) {
		return new ElasticSearchEngineQuery(_searchEngineFactory, new PrefixQuery(resourcePropertyName, prefix));
	}

	@Override
	public SearchEngineQuery fuzzy(String resourcePropertyName, String value) {
		return new ElasticSearchEngineQuery(_searchEngineFactory, new FuzzyQuery(resourcePropertyName, value));
	}

	@Override
	public SearchEngineQuery fuzzy(String resourcePropertyName, String value, float minimumSimilarity) {
		return new ElasticSearchEngineQuery(_searchEngineFactory, new FuzzyQuery(resourcePropertyName, value).setMinSimilarity(minimumSimilarity));
	}

	@Override
	public SearchEngineQuery fuzzy(String resourcePropertyName, String value, float minimumSimilarity, int prefixLength) {
		return new ElasticSearchEngineQuery(_searchEngineFactory, new FuzzyQuery(resourcePropertyName, value).setMinSimilarity(minimumSimilarity).setPrefixLength(prefixLength));
	}

	@Override
	public SearchEngineSpanQuery spanEq(String resourcePropertyName, String value) {
		return new ElasticSearchEngineSpanQuery(_searchEngineFactory, new SpanTermQuery(resourcePropertyName, value));
	}

	@Override
	public SearchEngineSpanQuery spanFirst(SearchEngineSpanQuery searchEngineSpanQuery, int end) {
		// TODO validate the assumption, that end param changed from Compass to ES
		SpanFirstQuery spanQuery = new SpanFirstQuery(((ElasticSearchEngineSpanQuery) searchEngineSpanQuery).getSpanQuery(), end+1);
        return new ElasticSearchEngineSpanQuery(_searchEngineFactory, spanQuery);
	}

	@Override
	public SearchEngineSpanQuery spanFirst(String resourcePropertyName, String value, int end) {
		return spanFirst(spanEq(resourcePropertyName, value), end);
	}

	@Override
	public SearchEngineQuerySpanNearBuilder spanNear(String resourcePropertyName) {
		throw new NotImplementedException();
	}

	@Override
	public SearchEngineSpanQuery spanNot(SearchEngineSpanQuery include, SearchEngineSpanQuery exclude) {
		SpanNotQuery spanQuery = new SpanNotQuery(((ElasticSearchEngineSpanQuery) include).getSpanQuery(), ((ElasticSearchEngineSpanQuery) exclude).getSpanQuery());
        return new ElasticSearchEngineSpanQuery(_searchEngineFactory, spanQuery);
	}

	@Override
	public SearchEngineQuerySpanOrBuilder spanOr() {
		throw new NotImplementedException();
	}

	@Override
	public SearchEngineMoreLikeThisQueryBuilder moreLikeThis(SearchEngine searchEngine, Resource idResource) {
		throw new NotImplementedException();
	}

	@Override
	public SearchEngineMoreLikeThisQueryBuilder moreLikeThis(SearchEngine searchEngine, Reader reader) {
		throw new NotImplementedException();
	}

}
