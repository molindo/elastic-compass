package org.compass.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.compass.core.Compass;
import org.compass.core.CompassException;
import org.compass.core.CompassIndexSession;
import org.compass.core.CompassQueryBuilder;
import org.compass.core.CompassQueryFilterBuilder;
import org.compass.core.CompassSearchSession;
import org.compass.core.CompassSession;
import org.compass.core.CompassSessionFactory;
import org.compass.core.ResourceFactory;
import org.compass.core.config.CompassConfiguration;
import org.compass.core.config.CompassEnvironment;
import org.compass.core.config.CompassSettings;
import org.compass.core.converter.ConverterLookup;
import org.compass.core.engine.SearchEngineFactory;
import org.compass.core.engine.SearchEngineIndexManager;
import org.compass.core.engine.naming.PropertyNamingStrategy;
import org.compass.core.engine.spellcheck.SearchEngineSpellCheckManager;
import org.compass.core.events.CompassEventManager;
import org.compass.core.events.RebuildEventListener;
import org.compass.core.mapping.CompassMapping;
import org.compass.core.metadata.CompassMetaData;
import org.compass.core.spi.InternalCompass;

/**
 * A wrapper around an actual implemenation of {@link Compass} that allows to
 * rebuild it after changes that are perfomed on the {@link #getConfig()}
 * configuration.
 * 
 * @author kimchy
 */
public class RefreshableCompass implements InternalCompass {

	private static final Log logger = LogFactory.getLog(RefreshableCompass.class);

	private final CompassConfiguration config;

	private volatile InternalCompass compass;

	private List<RebuildEventListener> rebuildEventListeners = new ArrayList<RebuildEventListener>();

	public RefreshableCompass(CompassConfiguration config, InternalCompass compass) {
		this.config = config;
		this.compass = compass;
	}

	public CompassConfiguration getConfig() {
		return config;
	}

	public synchronized void rebuild() throws CompassException {
		compass.stop();
		config.getSettings().addSettings(compass.getSettings());
		InternalCompass rebuiltCompass;
		try {
			rebuiltCompass = (InternalCompass) config.buildCompass();
		} catch (RuntimeException e) {
			compass.start();
			throw e;
		}
		// do the switch
		Compass oldCompass = compass;
		compass = rebuiltCompass;

		oldCompass.getSearchEngineIndexManager().clearCache();

		long sleepBeforeClose = getConfig()
				.getSettings()
				.getSettingAsTimeInMillis(CompassEnvironment.Rebuild.SLEEP_BEFORE_CLOSE, CompassEnvironment.Rebuild.DEFAULT_SLEEP_BEFORE_CLOSE); // schedule
																																					// Compass
																																					// to
																																					// be
																																					// closed
		if (sleepBeforeClose <= 0) {
			oldCompass.close();
		} else {
			Thread t = new Thread(new CloseCompassRunnable(oldCompass, sleepBeforeClose), "Close Compass");
			t.start();
		}

		for (RebuildEventListener eventListener : rebuildEventListeners) {
			eventListener.onCompassRebuild(compass);
		}
	}

	public Compass clone(CompassSettings addedSettings) {
		InternalCompass clonedCompass = (InternalCompass) compass.clone(addedSettings);
		return new RefreshableCompass(config, clonedCompass);
	}

	public synchronized void addRebuildEventListener(RebuildEventListener eventListener) {
		rebuildEventListeners.add(eventListener);
	}

	public synchronized void removeRebuildEventListener(RebuildEventListener eventListener) {
		rebuildEventListeners.remove(eventListener);
	}

	// Delegate Methods

	public void start() {
		compass.start();
	}

	public void stop() {
		compass.stop();
	}

	public CompassSession openSession() throws CompassException {
		return compass.openSession();
	}

	@Override
	public CompassSearchSession openSearchSession() {
		return compass.openSearchSession();
	}
	
    @Override
	public CompassIndexSession openIndexSession() {
		return compass.openIndexSession();
	}

	public CompassQueryBuilder queryBuilder() throws CompassException {
        return compass.queryBuilder();
    }

    public CompassQueryFilterBuilder queryFilterBuilder() throws CompassException {
        return compass.queryFilterBuilder();
    }
	
	public CompassSettings getSettings() {
		return compass.getSettings();
	}

	@Override
	public SearchEngineSpellCheckManager getSpellCheckManager() {
		return compass.getSpellCheckManager();
	}

	public CompassMapping getMapping() {
		return compass.getMapping();
	}

	@Override
	public CompassSessionFactory getCompassSessionFactory() {
		return compass.getCompassSessionFactory();
	}

	@Override
	public CompassSessionFactory getLocalCompassSessionFactory() {
		return compass.getLocalCompassSessionFactory();
	}

	public SearchEngineFactory getSearchEngineFactory() {
		return compass.getSearchEngineFactory();
	}

	public PropertyNamingStrategy getPropertyNamingStrategy() {
		return compass.getPropertyNamingStrategy();
	}

	@Override
	public CompassEventManager getEventManager() {
		return compass.getEventManager();
	}

	public void close() throws CompassException {
		compass.close();
	}

	public ResourceFactory getResourceFactory() {
		return compass.getResourceFactory();
	}

	public CompassMetaData getMetaData() {
		return compass.getMetaData();
	}

	public ConverterLookup getConverterLookup() {
		return compass.getConverterLookup();
	}

	public SearchEngineIndexManager getSearchEngineIndexManager() {
		return compass.getSearchEngineIndexManager();
	}

	public boolean isClosed() {
		return compass.isClosed();
	}

	private class CloseCompassRunnable implements Runnable {

		private final Compass compass;

		private final long timeout;

		private CloseCompassRunnable(Compass compass, long timeout) {
			this.compass = compass;
			this.timeout = timeout;
		}

		public void run() {
			try {
				Thread.sleep(timeout);
			} catch (InterruptedException e) {
				// do nothing
			}
			try {
				compass.close();
			} catch (Exception e) {
				logger.error("Failed to close original Compass after rebuild", e);
			}
		}
	}

}
