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
import org.elasticsearch.index.query.xcontent.SpanTermQueryBuilder;
import org.elasticsearch.index.query.xcontent.XContentSpanQueryBuilder;

public class SpanTermQuery extends SpanBoostQuery<SpanTermQuery> {

	private SpanTermQueryBuilder _builder;

	public SpanTermQuery(String name, int value) {
		_builder = QueryBuilders.spanTermQuery(name, value);
	}
	public SpanTermQuery(String name, long value) {
		_builder = QueryBuilders.spanTermQuery(name, value);
	}
	public SpanTermQuery(String name, float value) {
		_builder = QueryBuilders.spanTermQuery(name, value);
	}
	public SpanTermQuery(String name, double value) {
		_builder = QueryBuilders.spanTermQuery(name, value);
	}
	public SpanTermQuery(String name, String value) {
		_builder = QueryBuilders.spanTermQuery(name, value);
	}
	
	@Override
	public SpanTermQuery setBoost(float boost) {
		_builder.boost(boost);
		return null;
	}

	@Override
	public XContentSpanQueryBuilder getBuilder() {
		return _builder;
	}

}
