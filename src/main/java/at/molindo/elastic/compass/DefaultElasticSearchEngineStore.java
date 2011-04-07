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
