package org.compass.core.engine;

import org.compass.core.Resource;

import at.molindo.elastic.compass.CompassAdapted;

@CompassAdapted
public interface SearchEngine {

	SearchEngineFactory getSearchEngineFactory();

	void create(Resource resource);

	Resource get(Resource idResource);

	SearchEngineQueryBuilder queryBuilder();

	void delete(SearchEngineQuery searchEngineQuery);

	void setReadOnly();

	boolean isReadOnly();

}
