/*
 * Copyright 2004-2009 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.molindo.elastic.compass;

import java.util.ArrayList;
import java.util.Locale;

import org.compass.core.CompassQuery.SortDirection;
import org.compass.core.CompassQuery.SortImplicitType;
import org.compass.core.CompassQuery.SortPropertyType;
import org.compass.core.engine.SearchEngine;
import org.compass.core.engine.SearchEngineHits;
import org.compass.core.engine.SearchEngineQuery;
import org.compass.core.engine.SearchEngineQueryFilter;

import at.molindo.elastic.query.BooleanClause;
import at.molindo.elastic.query.BooleanQuery;
import at.molindo.elastic.query.BoostQuery;
import at.molindo.elastic.query.ElasticSpanQuery;
import at.molindo.elastic.query.Query;
import at.molindo.elastic.query.SortField;
import at.molindo.elastic.query.SortField.SortType;
import at.molindo.elastic.query.Term;
import at.molindo.elastic.query.TermQuery;

/**
 * @author kimchy
 */
public class ElasticSearchEngineQuery implements SearchEngineQuery, Cloneable {

	public static class ElasticSearchEngineSpanQuery extends ElasticSearchEngineQuery implements SearchEngineSpanQuery {

		public ElasticSearchEngineSpanQuery(ElasticSearchEngineFactory searchEngineFactory, ElasticSpanQuery query) {
			super(searchEngineFactory, query);
		}

	}

	public static class QueryHolder {

		private final Query query;

		private final boolean suggested;

		public QueryHolder(Query query) {
			this(query, false);
		}

		public QueryHolder(Query query, boolean suggested) {
			this.query = query;
			this.suggested = suggested;
		}

		public Query getQuery() {
			return query;
		}

		public boolean isSuggested() {
			return suggested;
		}
	}

	private final ElasticSearchEngineFactory searchEngineFactory;

	private ArrayList<SortField> sortFields = new ArrayList<SortField>();

	private String[] aliases;

	// private ElasticSearchEngineQueryFilter filter;

	private Query origQuery;

	private Query query;

	private String defaultSearchProperty;

	private boolean rewrite;

	private boolean suggested;

	public ElasticSearchEngineQuery(ElasticSearchEngineFactory searchEngineFactory, Query query) {
		this(searchEngineFactory, new QueryHolder(query));
	}

	public ElasticSearchEngineQuery(ElasticSearchEngineFactory searchEngineFactory, QueryHolder query) {
		this(searchEngineFactory, query, searchEngineFactory.getElasticSettings()
				.getDefaultSearchPropery());
	}

	public ElasticSearchEngineQuery(ElasticSearchEngineFactory searchEngineFactory, QueryHolder query, String defualtSearchProperty) {
		this.searchEngineFactory = searchEngineFactory;
		this.query = query.getQuery();
		this.origQuery = query.getQuery();
		this.suggested = query.isSuggested();
		this.defaultSearchProperty = defualtSearchProperty;
	}

	public SearchEngineQuery addSort(String propertyName) {
		throw new NotImplementedException();
	}

	public SearchEngineQuery addSort(String propertyName, SortDirection direction) {
		throw new NotImplementedException();
	}

	public SearchEngineQuery addSort(String propertyName, SortPropertyType type) {
		sortFields.add(new SortField(propertyName, getSortType(type)));
		return this;
	}

	public SearchEngineQuery addSort(String propertyName, SortPropertyType type, SortDirection direction) {
		sortFields.add(new SortField(propertyName, getSortType(type), getSortReverse(direction)));
		return this;
	}

	public SearchEngineQuery addSort(SortImplicitType implicitType) {
		sortFields.add(new SortField(null, getImplicitSortField(implicitType)));
		return this;
	}

	public SearchEngineQuery addSort(SortImplicitType implicitType, SortDirection direction) {
		sortFields
				.add(new SortField(null, getImplicitSortField(implicitType), getSortReverse(direction)));
		return this;
	}

	public SearchEngineQuery addSort(String propertyName, Locale locale, SortDirection direction) {
		sortFields.add(new SortField(propertyName, locale, getSortReverse(direction)));
		return this;
	}

	public SearchEngineQuery addSort(String propertyName, Locale locale) {
		sortFields.add(new SortField(propertyName, locale));
		return this;
	}

	public SearchEngineQuery addSort(SortField sortField) {
		sortFields.add(sortField);
		return this;
	}

	private SortType getImplicitSortField(SortImplicitType implicitType) {
		switch (implicitType) {
		case DOC:
			return SortField.SortType.DOC;
		case SCORE:
			return SortField.SortType.SCORE;
		default:
			throw new IllegalArgumentException("Faile to create lucene implicit type for ["
					+ implicitType + "]");
		}
	}

	private boolean getSortReverse(SortDirection direction) {
		return direction == SortDirection.REVERSE;
	}

	private SortType getSortType(SortPropertyType type) {
		switch (type) {
		case AUTO:
			return SortField.SortType.SCORE;
		case BYTE:
			return SortField.SortType.BYTE;
		case DOUBLE:
			return SortField.SortType.DOUBLE;
		case FLOAT:
			return SortField.SortType.FLOAT;
		case INT:
			return SortField.SortType.INT;
		case LONG:
			return SortField.SortType.LONG;
		case STRING:
			return SortField.SortType.STRING;
		default:
			throw new IllegalArgumentException("Failed to convert type [" + type + "]");
		}
	}

	public SearchEngineHits hits(SearchEngine searchEngine) {
		return ((ElasticSearchEngine) searchEngine).find(this);
	}

	public long count(SearchEngine searchEngine) {
		return count(searchEngine, 0.0f);
	}

	public long count(SearchEngine searchEngine, float minimumScore) {
		throw new NotImplementedException();
	}

	public SearchEngineQuery setBoost(float boost) {
		if (query instanceof BoostQuery) {
			((BoostQuery<?>) query).setBoost(boost);
		}
		return this;
	}

	public SearchEngineQuery setAliases(String[] aliases) {
		if (aliases == null) {
			query = origQuery;
			return this;
		}

		String aliasProperty = searchEngineFactory.getAliasProperty();
		BooleanQuery boolQuery2 = new BooleanQuery();
		for (String alias : aliases) {
			boolQuery2
					.add(new TermQuery(new Term.StringTerm(aliasProperty, alias)), BooleanClause.Occur.SHOULD);
		}

		BooleanQuery boolQuery = new BooleanQuery();
		boolQuery.add(origQuery, BooleanClause.Occur.MUST);
		boolQuery.add(boolQuery2, BooleanClause.Occur.MUST);
		this.query = boolQuery;

		this.aliases = aliases;

		return this;
	}

	public String[] getAliases() {
		return this.aliases;
	}

	public SearchEngineQuery rewrite() {
		this.rewrite = true;
		return this;
	}

	public boolean isRewrite() {
		return this.rewrite;
	}

	public boolean isSuggested() {
		return this.suggested;
	}

	public Query getOriginalQuery() {
		return this.origQuery;
	}

	public Query getQuery() {
		return this.query;
	}

	public String toString() {
		if (query == null) {
			return "<null>";
		}
		// remove the "zzz-all:" prefix
		return query.toString().replace(defaultSearchProperty + ":", "");
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	// breaks encapsulation, but we need it

	public void setQuery(Query query) {
		this.query = query;
		this.origQuery = query;
	}

	public void setSuggested(boolean suggested) {
		this.suggested = suggested;
	}

	@Override
	public SearchEngineQuery setFilter(SearchEngineQueryFilter filter) {
		throw new NotImplementedException();
	}
}
