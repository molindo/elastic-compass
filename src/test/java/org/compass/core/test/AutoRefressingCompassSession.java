package org.compass.core.test;

import org.compass.core.CompassAnalyzerHelper;
import org.compass.core.CompassException;
import org.compass.core.CompassHits;
import org.compass.core.CompassQuery;
import org.compass.core.CompassQueryBuilder;
import org.compass.core.CompassQueryFilterBuilder;
import org.compass.core.CompassSession;
import org.compass.core.Resource;
import org.compass.core.cache.first.FirstLevelCache;
import org.compass.core.config.CompassSettings;
import org.compass.core.engine.SearchEngine;
import org.compass.core.mapping.CompassMapping;
import org.compass.core.marshall.MarshallingContext;
import org.compass.core.marshall.MarshallingStrategy;
import org.compass.core.metadata.CompassMetaData;
import org.compass.core.spi.DirtyOperationContext;
import org.compass.core.spi.InternalCompass;
import org.compass.core.spi.InternalCompassSession;
import org.compass.core.spi.InternalSessionDelegateClose;

import at.molindo.elastic.compass.ElasticSearchEngine;

public class AutoRefressingCompassSession implements InternalCompassSession {

	private final InternalCompassSession _wrapped;
	private boolean _dirty;

	public AutoRefressingCompassSession(CompassSession wrapped) {
		if (wrapped == null) {
			throw new NullPointerException("wrapped");
		}
		_wrapped = (InternalCompassSession) wrapped;
		_dirty = true;
	}

	public void create(Object object) {
		_wrapped.create(object);
		_dirty = true;
	}

	public void setReadOnly() {
		_wrapped.setReadOnly();
	}

	public void evictAll() {
		_wrapped.evictAll();
	}

	public void flush() {
		_wrapped.flush();
		refreshIfDirty();
	}

	public void create(String alias, Object obj) {
		_wrapped.create(alias, obj);
		_dirty = true;
	}

	public void delete(Resource resource) {
		_wrapped.delete(resource);
		_dirty = true;
	}

	public void delete(Object obj) {
		_wrapped.delete(obj);
		_dirty = true;
	}

	public void delete(Class<?> clazz, Object obj) {
		_wrapped.delete(clazz, obj);
		_dirty = true;
	}

	public void delete(String alias, Object obj) {
		_wrapped.delete(alias, obj);
		_dirty = true;
	}

	public void save(Object obj) {
		_wrapped.save(obj);
		_dirty = true;
	}

	public void save(String alias, Object obj) {
		_wrapped.save(alias, obj);
		_dirty = true;
	}

	public void evict(Object obj) {
		_wrapped.evict(obj);
	}

	public void evict(String alias, Object id) {
		_wrapped.evict(alias, id);
	}

	public CompassQueryBuilder queryBuilder() throws CompassException {
		refreshIfDirty();
		return _wrapped.queryBuilder();
	}

	public CompassQueryFilterBuilder queryFilterBuilder() throws CompassException {
		refreshIfDirty();
		return _wrapped.queryFilterBuilder();
	}

	public CompassAnalyzerHelper analyzerHelper() throws CompassException {
		refreshIfDirty();
		return _wrapped.analyzerHelper();
	}

	public CompassSettings getSettings() {
		return _wrapped.getSettings();
	}

	public Object get(String alias, Object id) throws CompassException {
		refreshIfDirty();
		return _wrapped.get(alias, id);
	}

	public <T> T get(Class<T> clazz, Object id) throws CompassException {
		refreshIfDirty();
		return _wrapped.get(clazz, id);
	}

	public Object get(String alias, Object... ids) throws CompassException {
		refreshIfDirty();
		return _wrapped.get(alias, ids);
	}

	public <T> T load(Class<T> clazz, Object id) throws CompassException {
		refreshIfDirty();
		return _wrapped.load(clazz, id);
	}

	public Object load(String alias, Object id) {
		refreshIfDirty();
		return _wrapped.load(alias, id);
	}

	public Resource getResource(Class<?> clazz, Object id) {
		refreshIfDirty();
		return _wrapped.getResource(clazz, id);
	}

	public Resource getResource(String alias, Object id) {
		refreshIfDirty();
		return _wrapped.getResource(alias, id);
	}

	public Resource loadResource(Class<?> clazz, Object id) {
		refreshIfDirty();
		return _wrapped.loadResource(clazz, id);
	}

	public Resource loadResource(String alias, Object id) {
		refreshIfDirty();
		return _wrapped.loadResource(alias, id);
	}

	public CompassHits find(String query) {
		refreshIfDirty();
		return _wrapped.find(query);
	}

	public void close() {
		_wrapped.close();
	}

	public void delete(String alias, Object... ids) throws CompassException {
		_wrapped.delete(alias, ids);
		_dirty = true;
	}

	public void delete(Class<?> clazz, Object... ids) throws CompassException {
		_wrapped.delete(clazz, ids);
		_dirty = true;
	}

	public void delete(CompassQuery query) throws CompassException {
		_wrapped.delete(query);
		_dirty = true;
	}

	public Resource getResource(Class<?> clazz, Object... ids) throws CompassException {
		refreshIfDirty();
		return _wrapped.getResource(clazz, ids);
	}

	public boolean isClosed() {
		return _wrapped.isClosed();
	}

	public Resource getResource(String alias, Object... ids) throws CompassException {
		refreshIfDirty();
		return _wrapped.getResource(alias, ids);
	}

	public Resource loadResource(Class<?> clazz, Object... ids) throws CompassException {
		refreshIfDirty();
		return _wrapped.loadResource(clazz, ids);
	}

	public Resource loadResource(String alias, Object... ids) throws CompassException {
		refreshIfDirty();
		return _wrapped.loadResource(alias, ids);
	}

	public <T> T get(Class<T> clazz, Object... ids) throws CompassException {
		refreshIfDirty();
		return _wrapped.get(clazz, ids);
	}

	public <T> T load(Class<T> clazz, Object... ids) throws CompassException {
		refreshIfDirty();
		return _wrapped.load(clazz, ids);
	}

	public Object load(String alias, Object... ids) throws CompassException {
		refreshIfDirty();
		return _wrapped.load(alias, ids);
	}

	public void evict(Resource resource) {
		_wrapped.evict(resource);
	}
	
	// InternalCompassSession

	public InternalCompass getCompass() {
		return _wrapped.getCompass();
	}

	public FirstLevelCache getFirstLevelCache() {
		return _wrapped.getFirstLevelCache();
	}

	public CompassMapping getMapping() {
		return _wrapped.getMapping();
	}

	public CompassMetaData getMetaData() {
		return _wrapped.getMetaData();
	}

	public SearchEngine getSearchEngine() {
		return _wrapped.getSearchEngine();
	}

	public MarshallingStrategy getMarshallingStrategy() {
		return _wrapped.getMarshallingStrategy();
	}

	public boolean isReadOnly() {
		return _wrapped.isReadOnly();
	}

	public void delete(Object value, DirtyOperationContext context) {
		_wrapped.delete(value, context);
		_dirty = true;
	}

	public void create(Object value, DirtyOperationContext context) {
		_wrapped.create(value, context);
		_dirty = true;
	}

	public void save(Object value, DirtyOperationContext context) {
		_wrapped.save(value, context);
		_dirty = true;
	}

	public Object getByResource(Resource resource) throws CompassException {
		refreshIfDirty();
		return _wrapped.getByResource(resource);
	}

	public void addDelegateClose(InternalSessionDelegateClose delegateClose) {
		_wrapped.addDelegateClose(delegateClose);
	}

	public Resource getResourceByIdResource(Resource idResource) throws CompassException {
		refreshIfDirty();
		return _wrapped.getResourceByIdResource(idResource);
	}

	public Resource getResourceByIdResourceNoCache(Resource idResource) throws CompassException {
		refreshIfDirty();
		return _wrapped.getResourceByIdResourceNoCache(idResource);
	}

	public Object get(String alias, Object id, MarshallingContext context) throws CompassException {
		refreshIfDirty();
		return _wrapped.get(alias, id, context);
	}

	// refreshing
	
	void refreshIfDirty() {
		if (_dirty) {
			refresh();
		}
	}

	void refresh() {
		((ElasticSearchEngine) _wrapped.getSearchEngine()).refresh();
		_dirty = false;
	}
}
