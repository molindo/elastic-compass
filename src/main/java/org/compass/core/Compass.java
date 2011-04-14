package org.compass.core;

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
	
	CompassSession openSession();

	CompassQueryBuilder queryBuilder() throws CompassException;
	
	CompassQueryFilterBuilder queryFilterBuilder() throws CompassException;
	
	Compass clone(CompassSettings indexCompassSettings);

}
