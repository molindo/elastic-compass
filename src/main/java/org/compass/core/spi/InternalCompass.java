package org.compass.core.spi;

import org.compass.core.Compass;
import org.compass.core.config.CompassSettings;
import org.compass.core.converter.ConverterLookup;
import org.compass.core.engine.SearchEngineFactory;
import org.compass.core.engine.SearchEngineIndexManager;
import org.compass.core.events.CompassEventManager;
import org.compass.core.events.RebuildEventListener;
import org.compass.core.mapping.CompassMapping;
import org.compass.core.metadata.CompassMetaData;

public interface InternalCompass extends Compass {

    CompassSettings getSettings();

    CompassMapping getMapping();

	SearchEngineFactory getSearchEngineFactory();

	void addRebuildEventListener(RebuildEventListener rebuildEventListener);

	CompassMetaData getMetaData();

	ConverterLookup getConverterLookup();

	CompassEventManager getEventManager();

}
