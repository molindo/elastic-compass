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

public class ElasticSearchEngineQueryFilterBuilder implements SearchEngineQueryFilterBuilder {

	@Override
	public SearchEngineQueryFilter between(String resourcePropertyName, String low, String high, boolean includeLow, boolean includeHigh) {
		throw new NotImplementedException();
	}

	@Override
	public SearchEngineQueryFilter lt(String resourcePropertyName, String value) {
		throw new NotImplementedException();
	}

	@Override
	public SearchEngineQueryFilter le(String resourcePropertyName, String value) {
		throw new NotImplementedException();
	}

	@Override
	public SearchEngineQueryFilter gt(String resourcePropertyName, String value) {
		throw new NotImplementedException();
	}

	@Override
	public SearchEngineQueryFilter ge(String resourcePropertyName, String value) {
		throw new NotImplementedException();
	}

	@Override
	public SearchEngineQueryFilter query(SearchEngineQuery query) {
		throw new NotImplementedException();
	}

	@Override
	public SearchEngineBooleanQueryFilterBuilder bool() {
		throw new NotImplementedException();
	}

}
