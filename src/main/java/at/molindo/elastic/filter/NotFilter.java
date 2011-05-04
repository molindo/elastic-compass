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

import org.elasticsearch.index.query.xcontent.FilterBuilders;
import org.elasticsearch.index.query.xcontent.NotFilterBuilder;
import org.elasticsearch.index.query.xcontent.XContentFilterBuilder;

public class NotFilter extends Filter {

	private NotFilterBuilder _builder;

	public NotFilter(Filter filter) {
		_builder = FilterBuilders.notFilter(filter.getBuilder());
	}

	public NotFilter setCache(boolean cache) {
		_builder.cache(cache);
		return this;
	}

	public XContentFilterBuilder getBuilder() {
		return _builder;
	}

}
