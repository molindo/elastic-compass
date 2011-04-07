/**
 * Copyright 2011 Molindo GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.molindo.elastic.compass;

import org.compass.core.ResourceFactory;
import org.compass.core.config.CompassEnvironment;
import org.compass.core.config.CompassSettings;
import org.compass.core.config.RuntimeCompassSettings;
import org.compass.core.engine.SearchEngine;
import org.compass.core.engine.SearchEngineFactory;
import org.compass.core.engine.SearchEngineIndexManager;
import org.compass.core.engine.naming.PropertyNamingStrategy;
import org.compass.core.engine.spi.InternalSearchEngineFactory;
import org.compass.core.executor.ExecutorManager;
import org.compass.core.mapping.CompassMapping;

/**
 * created once per DefaultCompass
 */
public class ElasticSearchEngineFactory implements InternalSearchEngineFactory {

	private static final String ELASTIC_NODE_KEY = ElasticNode.class.getName();
	
	private final PropertyNamingStrategy _propertyNamingStrategy;
	private final CompassSettings _settings;
	private final CompassMapping _mapping;
	private final ExecutorManager _executorManager;
	private final SearchEngineIndexManager _indexManager;
	private final boolean _debug;
	private final ResourceFactory _resourceFactory;
	private final ElasticNode _node;

	public ElasticSearchEngineFactory(PropertyNamingStrategy propertyNamingStrategy, CompassSettings settings, CompassMapping mapping, ExecutorManager executorManager) {
		_propertyNamingStrategy = propertyNamingStrategy;
		_settings = settings;
		_mapping = mapping;
		_executorManager = executorManager;
		_indexManager = new ElasticSearchEngineIndexManager(new DefaultElasticSearchEngineStore(this, mapping));
		_resourceFactory = new ElasticResourceFactory(this);
		
		_debug = settings.getSettingAsBoolean(CompassEnvironment.DEBUG, false);
		
		ElasticNode node;
		synchronized (ELASTIC_NODE_KEY) {
			node = (ElasticNode) settings.getRegistry(ELASTIC_NODE_KEY);
			if (node == null) {
				node = new ElasticNode(this);
				node.configure(settings);
				node.start();
				settings.setRegistry(ELASTIC_NODE_KEY, node);
				// FIXME and who stops me?
			}
		}
		_node = node;

	}

	@Override
	public CompassMapping getMapping() {
		return _mapping;
	}

	@Override
	public SearchEngineIndexManager getIndexManager() {
		return _indexManager;
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}

	@Override
	public void close() {
	}

	@Override
	public boolean isDebug() {
		return _debug;
	}

	@Override
	public ResourceFactory getResourceFactory() {
		return _resourceFactory ;
	}

	@Override
	public SearchEngine openSearchEngine(RuntimeCompassSettings runtimeSettings) {
		return new ElasticSearchEngine(runtimeSettings, this);
	}

	public ElasticClient openElasticClient() {
		return _node.client();
	}

}
