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

import org.compass.core.engine.SearchEngineInternalSearch;

public class ElasticSearchEngineInternalSearch implements SearchEngineInternalSearch {

	private final ElasticClient _client;
	private String[] _aliases;

	public ElasticSearchEngineInternalSearch(ElasticClient client, String[] aliases) {
		if (client == null) {
			throw new NullPointerException("client");
		}
		_client = client;
		_aliases = aliases;
	}

	public ElasticClient getClient() {
		return _client;
	}

	public String[] getAliases() {
		return _aliases;
	}

	public void setAliases(String[] aliases) {
		_aliases = aliases;
	}
	
}