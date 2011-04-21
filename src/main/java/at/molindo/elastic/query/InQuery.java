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

import org.elasticsearch.index.query.xcontent.QueryBuilders;
import org.elasticsearch.index.query.xcontent.TermsQueryBuilder;
import org.elasticsearch.index.query.xcontent.XContentQueryBuilder;

public class InQuery extends BoostQuery<InQuery> {

	private TermsQueryBuilder _builder;

	public InQuery(String name, int... values) {
		_builder = QueryBuilders.inQuery(name, values);
	}

	public InQuery(String name, long... values) {
		_builder = QueryBuilders.inQuery(name, values);
	}

	public InQuery(String name, float... values) {
		_builder = QueryBuilders.inQuery(name, values);
	}

	public InQuery(String name, double... values) {
		_builder = QueryBuilders.inQuery(name, values);
	}

	public InQuery(String name, String... values) {
		_builder = QueryBuilders.inQuery(name, values);
	}

	public InQuery(String name, Object... values) {
		_builder = QueryBuilders.inQuery(name, values);
	}

	public InQuery setMinimumMatch(int minimumMatch) {
		_builder.minimumMatch(minimumMatch);
		return this;
	}

	public InQuery setBoost(float boost) {
		_builder.boost(boost);
		return this;
	}

	public InQuery setDisableCoord(boolean disableCoord) {
		_builder.disableCoord(disableCoord);
		return this;
	}

	@Override
	public XContentQueryBuilder getBuilder() {
		return _builder;
	}

}
