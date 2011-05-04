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

import org.elasticsearch.index.query.xcontent.BoolFilterBuilder;
import org.elasticsearch.index.query.xcontent.FilterBuilders;
import org.elasticsearch.index.query.xcontent.XContentFilterBuilder;

import at.molindo.elastic.query.BooleanClause.Occur;

public class BooleanFilter extends Filter {

	private BoolFilterBuilder _builder;

	public BooleanFilter() {
		_builder = FilterBuilders.boolFilter();
	}
	
	public void add(Filter filter, Occur occur) {
		switch (occur) {
		case MUST:
			_builder.must(filter.getBuilder());
			break;
		case MUST_NOT:
			_builder.mustNot(filter.getBuilder());
			break;
		case SHOULD:
			_builder.should(filter.getBuilder());
			break;
		default:
			throw new RuntimeException("unknown occur: " + occur);
		}
	}

	public BooleanFilter setCache(boolean cache) {
		_builder.cache(cache);
		return this;
	}
	
	public XContentFilterBuilder getBuilder() {
		return _builder;
	}

}
