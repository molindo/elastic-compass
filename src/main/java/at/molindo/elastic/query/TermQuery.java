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

import org.elasticsearch.index.query.xcontent.XContentQueryBuilder;

import at.molindo.elastic.term.Term;

public class TermQuery extends Query {

	private XContentQueryBuilder _builder;

	public static Query string(String name, String value) {
		return new TermQuery(Term.string(name, value));
	}
	
	public TermQuery(Term term) {
		_builder = term.buildQuery();
	}
	
	@Override
	public XContentQueryBuilder getBuilder() {
		return _builder;
	}

}
