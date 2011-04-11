package org.compass.core;

import org.compass.core.config.CompassSettings;
import org.compass.core.engine.SearchEngineIndexManager;
import org.compass.core.engine.naming.PropertyNamingStrategy;

import at.molindo.elastic.compass.CompassAdapted;

@CompassAdapted
public interface Compass {

	CompassSettings getSettings();

	void start();
	
	void stop();
	
	boolean isClosed();

	void close();

	PropertyNamingStrategy getPropertyNamingStrategy();

	ResourceFactory getResourceFactory();
	

	SearchEngineIndexManager getSearchEngineIndexManager();

	CompassSession openSession();

	Compass clone(CompassSettings indexCompassSettings);

}
