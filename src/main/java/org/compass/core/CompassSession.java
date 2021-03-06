package org.compass.core;

import at.molindo.elastic.compass.CompassAdapted;

@CompassAdapted
public interface CompassSession extends CompassSearchSession, CompassIndexSession, CompassOperations {

	void create(Object object);

	void setReadOnly();

	void evictAll();

	void flush();

	void create(String alias, Object obj);

	void delete(Resource resource);

	void delete(Object obj);

	void delete(Class<?> clazz, Object obj);

	void delete(String alias, Object obj);

	void save(Object obj);

	void save(String alias, Object obj);

	void evict(Object obj);

	void evict(String alias, Object id);

	CompassQueryBuilder queryBuilder() throws CompassException;

	CompassQueryFilterBuilder queryFilterBuilder() throws CompassException;
	
    CompassAnalyzerHelper analyzerHelper() throws CompassException;

    /**
     * @deprecated use {@link #bindSession()} instead
     */
    @Deprecated
	void beginTransaction();
    
    void bindSession();
    
}
