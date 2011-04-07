package org.compass.core;

import org.compass.core.config.CompassSettings;

import at.molindo.elastic.compass.CompassAdapted;

@CompassAdapted
public interface CompassSession {

	void create(Object object);

    Object get(String alias, Object id) throws CompassException;
    
    <T> T get(Class<T> clazz, Object id) throws CompassException;
	
    Object get(String alias, Object... ids) throws CompassException;
    
    <T> T load(Class<T> clazz, Object id) throws CompassException;

	Object load(String alias, Object id);

	void setReadOnly();

	CompassSettings getSettings();

	void evictAll();

	void flush();

	void close();

	void create(String alias, Resource obj);

	void create(String alias, Object obj);

	void delete(Resource resource);
	
	void delete(Object obj);

	CompassHits find(String query);

	void delete(Class<?> clazz, Object obj);

	void delete(String alias, Object obj);

	Resource getResource(Class<?> clazz, Object id);

	Resource getResource(String alias, Object id);

	Resource loadResource(Class<?> clazz, Object id);
	
	Resource loadResource(String alias, Object id);

	void save(Object obj);

	void save(String alias, Object obj);

	void evict(Object obj);

	void evict(String alias, Object id);

}
