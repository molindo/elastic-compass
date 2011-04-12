package org.compass.core.engine;

import org.compass.core.Resource;

import at.molindo.elastic.compass.CompassAdapted;

@CompassAdapted
public interface SearchEngine {

	SearchEngineFactory getSearchEngineFactory();

	void create(Resource resource);

	Resource load(Resource idResource);
	
	Resource get(Resource idResource);

	void delete(Resource resource);
	
	SearchEngineQueryBuilder queryBuilder();
	
	void delete(SearchEngineQuery searchEngineQuery);

	void setReadOnly();

	boolean isReadOnly();

	void save(Resource resource);

	void flush();
}
