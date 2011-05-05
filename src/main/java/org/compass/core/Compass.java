package org.compass.core;

import org.compass.core.config.CompassConfiguration;
import org.compass.core.config.CompassSettings;
import org.compass.core.engine.SearchEngineIndexManager;
import org.compass.core.engine.naming.PropertyNamingStrategy;
import org.compass.core.engine.spellcheck.SearchEngineSpellCheckManager;

import at.molindo.elastic.compass.CompassAdapted;

@CompassAdapted
public interface Compass {

	CompassSettings getSettings();
	
    SearchEngineSpellCheckManager getSpellCheckManager();

	void start();
	
	void stop();
	
	boolean isClosed();

	void close();

	PropertyNamingStrategy getPropertyNamingStrategy();

	ResourceFactory getResourceFactory();

	SearchEngineIndexManager getSearchEngineIndexManager();

	CompassSearchSession openSearchSession();

	CompassIndexSession openIndexSession();
	
	CompassSession openSession();

	CompassQueryBuilder queryBuilder() throws CompassException;
	
	CompassQueryFilterBuilder queryFilterBuilder() throws CompassException;
	
	Compass clone(CompassSettings indexCompassSettings);

    /**
     * Allows to get the configuraion object. One can add settings, and remove or add mappings.
     * Once changes are done, {@link #rebuild()} should be called.
     */
    CompassConfiguration getConfig();

    /**
     * Rebuilds Compass. Taking into account any changes done on the configuration object since
     * the current Compass instance was created.
     *
     * <p>If the rebuild fails, the Compass instance can still work and it will be based on the
     * latest valid Compass instance that was rebuilt.
     */
    void rebuild() throws CompassException;
}
