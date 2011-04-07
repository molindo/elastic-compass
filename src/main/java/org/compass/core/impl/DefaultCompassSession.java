package org.compass.core.impl;

import org.compass.core.CompassException;
import org.compass.core.CompassHits;
import org.compass.core.Resource;
import org.compass.core.cache.first.FirstLevelCache;
import org.compass.core.cascade.CascadingManager;
import org.compass.core.config.CompassSettings;
import org.compass.core.config.RuntimeCompassSettings;
import org.compass.core.engine.SearchEngine;
import org.compass.core.events.FilterOperation;
import org.compass.core.mapping.Cascade;
import org.compass.core.mapping.CompassMapping;
import org.compass.core.marshall.DefaultMarshallingStrategy;
import org.compass.core.marshall.MarshallingContext;
import org.compass.core.marshall.MarshallingException;
import org.compass.core.metadata.CompassMetaData;
import org.compass.core.spi.DirtyOperationContext;
import org.compass.core.spi.InternalCompass;
import org.compass.core.spi.InternalCompassSession;
import org.compass.core.spi.InternalResource;
import org.compass.core.spi.ResourceKey;

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

	public DefaultCompassSession(RuntimeCompassSettings runtimeSettings, InternalCompass compass, SearchEngine searchEngine,
            FirstLevelCache firstLevelCache) {
    	
        this.compass = compass;
        this.mapping = compass.getMapping();
        this.compassMetaData = compass.getMetaData();
        this.runtimeSettings = runtimeSettings;
        this.searchEngine = searchEngine;
        this.firstLevelCache = firstLevelCache;
        this.marshallingStrategy = new DefaultMarshallingStrategy(mapping, searchEngine, compass.getConverterLookup(), this);
        this.cascadingManager = new CascadingManager(this);
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
            // if we found a resource, we perform the cascading based on its alias
            performedCascading = cascadingManager.cascade(key.getAlias(), object, Cascade.CREATE, context);
        } else {
            context.addOperatedObjects(object);
            // actuall, no root mapping to create a resource, try and create one based on the object
            performedCascading = cascadingManager.cascade(object, Cascade.CREATE, context);
        }
        if (resource == null && !performedCascading) {
            throw new MarshallingException("Object [" + object.getClass().getName() + "] has no root mappings and no cascading defined, no operation was perfomed");
        }
        if (resource != null) {
            compass.getEventManager().onPostCreate(resource);
            compass.getEventManager().onPostCreate(resource.getAlias(), object);
        } else {
            compass.getEventManager().onPostCreate(null, object);
        }
	}

	@Override
	public void delete(Object value, DirtyOperationContext context) {
	}

	@Override
	public void save(Object value, DirtyOperationContext context) {
	}

    public <T> T get(Class<T> clazz, Object... ids) throws CompassException {
        return get(clazz, (Object) ids);
    }

    public <T> T get(Class<T> clazz, Object id) throws CompassException {
        Resource resource = getResource(clazz, id);
        if (resource == null) {
            return null;
        }
        //noinspection unchecked
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
	
	@Override
	public void evictAll() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReadOnly() {
		// TODO Auto-generated method stub

	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub

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

	@Override
	public boolean isReadOnly() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isClosed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> T load(Class<T> clazz, Object id) throws CompassException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object load(String alias, Object id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public void create(String alias, Resource obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void create(String alias, Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Resource resource) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public CompassHits find(String query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Class<?> clazz, Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(String alias, Object obj) {
		// TODO Auto-generated method stub

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
    
	@Override
	public Resource loadResource(Class<?> clazz, Object id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource loadResource(String alias, Object id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void save(String alias, Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void evict(Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void evict(String alias, Object id) {
		// TODO Auto-generated method stub

	}
	
    private void checkClosed() throws IllegalStateException {
        if (closed) {
            throw new IllegalStateException("CompassSession already closed");
        }
    }

}
