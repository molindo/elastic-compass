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

package at.molindo.elastic.query;

import org.elasticsearch.index.query.xcontent.ConstantScoreQueryBuilder;
import org.elasticsearch.index.query.xcontent.FilterBuilders;
import org.elasticsearch.index.query.xcontent.QueryBuilders;
import org.elasticsearch.index.query.xcontent.XContentQueryBuilder;

import at.molindo.elastic.filter.Filter;

public class ConstantScoreQuery extends Query {

	private ConstantScoreQueryBuilder _builder;

	public ConstantScoreQuery(Query query) {
		_builder = QueryBuilders.constantScoreQuery(FilterBuilders.queryFilter(query.getBuilder()));
	}
	
	public ConstantScoreQuery(Filter filter) {
		_builder = QueryBuilders.constantScoreQuery(filter.getBuilder());
	}
	
	@Override
	public XContentQueryBuilder getBuilder() {
		return _builder;
	}

}
