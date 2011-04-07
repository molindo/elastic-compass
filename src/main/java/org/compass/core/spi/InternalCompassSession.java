package org.compass.core.spi;

import org.compass.core.CompassException;
import org.compass.core.CompassSession;
import org.compass.core.Resource;
import org.compass.core.cache.first.FirstLevelCache;
import org.compass.core.engine.SearchEngine;
import org.compass.core.mapping.CompassMapping;
import org.compass.core.metadata.CompassMetaData;

import at.molindo.elastic.compass.CompassAdapted;

@CompassAdapted
public interface InternalCompassSession extends CompassSession {

	InternalCompass getCompass();

	FirstLevelCache getFirstLevelCache();

	CompassMapping getMapping();
	
    CompassMetaData getMetaData();
	
	SearchEngine getSearchEngine();

	boolean isReadOnly();

	boolean isClosed();

	void delete(Object value, DirtyOperationContext context);

	void create(Object value, DirtyOperationContext context);

	void save(Object value, DirtyOperationContext context);

	Object getByResource(Resource resource) throws CompassException;
}
