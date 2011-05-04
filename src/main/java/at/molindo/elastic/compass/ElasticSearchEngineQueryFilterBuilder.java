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

import org.compass.core.engine.SearchEngineQuery;
import org.compass.core.engine.SearchEngineQueryFilter;
import org.compass.core.engine.SearchEngineQueryFilterBuilder;

import at.molindo.elastic.compass.query.ElasticSearchEngineBooleanQueryFilterBuilder;
import at.molindo.elastic.filter.Filter;
import at.molindo.elastic.filter.QueryFilter;
import at.molindo.elastic.filter.RangeFilter;
import at.molindo.elastic.term.Term;

public class ElasticSearchEngineQueryFilterBuilder implements SearchEngineQueryFilterBuilder {

	public SearchEngineQueryFilter between(String resourcePropertyName, String low, String high, boolean include) {
		return between(resourcePropertyName, low, high, include, include);
	}

	@Override
	public SearchEngineQueryFilter between(String resourcePropertyName, String low, String high, boolean includeLow, boolean includeHigh) {
		Filter filter;

		Term lowTerm = null;
		if (low != null) {
			lowTerm = Term.string(resourcePropertyName, low);
		}
		Term highTerm = null;
		if (high != null) {
			highTerm = Term.string(resourcePropertyName, high);
		}
		filter = new RangeFilter(resourcePropertyName).setFrom(lowTerm).setTo(highTerm)
				.setIncludeLower(includeLow).setIncludeUpper(includeHigh);

		return new ElasticSearchEngineQueryFilter(filter);
	}

	@Override
	public SearchEngineQueryFilter lt(String resourcePropertyName, String value) {
		return between(resourcePropertyName, null, value, false);
	}

	@Override
	public SearchEngineQueryFilter le(String resourcePropertyName, String value) {
		return between(resourcePropertyName, null, value, true);
	}

	@Override
	public SearchEngineQueryFilter gt(String resourcePropertyName, String value) {
		return between(resourcePropertyName, value, null, false);
	}

	@Override
	public SearchEngineQueryFilter ge(String resourcePropertyName, String value) {
		return between(resourcePropertyName, value, null, true);
	}

	@Override
	public SearchEngineQueryFilter query(SearchEngineQuery query) {
		return new ElasticSearchEngineQueryFilter(new QueryFilter((((ElasticSearchEngineQuery) query)
				.getQuery())));
	}

	@Override
	public SearchEngineBooleanQueryFilterBuilder bool() {
		return new ElasticSearchEngineBooleanQueryFilterBuilder();
	}

}
