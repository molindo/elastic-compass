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

package at.molindo.elastic.filter;

import org.elasticsearch.index.query.xcontent.AndFilterBuilder;
import org.elasticsearch.index.query.xcontent.FilterBuilders;
import org.elasticsearch.index.query.xcontent.XContentFilterBuilder;

public class AndFilter extends Filter {

	private AndFilterBuilder _builder;

	public AndFilter(Filter... filters) {

		XContentFilterBuilder[] builders = new XContentFilterBuilder[filters.length];
		for (int i = 0; i < filters.length; i++) {
			builders[i] = filters[i].getBuilder();
		}

		_builder = FilterBuilders.andFilter(builders);
	}

	public AndFilter add(Filter filter) {
		_builder.add(filter.getBuilder());
		return this;
	}

	public AndFilter setCache(boolean cache) {
		_builder.cache(cache);
		return this;
	}

	public XContentFilterBuilder getBuilder() {
		return _builder;
	}

}
