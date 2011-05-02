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

import org.elasticsearch.index.query.xcontent.IdsQueryBuilder;
import org.elasticsearch.index.query.xcontent.QueryBuilders;
import org.elasticsearch.index.query.xcontent.XContentQueryBuilder;

public class IdsQuery extends BoostQuery<IdsQuery> {

	private IdsQueryBuilder _builder;

	public IdsQuery(String name) {
		_builder = QueryBuilders.idsQuery(name);
	}

	public IdsQuery addIds(String... ids) {
		_builder.addIds(ids);
		return this;
	}
	
	public IdsQuery setBoost(float boost) {
		_builder.boost(boost);
		return this;
	}

	@Override
	public XContentQueryBuilder getBuilder() {
		return _builder;
	}

}
