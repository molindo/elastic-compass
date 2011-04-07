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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.compass.core.mapping.CompassMapping;
import org.compass.core.mapping.ResourceMapping;

public class DefaultElasticSearchEngineStore implements ElasticSearchEngineStore {

	private CompassMapping _mapping;

	private final Set<String> _types;

	public DefaultElasticSearchEngineStore(ElasticSearchEngineFactory searchEngineFactory, CompassMapping mapping) {
		_mapping = mapping;

		// setup sub indexes and aliases
		_types = new HashSet<String>();
		for (ResourceMapping resourceMapping : mapping.getRootMappings()) {
			_types.add(resourceMapping.getAlias());
		}
	}

	public String[] polyCalcSubIndexes(String[] aliases, Class<?>[] types) {
		HashSet<String> aliasesSet = new HashSet<String>();
		if (aliases != null) {
			for (String alias : aliases) {
				ResourceMapping resourceMapping = _mapping.getRootMappingByAlias(alias);
				if (resourceMapping == null) {
					throw new IllegalArgumentException("No root mapping found for alias [" + alias
							+ "]");
				}
				aliasesSet.add(resourceMapping.getAlias());
				aliasesSet.addAll(Arrays.asList(resourceMapping.getExtendingAliases()));
			}
		}
		if (types != null) {
			for (Class<?> type : types) {
				ResourceMapping resourceMapping = _mapping.getRootMappingByClass(type);
				if (resourceMapping == null) {
					throw new IllegalArgumentException("No root mapping found for class [" + type
							+ "]");
				}
				aliasesSet.add(resourceMapping.getAlias());
				aliasesSet.addAll(Arrays.asList(resourceMapping.getExtendingAliases()));
			}
		}
		return aliasesSet.toArray(new String[aliasesSet.size()]);
	}

}
