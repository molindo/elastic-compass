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

import at.molindo.elastic.compass.query.ElasticSearchEngineQueryStringBuilder;

public class ElasticSearchEngineQueryBuilder implements SearchEngineQueryBuilder {

	private ElasticSearchEngineFactory _searchEngineFactory;

	public ElasticSearchEngineQueryBuilder(ElasticSearchEngineFactory searchEngineFactory) {
		_searchEngineFactory = searchEngineFactory;
	}

	@Override
	public SearchEngineBooleanQueryBuilder bool() {
		throw new NotImplementedException();
	}

	@Override
	public SearchEngineBooleanQueryBuilder bool(boolean disableCoord) {
		throw new NotImplementedException();
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
		throw new NotImplementedException();
	}

	@Override
	public SearchEngineQuery term(String resourcePropertyName, String value) {
		throw new NotImplementedException();
	}

	@Override
	public SearchEngineQuery matchAll() {
		throw new NotImplementedException();
	}

	@Override
	public SearchEngineQuery between(String resourcePropertyName, String low, String high, boolean inclusive, boolean constantScore) {
		throw new NotImplementedException();
	}

	@Override
	public SearchEngineQuery between(String resourcePropertyName, String low, String high, boolean inclusive) {
		throw new NotImplementedException();
	}

	@Override
	public SearchEngineQuery lt(String resourcePropertyName, String value) {
		throw new NotImplementedException();
	}

	@Override
	public SearchEngineQuery le(String resourcePropertyName, String value) {
		throw new NotImplementedException();
	}

	@Override
	public SearchEngineQuery gt(String resourcePropertyName, String value) {
		throw new NotImplementedException();
	}

	@Override
	public SearchEngineQuery ge(String resourcePropertyName, String value) {
		throw new NotImplementedException();
	}

	@Override
	public SearchEngineQuery prefix(String resourcePropertyName, String prefix) {
		throw new NotImplementedException();
	}

	@Override
	public SearchEngineQuery fuzzy(String resourcePropertyName, String value) {
		throw new NotImplementedException();
	}

	@Override
	public SearchEngineQuery fuzzy(String resourcePropertyName, String value, float minimumSimilarity) {
		throw new NotImplementedException();
	}

	@Override
	public SearchEngineQuery fuzzy(String resourcePropertyName, String value, float minimumSimilarity, int prefixLength) {
		throw new NotImplementedException();
	}

	@Override
	public SearchEngineSpanQuery spanEq(String resourcePropertyName, String value) {
		throw new NotImplementedException();
	}

	@Override
	public SearchEngineSpanQuery spanFirst(SearchEngineSpanQuery searchEngineSpanQuery, int end) {
		throw new NotImplementedException();
	}

	@Override
	public SearchEngineSpanQuery spanFirst(String resourcePropertyName, String value, int end) {
		throw new NotImplementedException();
	}

	@Override
	public SearchEngineQuerySpanNearBuilder spanNear(String resourcePropertyName) {
		throw new NotImplementedException();
	}

	@Override
	public SearchEngineSpanQuery spanNot(SearchEngineSpanQuery include, SearchEngineSpanQuery exclude) {
		throw new NotImplementedException();
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
