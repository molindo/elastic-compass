package org.compass.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.compass.core.CompassAnalyzerHelper;
import org.compass.core.CompassException;
import org.compass.core.CompassHits;
import org.compass.core.CompassQuery;
import org.compass.core.CompassQueryBuilder;
import org.compass.core.CompassQueryFilterBuilder;
import org.compass.core.CompassSession;
import org.compass.core.CompassSessionFactory;
import org.compass.core.Resource;
import org.compass.core.cache.first.FirstLevelCache;
import org.compass.core.cascade.CascadingManager;
import org.compass.core.config.CompassSettings;
import org.compass.core.config.RuntimeCompassSettings;
import org.compass.core.engine.SearchEngine;
import org.compass.core.engine.SearchEngineAnalyzerHelper;
import org.compass.core.engine.SearchEngineQueryBuilder;
import org.compass.core.events.FilterOperation;
import org.compass.core.mapping.Cascade;
import org.compass.core.mapping.CompassMapping;
import org.compass.core.marshall.DefaultMarshallingStrategy;
import org.compass.core.marshall.MarshallingContext;
import org.compass.core.marshall.MarshallingException;
import org.compass.core.marshall.MarshallingStrategy;
import org.compass.core.metadata.CompassMetaData;
import org.compass.core.spi.DirtyOperationContext;
import org.compass.core.spi.InternalCompass;
import org.compass.core.spi.InternalCompassSession;
import org.compass.core.spi.InternalResource;
import org.compass.core.spi.InternalSessionDelegateClose;
import org.compass.core.spi.ResourceKey;
import org.hibernate.SessionFactory;

public class DefaultCompassSession implements InternalCompassSession {

	private InternalCompass compass;
	private CompassMapping mapping;
	private CompassMetaData compassMetaData;
	private RuntimeCompassSettings runtimeSettings;
	private SearchEngine searchEngine;
	private FirstLevelCache firstLevelCache;
	private DefaultMarshallingStrategy marshallingStrategy;
	private boolean closed;
	private CascadingManager cascadingManager;
	private final List<InternalSessionDelegateClose> delegateClose = new ArrayList<InternalSessionDelegateClose>();

	public DefaultCompassSession(RuntimeCompassSettings runtimeSettings, InternalCompass compass, SearchEngine searchEngine, FirstLevelCache firstLevelCache) {

		this.compass = compass;
		this.mapping = compass.getMapping();
		this.compassMetaData = compass.getMetaData();
		this.runtimeSettings = runtimeSettings;
		this.searchEngine = searchEngine;
		this.firstLevelCache = firstLevelCache;
		this.marshallingStrategy = new DefaultMarshallingStrategy(mapping, searchEngine, compass.getConverterLookup(), this);
		this.cascadingManager = new CascadingManager(this);
	}

	public void create(String alias, Object object) throws CompassException {
		checkClosed();
		create(alias, object, new DirtyOperationContext());
	}

	public void create(String alias, Object object, DirtyOperationContext context) throws CompassException {
		if (context.alreadyPerformedOperation(object)) {
			return;
		}
		if (compass.getEventManager().onPreCreate(alias, object) == FilterOperation.YES) {
			return;
		}
		Resource resource = marshallingStrategy.marshall(alias, object);
		if (resource != null) {
			if (compass.getEventManager().onPreCreate(resource) == FilterOperation.YES) {
				return;
			}
			searchEngine.create(resource);
			ResourceKey key = ((InternalResource) resource).getResourceKey();
			firstLevelCache.set(key, object);
			firstLevelCache.setResource(key, resource);
		}
		context.addOperatedObjects(object);
		boolean performedCascading = cascadingManager
				.cascade(alias, object, Cascade.CREATE, context);
		if (resource == null && !performedCascading) {
			throw new MarshallingException("Alias [" + alias
					+ "] has no root mappings and no cascading defined, no operation was perfomed");
		}
		if (resource != null) {
			compass.getEventManager().onPostCreate(resource);
		}
		compass.getEventManager().onPostCreate(alias, object);
	}

	@Override
	public void create(Object object) {
		checkClosed();
		create(object, new DirtyOperationContext());
	}

	public void create(Object object, DirtyOperationContext context) {
		if (context.alreadyPerformedOperation(object)) {
			return;
		}
		if (compass.getEventManager().onPreCreate(null, object) == FilterOperation.YES) {
			return;
		}
		boolean performedCascading;
		Resource resource = marshallingStrategy.marshall(object);
		if (resource != null) {
			if (compass.getEventManager().onPreCreate(resource) == FilterOperation.YES) {
				return;
			}
			searchEngine.create(resource);
			ResourceKey key = ((InternalResource) resource).getResourceKey();
			firstLevelCache.set(key, object);
			firstLevelCache.setResource(key, resource);
			context.addOperatedObjects(object);
			// if we found a resource, we perform the cascading based on its
			// alias
			performedCascading = cascadingManager
					.cascade(key.getAlias(), object, Cascade.CREATE, context);
		} else {
			context.addOperatedObjects(object);
			// actuall, no root mapping to create a resource, try and create one
			// based on the object
			performedCascading = cascadingManager.cascade(object, Cascade.CREATE, context);
		}
		if (resource == null && !performedCascading) {
			throw new MarshallingException("Object [" + object.getClass().getName()
					+ "] has no root mappings and no cascading defined, no operation was perfomed");
		}
		if (resource != null) {
			compass.getEventManager().onPostCreate(resource);
			compass.getEventManager().onPostCreate(resource.getAlias(), object);
		} else {
			compass.getEventManager().onPostCreate(null, object);
		}
	}

	public void save(String alias, Object object) throws CompassException {
		checkClosed();
		save(alias, object, new DirtyOperationContext());
	}

	public void save(String alias, Object object, DirtyOperationContext context) throws CompassException {
		if (context.alreadyPerformedOperation(object)) {
			return;
		}
		if (compass.getEventManager().onPreSave(alias, object) == FilterOperation.YES) {
			return;
		}
		Resource resource = marshallingStrategy.marshall(alias, object);
		if (resource != null) {
			if (compass.getEventManager().onPreSave(resource) == FilterOperation.YES) {
				return;
			}
			searchEngine.save(resource);
			ResourceKey key = ((InternalResource) resource).getResourceKey();
			firstLevelCache.set(key, object);
			firstLevelCache.setResource(key, resource);
		}
		context.addOperatedObjects(object);
		boolean performedCascading = cascadingManager.cascade(alias, object, Cascade.SAVE, context);
		if (resource == null && !performedCascading) {
			throw new MarshallingException("Alias [" + alias
					+ "] has no root mappings and no cascading defined, no operation was perfomed");
		}
		if (resource != null) {
			compass.getEventManager().onPostSave(resource);
		}
		compass.getEventManager().onPostSave(alias, object);
	}

	public void save(Object object) throws CompassException {
		checkClosed();
		save(object, new DirtyOperationContext());
	}

	public void save(Object object, DirtyOperationContext context) throws CompassException {
		if (context.alreadyPerformedOperation(object)) {
			return;
		}
		if (compass.getEventManager().onPreSave(null, object) == FilterOperation.YES) {
			return;
		}
		boolean performedCascading;
		Resource resource = marshallingStrategy.marshall(object);
		if (resource != null) {
			if (compass.getEventManager().onPreSave(resource) == FilterOperation.YES) {
				return;
			}
			searchEngine.save(resource);
			ResourceKey key = ((InternalResource) resource).getResourceKey();
			firstLevelCache.setResource(key, resource);
			firstLevelCache.set(key, object);
			context.addOperatedObjects(object);
			performedCascading = cascadingManager
					.cascade(key.getAlias(), object, Cascade.SAVE, context);
		} else {
			context.addOperatedObjects(object);
			performedCascading = cascadingManager.cascade(object, Cascade.SAVE, context);
		}
		if (resource == null && !performedCascading) {
			throw new MarshallingException("Object [" + object.getClass().getName()
					+ "] has no root mappings and no cascading defined, no operation was perfomed");
		}
		if (resource != null) {
			compass.getEventManager().onPostSave(resource);
			compass.getEventManager().onPostSave(resource.getAlias(), object);
		} else {
			compass.getEventManager().onPostSave(null, object);
		}
	}

	public <T> T get(Class<T> clazz, Object... ids) throws CompassException {
		return get(clazz, (Object) ids);
	}

	public <T> T get(Class<T> clazz, Object id) throws CompassException {
		Resource resource = getResource(clazz, id);
		if (resource == null) {
			return null;
		}
		// noinspection unchecked
		return clazz.cast(getByResource(resource));
	}

	public Object get(String alias, Object... ids) throws CompassException {
		return get(alias, (Object) ids);
	}

	public Object get(String alias, Object id) throws CompassException {
		checkClosed();
		Resource resource = getResource(alias, id);
		if (resource == null) {
			return null;
		}
		return getByResource(resource);
	}

	public Object get(String alias, Object id, MarshallingContext context) throws CompassException {
		checkClosed();
		Resource resource = getResource(alias, id);
		if (resource == null) {
			return null;
		}
		return getByResource(resource, context);
	}

	public Object getByResource(Resource resource) {
		checkClosed();
		return getByResource(resource, null);
	}

	public Object getByResource(Resource resource, MarshallingContext context) {
		checkClosed();
		ResourceKey key = ((InternalResource) resource).getResourceKey();
		Object cachedValue = firstLevelCache.get(key);
		if (cachedValue != null) {
			return cachedValue;
		}
		Object value;
		if (context == null) {
			value = marshallingStrategy.unmarshall(resource);
		} else {
			value = marshallingStrategy.unmarshall(resource, context);
		}
		firstLevelCache.set(key, value);
		return value;
	}

	public void addDelegateClose(InternalSessionDelegateClose delegateClose) {
		this.delegateClose.add(delegateClose);
	}

	public void setReadOnly() {
		searchEngine.setReadOnly();
	}

	public boolean isReadOnly() {
		return searchEngine.isReadOnly();
	}

    public void flush() throws CompassException {
        checkClosed();
        searchEngine.flush();
    }

	@Override
	public InternalCompass getCompass() {
		return compass;
	}

	@Override
	public FirstLevelCache getFirstLevelCache() {
		return firstLevelCache;
	}

	@Override
	public CompassSettings getSettings() {
		return runtimeSettings;
	}

	@Override
	public SearchEngine getSearchEngine() {
		return searchEngine;
	}

	@Override
	public CompassMapping getMapping() {
		return mapping;
	}

	@Override
	public CompassMetaData getMetaData() {
		return compassMetaData;
	}

	public CompassQueryBuilder queryBuilder() throws CompassException {
		checkClosed();
		SearchEngineQueryBuilder searchEngineQueryBuilder = searchEngine.queryBuilder();
		return new DefaultCompassQueryBuilder(searchEngineQueryBuilder, compass, this);
	}

    public CompassQueryFilterBuilder queryFilterBuilder() throws CompassException {
        checkClosed();
        return compass.queryFilterBuilder();
    }
	
	public <T> T load(Class<T> clazz, Object... ids) throws CompassException {
		return load(clazz, (Object) ids);
	}

	public <T> T load(Class<T> clazz, Object id) throws CompassException {
		checkClosed();
		Resource resource = loadResource(clazz, id);
		return clazz.cast(getByResource(resource));
	}

	public Object load(String alias, Object... ids) throws CompassException {
		return load(alias, (Object) ids);
	}

	public Object load(String alias, Object id) throws CompassException {
		checkClosed();
		Resource resource = loadResource(alias, id);
		return getByResource(resource);
	}

	public Resource loadResource(Class<?> clazz, Object... ids) throws CompassException {
		return loadResource(clazz, (Object) ids);
	}

	public Resource loadResource(Class<?> clazz, Object id) throws CompassException {
		checkClosed();
		Resource idResource = marshallingStrategy.marshallIds(clazz, id);
		return loadResourceByIdResource(idResource);
	}

	public Resource loadResource(String alias, Object... ids) throws CompassException {
		return loadResource(alias, (Object) ids);
	}

	public Resource loadResource(String alias, Object id) throws CompassException {
		checkClosed();
		Resource idResource = marshallingStrategy.marshallIds(alias, id);
		return loadResourceByIdResource(idResource);
	}

	public Resource loadResourceByIdResource(Resource idResource) {
		checkClosed();
		ResourceKey key = ((InternalResource) idResource).getResourceKey();
		Resource cachedValue = firstLevelCache.getResource(key);
		if (cachedValue != null) {
			return cachedValue;
		}
		Resource value = searchEngine.load(idResource);
		firstLevelCache.setResource(key, value);
		return value;
	}

	@Override
	public boolean isClosed() {
		return closed;
	}

	@Override
	public void close() {
		if (closed) {
			return;
		}
		for (InternalSessionDelegateClose delegateClose : this.delegateClose) {
			delegateClose.close();
		}
        CompassSession transactionBoundSession = compass.getCompassSessionFactory().getTransactionBoundSession();
        if (transactionBoundSession == this) {
        	compass.getCompassSessionFactory().setTransactionBoundSession(null);
        }
        evictAll();
        closed = true;
	}

	public CompassHits find(String query) throws CompassException {
		checkClosed();
		return queryBuilder().queryString(query).toQuery().hits();
	}

	public Resource getResource(String alias, Object... ids) throws CompassException {
		return getResource(alias, (Object) ids);
	}

	public Resource getResource(Class<?> clazz, Object... ids) throws CompassException {
		return getResource(clazz, (Object) ids);
	}

	public Resource getResource(Class<?> clazz, Object id) throws CompassException {
		checkClosed();
		Resource idResource = marshallingStrategy.marshallIds(clazz, id);
		if (idResource == null) {
			return null;
		}
		return getResourceByIdResource(idResource);
	}

	public Resource getResource(String alias, Object id) throws CompassException {
		checkClosed();
		Resource idResource = marshallingStrategy.marshallIds(alias, id);
		if (idResource == null) {
			return null;
		}
		return getResourceByIdResource(idResource);
	}

	public Resource getResourceByIdResource(Resource idResource) {
		checkClosed();
		ResourceKey key = ((InternalResource) idResource).getResourceKey();
		Resource cachedValue = firstLevelCache.getResource(key);
		if (cachedValue != null) {
			return cachedValue;
		}
		Resource value = searchEngine.get(idResource);
		if (value != null) {
			firstLevelCache.setResource(key, value);
		}
		return value;
	}

	public Resource getResourceByIdResourceNoCache(Resource idResource) {
		checkClosed();
		return searchEngine.get(idResource);
	}

	public void delete(String alias, Object... ids) throws CompassException {
		delete(alias, (Object) ids);
	}

	public void delete(String alias, Object obj) throws CompassException {
		checkClosed();
		delete(alias, obj, new DirtyOperationContext(marshallingStrategy));
	}

	public void delete(String alias, Object obj, DirtyOperationContext context) throws CompassException {
		if (context.alreadyPerformedOperation(obj)) {
			return;
		}
		if (compass.getEventManager().onPreDelete(alias, obj) == FilterOperation.YES) {
			return;
		}
		boolean performedCascading = false;
		Resource idResource = marshallingStrategy.marshallIds(alias, obj);
		if (idResource != null) {
			if (compass.getEventManager().onPreDelete(idResource) == FilterOperation.YES) {
				return;
			}
			Object cascadeObj = null;
			// in case we need to do cascading, we need to load the object to we
			// can delete its cascaded objects
			if (cascadingManager.shouldCascade(idResource.getAlias(), obj, Cascade.DELETE)) {
				Resource resouce = getResourceByIdResource(idResource);
				if (resouce != null) {
					cascadeObj = getByResource(resouce);
				}
			}
			delete(idResource);
			if (cascadeObj != null) {
				context.addOperatedObjects(cascadeObj);
				performedCascading = cascadingManager
						.cascade(idResource.getAlias(), cascadeObj, Cascade.DELETE, context);
			}
		} else {
			context.addOperatedObjects(obj);
			performedCascading = cascadingManager.cascade(alias, obj, Cascade.DELETE, context);
		}
		if (idResource == null && !performedCascading) {
			throw new MarshallingException("Alias [" + alias
					+ "] has no root mappings and no cascading defined, no operation was perfomed");
		}
		if (idResource != null) {
			compass.getEventManager().onPostDelete(idResource);
		}
		compass.getEventManager().onPostDelete(alias, obj);
	}

	public void delete(Class<?> clazz, Object... ids) throws CompassException {
		delete(clazz, (Object) ids);
	}

	public void delete(Class<?> clazz, Object obj) throws CompassException {
		checkClosed();
		delete(clazz, obj, new DirtyOperationContext(marshallingStrategy));
	}

	public void delete(Class<?> clazz, Object obj, DirtyOperationContext context) throws CompassException {
		if (context.alreadyPerformedOperation(obj)) {
			return;
		}
		if (compass.getEventManager().onPreDelete(clazz, obj) == FilterOperation.YES) {
			return;
		}
		boolean performedCascading = false;
		Resource idResource = marshallingStrategy.marshallIds(clazz, obj);
		if (idResource != null) {
			if (compass.getEventManager().onPreDelete(idResource) == FilterOperation.YES) {
				return;
			}
			Object cascadeObj = null;
			// in case we need to do cascading, we need to load the object to we
			// can delete its cascaded objects
			if (cascadingManager.shouldCascade(idResource.getAlias(), obj, Cascade.DELETE)) {
				Resource resouce = getResourceByIdResource(idResource);
				if (resouce != null) {
					cascadeObj = getByResource(resouce);
				}
			}
			delete(idResource);
			if (cascadeObj != null) {
				context.addOperatedObjects(cascadeObj);
				performedCascading = cascadingManager
						.cascade(idResource.getAlias(), cascadeObj, Cascade.DELETE, context);
			}
		} else {
			context.addOperatedObjects(obj);
			performedCascading = cascadingManager.cascade(clazz, obj, Cascade.DELETE, context);
		}
		if (idResource == null && !performedCascading) {
			throw new MarshallingException("Object [" + clazz
					+ "] has no root mappings and no cascading defined, no operation was perfomed");
		}
		if (idResource != null) {
			compass.getEventManager().onPostDelete(idResource);
			compass.getEventManager().onPostDelete(idResource.getAlias(), obj);
		} else {
			compass.getEventManager().onPostDelete(clazz, obj);
		}
	}

	public void delete(Object obj) throws CompassException {
		checkClosed();
		delete(obj, new DirtyOperationContext(marshallingStrategy));
	}

	public void delete(Object obj, DirtyOperationContext context) throws CompassException {
		if (context.alreadyPerformedOperation(obj)) {
			return;
		}
		if (compass.getEventManager().onPreDelete((String) null, obj) == FilterOperation.YES) {
			return;
		}
		boolean performedCascading = false;
		Resource idResource = marshallingStrategy.marshallIds(obj);
		if (idResource != null) {
			if (compass.getEventManager().onPreDelete(idResource) == FilterOperation.YES) {
				return;
			}
			Object cascadeObj = null;
			// in case we need to do cascading, we need to load the object to we
			// can delete its cascaded objects
			if (cascadingManager.shouldCascade(idResource.getAlias(), obj, Cascade.DELETE)) {
				Resource resouce = getResourceByIdResource(idResource);
				if (resouce != null) {
					cascadeObj = getByResource(resouce);
				}
			}
			delete(idResource);
			if (cascadeObj != null) {
				context.addOperatedObjects(cascadeObj);
				performedCascading = cascadingManager
						.cascade(idResource.getAlias(), cascadeObj, Cascade.DELETE, context);
			}
		} else {
			context.addOperatedObjects(obj);
			performedCascading = cascadingManager.cascade(obj, Cascade.DELETE, context);
		}
		if (idResource == null && !performedCascading) {
			throw new MarshallingException("Object [" + obj.getClass().getName()
					+ "] has no root mappings and no cascading defined, no operation was perfomed");
		}
		if (idResource != null) {
			compass.getEventManager().onPostDelete(idResource);
			compass.getEventManager().onPostDelete(idResource.getAlias(), obj);
		} else {
			compass.getEventManager().onPostDelete((String) null, obj);
		}
	}

	public void delete(Resource resource) throws CompassException {
		checkClosed();
		firstLevelCache.evict(((InternalResource) resource).getResourceKey());
		if (compass.getEventManager().onPreDelete(resource) == FilterOperation.YES) {
			return;
		}
		searchEngine.delete(resource);
		compass.getEventManager().onPostDelete(resource);
	}

	public void delete(CompassQuery query) throws CompassException {
		checkClosed();
		query.delete();
	}

	public void evict(Object obj) {
		checkClosed();
		Resource idResource = marshallingStrategy.marshallIds(obj.getClass(), obj);
		ResourceKey key = ((InternalResource) idResource).getResourceKey();
		firstLevelCache.evict(key);
	}

	public void evict(String alias, Object id) {
		checkClosed();
		Resource idResource = marshallingStrategy.marshallIds(alias, id);
		ResourceKey key = ((InternalResource) idResource).getResourceKey();
		firstLevelCache.evict(key);
	}

	public void evict(Resource resource) {
		checkClosed();
		ResourceKey key = ((InternalResource) resource).getResourceKey();
		firstLevelCache.evict(key);
	}

	public void evictAll() {
		checkClosed();
		firstLevelCache.evictAll();
	}

	private void checkClosed() throws IllegalStateException {
		if (closed) {
			throw new IllegalStateException("CompassSession already closed");
		}
	}

	@Override
	public MarshallingStrategy getMarshallingStrategy() {
		return marshallingStrategy;
	}
	
    public CompassAnalyzerHelper analyzerHelper() throws CompassException {
        checkClosed();
        SearchEngineAnalyzerHelper analyzerHelper = searchEngine.analyzerHelper();
        return new DefaultCompassAnalyzerHelper(analyzerHelper, this);
    }

	@Override
	public final void beginTransaction() {
		bindSession();
	}

	@Override
	public void bindSession() {
		CompassSessionFactory sessionFactory = compass.getCompassSessionFactory();
        CompassSession boundSession = sessionFactory.getTransactionBoundSession();
        if (boundSession == null || boundSession != this) {
        	sessionFactory.setTransactionBoundSession(this);
        }
	}

    
}
