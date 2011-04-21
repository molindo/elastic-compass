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

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import org.compass.core.CompassException;
import org.compass.core.config.CompassConfigurable;
import org.compass.core.config.CompassSettings;
import org.compass.core.engine.SearchEngineException;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;

public class ElasticNode implements CompassConfigurable {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(ElasticNode.class);

	private static final String ELASTIC_NODE_KEY = ElasticNode.class.getName();

	private Node _node;
	private ElasticSearchEngineFactory _searchEngineFactory;

	private ElasticSettings _settings;

	private ElasticIndex _index;

	public ElasticNode(ElasticSearchEngineFactory searchEngineFactory) {
		if (searchEngineFactory == null) {
			throw new NullPointerException("searchEngineFactory");
		}
		_searchEngineFactory = searchEngineFactory;
	}

	@Override
	public void configure(CompassSettings settings) throws CompassException {
		_settings = new ElasticSettings(settings);

		synchronized (ELASTIC_NODE_KEY) {
			_node = (Node) settings.getRegistry(ELASTIC_NODE_KEY);
			if (_node == null) {
				// @formatter:off
				Settings elasticSettings = ImmutableSettings.settingsBuilder()
						.put("cluster.name", _settings.getClusterName())
						.build();
				// @formatter:on

				_node = nodeBuilder().client(!_settings.getLocal()).local(_settings.getLocal())
						.settings(elasticSettings).node();
				
				settings.setRegistry(ELASTIC_NODE_KEY, _node);
				// FIXME and who stops me?

				// wait for cluster to become ready if necessary
				String nodeCount = _settings.getLocal() ? "1" : ">1";
				log.info("waiting for " + nodeCount + " nodes");
				_node.client().admin().cluster().prepareHealth().setWaitForNodes(nodeCount)
						.execute().actionGet();
			}
		}

		_index = new ElasticIndex(_settings, _node.client(), _searchEngineFactory.getMapping());
	}

	public ElasticClient client() {
		if (_node == null) {
			throw new SearchEngineException("ElasticNode not started");
		}
		log.debug("creating new ElasticClient for ElasticNode");
		return new ElasticClient(_searchEngineFactory, _index, _node.client());
	}

	public void start() {
		// noop
	}

	public void stop() {
		// noop
	}

	public void close() {
		if (_node != null) {
			_node.stop();
			_node = null;
		}
	}

	public ElasticSettings getSettings() {
		if (_settings == null) {
			throw new IllegalStateException("ElasticNode not yet configured");
		}
		return _settings;
	}

	public void replaceWith(ElasticNode node) {
		String alias = _index.getAlias();

		_index.deleteIndex();

		node._index.addAlias(alias);

		_index = new ElasticIndex(_settings, _node.client(), _searchEngineFactory.getMapping());
	}
}
