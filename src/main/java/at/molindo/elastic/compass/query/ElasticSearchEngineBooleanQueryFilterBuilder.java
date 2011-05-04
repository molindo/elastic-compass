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

package at.molindo.elastic.compass.query;

import org.compass.core.engine.SearchEngineQueryFilter;
import org.compass.core.engine.SearchEngineQueryFilterBuilder;

import at.molindo.elastic.compass.ElasticSearchEngineQueryFilter;
import at.molindo.elastic.filter.AndFilter;
import at.molindo.elastic.filter.Filter;
import at.molindo.elastic.filter.NotFilter;
import at.molindo.elastic.filter.OrFilter;

/**
 * @author kimchy
 */
public class ElasticSearchEngineBooleanQueryFilterBuilder implements SearchEngineQueryFilterBuilder.SearchEngineBooleanQueryFilterBuilder {

	private Filter _filter;

	public ElasticSearchEngineBooleanQueryFilterBuilder() {

	}

	public void and(SearchEngineQueryFilter filter) {
		if (_filter == null) {
			_filter = filter(filter);
		} else {
			_filter = new AndFilter(_filter, filter(filter));
		}
	}

	public void or(SearchEngineQueryFilter filter) {
		if (_filter == null) {
			_filter = filter(filter);
		} else {
			_filter = new OrFilter(_filter, filter(filter));
		}
	}

	public void andNot(SearchEngineQueryFilter filter) {
		if (_filter == null) {
			_filter = filter(filter);
		} else {
			_filter = new AndFilter(_filter, new NotFilter(filter(filter)));
		}
	}

	public void xor(SearchEngineQueryFilter filter) {
	}

	private Filter filter(SearchEngineQueryFilter filter) {
		return ((ElasticSearchEngineQueryFilter) filter).getFilter();
	}
	
	public SearchEngineQueryFilter toFilter() {
		return new ElasticSearchEngineQueryFilter(_filter);
	}
}
