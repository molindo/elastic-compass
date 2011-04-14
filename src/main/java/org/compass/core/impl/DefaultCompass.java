package org.compass.core.impl;

import java.lang.ref.WeakReference;

import org.compass.core.Compass;
import org.compass.core.CompassException;
import org.compass.core.CompassIndexSession;
import org.compass.core.CompassQueryBuilder;
import org.compass.core.CompassQueryFilterBuilder;
import org.compass.core.CompassSearchSession;
import org.compass.core.CompassSession;
import org.compass.core.CompassSessionFactory;
import org.compass.core.LocalCompassSessionFactory;
import org.compass.core.ResourceFactory;
import org.compass.core.SessionFactoryFactory;
import org.compass.core.cache.first.FirstLevelCache;
import org.compass.core.cache.first.FirstLevelCacheFactory;
import org.compass.core.config.CompassEnvironment;
import org.compass.core.config.CompassSettings;
import org.compass.core.config.RuntimeCompassSettings;
import org.compass.core.converter.ConverterLookup;
import org.compass.core.engine.SearchEngineFactory;
import org.compass.core.engine.SearchEngineIndexManager;
import org.compass.core.engine.naming.PropertyNamingStrategy;
import org.compass.core.engine.spellcheck.SearchEngineSpellCheckManager;
import org.compass.core.events.CompassEventManager;
import org.compass.core.events.RebuildEventListener;
import org.compass.core.executor.ExecutorManager;
import org.compass.core.mapping.CompassMapping;
import org.compass.core.metadata.CompassMetaData;
import org.compass.core.spi.InternalCompass;
import org.compass.core.spi.InternalCompassSession;

import at.molindo.elastic.compass.ElasticSearchEngineFactory;

public class DefaultCompass implements InternalCompass {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(DefaultCompass.class);

	private final CompassSettings settings;
	private CompassMapping mapping;
	private ConverterLookup converterLookup;
	private CompassMetaData compassMetaData;
	private PropertyNamingStrategy propertyNamingStrategy;
	private ExecutorManager executorManager;
	private String name;
	private boolean duplicate;

	private FirstLevelCacheFactory firstLevelCacheFactory;
	private SearchEngineFactory searchEngineFactory;
	private ShutdownThread shutdownThread;

	private boolean debug;

	private boolean closed;

	private CompassEventManager eventManager;

	private CompassSessionFactory sessionFactory;

	private LocalCompassSessionFactory localSessionFactory;

	public DefaultCompass(CompassMapping mapping, ConverterLookup converterLookup, CompassMetaData compassMetaData, PropertyNamingStrategy propertyNamingStrategy, ExecutorManager executorManager, CompassSettings settings) throws CompassException {
		this(mapping, converterLookup, compassMetaData, propertyNamingStrategy, executorManager, settings, false);
	}

	public DefaultCompass(CompassMapping mapping, ConverterLookup converterLookup, CompassMetaData compassMetaData, PropertyNamingStrategy propertyNamingStrategy, ExecutorManager executorManager, CompassSettings settings, boolean duplicate) {
		this(mapping, converterLookup, compassMetaData, propertyNamingStrategy, executorManager, settings, duplicate, new ElasticSearchEngineFactory(propertyNamingStrategy, settings, mapping, executorManager));
	}
	
	public DefaultCompass(CompassMapping mapping, ConverterLookup converterLookup, CompassMetaData compassMetaData, PropertyNamingStrategy propertyNamingStrategy, ExecutorManager executorManager, CompassSettings settings, boolean duplicate, SearchEngineFactory searchEngineFactory) throws CompassException {

		this.mapping = mapping;
		this.converterLookup = converterLookup;
		this.compassMetaData = compassMetaData;
		this.propertyNamingStrategy = propertyNamingStrategy;
		this.executorManager = executorManager;
		this.name = settings.getSetting(CompassEnvironment.NAME, "default");
		this.settings = settings;
		this.duplicate = duplicate;

        this.eventManager = new CompassEventManager(this, mapping);
        eventManager.configure(settings);
		
        // build the session factory
        sessionFactory = SessionFactoryFactory.createSessionFactory(this, settings);
        localSessionFactory = SessionFactoryFactory.createLocalSessionFactory(this, settings);
        
		firstLevelCacheFactory = new FirstLevelCacheFactory();
		firstLevelCacheFactory.configure(settings);

		this.searchEngineFactory = searchEngineFactory;
		this.searchEngineFactory.getIndexManager().verifyIndex();

		if (!duplicate) {
			start();
		}

		if (settings.getSettingAsBoolean(CompassEnvironment.REGISTER_SHUTDOWN_HOOK, true)) {
			shutdownThread = new ShutdownThread(this);
			if (log.isDebugEnabled()) {
				log.debug("Registering shutdown hook [" + System.identityHashCode(shutdownThread)
						+ "]");
			}
			Runtime.getRuntime().addShutdownHook(shutdownThread);
		}

		this.debug = settings.getSettingAsBoolean(CompassEnvironment.DEBUG, false);
	}

    public SearchEngineSpellCheckManager getSpellCheckManager() {
        return searchEngineFactory.getSpellCheckManager();
    }
	
	@Override
	public CompassSettings getSettings() {
		return settings;
	}

	@Override
	public CompassMapping getMapping() {
		return mapping;
	}

	@Override
	public SearchEngineFactory getSearchEngineFactory() {
		return searchEngineFactory;
	}

	@Override
	public PropertyNamingStrategy getPropertyNamingStrategy() {
		return propertyNamingStrategy;
	}

	@Override
	public SearchEngineIndexManager getSearchEngineIndexManager() {
		return searchEngineFactory.getIndexManager();
	}

	private void checkClosed() throws IllegalStateException {
		if (closed) {
			throw new IllegalStateException("Compass already closed");
		}
	}

    public CompassSearchSession openSearchSession() {
        CompassSession session = openSession();
        session.setReadOnly();
        return session;
    }
	
    public CompassIndexSession openIndexSession() {
        CompassSession session = openSession();
        return session;
    }
    
	public CompassSession openSession() {
		return openSession(true);
	}

	public CompassSession openSession(boolean allowCreate) {
		return openSession(allowCreate, true);
	}

	public CompassSession openSession(boolean allowCreate, boolean checkClosed) {
		if (checkClosed) {
			checkClosed();
		}
		CompassSession session = sessionFactory.getTransactionBoundSession();

		if (session != null) {
			return new ExistingCompassSession((InternalCompassSession) session);
		}

		if (!allowCreate) {
			return null;
		}

		FirstLevelCache firstLevelCache = firstLevelCacheFactory.createFirstLevelCache();
		RuntimeCompassSettings runtimeSettings = new RuntimeCompassSettings(getSettings());
		return new DefaultCompassSession(runtimeSettings, this, searchEngineFactory.openSearchEngine(runtimeSettings), firstLevelCache);
	}

    public CompassQueryBuilder queryBuilder() throws CompassException {
        return new DefaultCompassQueryBuilder(searchEngineFactory.queryBuilder(), this);
    }

    public CompassQueryFilterBuilder queryFilterBuilder() throws CompassException {
        return new DefaultCompassQueryFilterBuilder(searchEngineFactory.queryFilterBuilder(), this);
    }
	
	@Override
	public CompassSessionFactory getCompassSessionFactory() {
		return sessionFactory;
	}

	@Override
	public LocalCompassSessionFactory getLocalCompassSessionFactory() {
		return localSessionFactory;
	}

	@Override
	public ResourceFactory getResourceFactory() {
		return getSearchEngineFactory().getResourceFactory();
	}

    public CompassMetaData getMetaData() {
        return compassMetaData;
    }

    public ConverterLookup getConverterLookup() {
        return converterLookup;
    }
	
	public void clearShutdownHook() {
		shutdownThread = null;
	}

	public void start() {
		searchEngineFactory.start();
	}

	public void stop() {
		searchEngineFactory.stop();
	}

	public void close() {
		if (closed) {
			return;
		}
		closed = true;

		log.info("Closing Compass [" + name + "]");

		if (shutdownThread != null) {
			if (log.isDebugEnabled()) {
				log.debug("Removing shutdown hook");
			}
			try {
				Runtime.getRuntime().removeShutdownHook(shutdownThread);
			} catch (IllegalStateException e) {
				// ignore, we are shuttng down
			}
		}

		searchEngineFactory.close();

		if (!duplicate) {
			executorManager.close();
		}

		log.info("Closed Compass [" + name + "]");
	}

	public boolean isClosed() {
		return this.closed;
	}

	@Override
	public void addRebuildEventListener(RebuildEventListener rebuildEventListener) {
		throw new UnsupportedOperationException();
	}

    public Compass clone(CompassSettings addedSettings) {
        CompassSettings copySettings = settings.copy();
        copySettings.addSettings(addedSettings);
        return new DefaultCompass(mapping, converterLookup, compassMetaData, propertyNamingStrategy, executorManager, copySettings, true);
    }

	@Override
	public CompassEventManager getEventManager() {
		return eventManager;
	}



	/**
	 * A shutdown hook that closes Compass.
	 */
	private static class ShutdownThread extends Thread {

		private final WeakReference<Compass> compass;

		public ShutdownThread(Compass compass) {
			this.compass = new WeakReference<Compass>(compass);
		}

		@Override
		public void run() {
			Compass compass = this.compass.get();
			if (compass != null) {
				((DefaultCompass) compass).clearShutdownHook();
				if (!compass.isClosed()) {
					compass.close();
				}
			}
		}
	}



}
