package org.compass.core.engine;

import org.compass.core.ResourceFactory;
import org.compass.core.config.RuntimeCompassSettings;
import org.compass.core.engine.naming.PropertyNamingStrategy;
import org.compass.core.mapping.CompassMapping;

import at.molindo.elastic.compass.CompassAdapted;

@CompassAdapted
public interface SearchEngineFactory {

	CompassMapping getMapping();

	SearchEngineIndexManager getIndexManager();

	void start();

	void stop();
	
	void close();

	ResourceFactory getResourceFactory();

	SearchEngine openSearchEngine(RuntimeCompassSettings runtimeSettings);

    /**
     * Returns the name of the alias property.
     *
     * @return The name of the alias property.
     */
    String getAliasProperty();

    /**
     * Returns the name of the extending alias property name.
     */
    String getExtendedAliasProperty();

	SearchEngineQueryBuilder queryBuilder() throws SearchEngineException;

	SearchEngineQueryFilterBuilder queryFilterBuilder() throws SearchEngineException;
	
    PropertyNamingStrategy getPropertyNamingStrategy();
}
