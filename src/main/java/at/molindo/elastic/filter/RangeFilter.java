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
import org.elasticsearch.index.query.xcontent.RangeFilterBuilder;
import org.elasticsearch.index.query.xcontent.XContentFilterBuilder;

import at.molindo.elastic.term.Term;

public class RangeFilter extends Filter {

	private RangeFilterBuilder _builder;

	public RangeFilter(String property) {
		_builder = FilterBuilders.rangeFilter(property);
	}

	public RangeFilter setFrom(Term from) {
		_builder.from(from == null ? null : from.getValue());
		return this;
	}

	public RangeFilter setTo(Term to) {
		_builder.to(to == null ? null : to.getValue());
		return this;
	}

	public RangeFilter setIncludeLower(boolean includeLower) {
		_builder.includeLower(includeLower);
		return this;
	}

	public RangeFilter setIncludeUpper(boolean includeUpper) {
		_builder.includeUpper(includeUpper);
		return this;
	}

	public RangeFilter setIncludeBoth(boolean includeBoth) {
		_builder.includeUpper(includeBoth).includeLower(includeBoth);
		return this;
	}
	
	@Override
	public XContentFilterBuilder getBuilder() {
		return _builder;
	}

}
