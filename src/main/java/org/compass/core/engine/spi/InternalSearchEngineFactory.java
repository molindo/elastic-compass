package org.compass.core.engine.spi;

import org.compass.core.engine.SearchEngineFactory;

/**
 * @author kimchy
 */
public interface InternalSearchEngineFactory extends SearchEngineFactory {

    boolean isDebug();
}
