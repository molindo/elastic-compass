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
import org.elasticsearch.common.collect.IdentityHashSet;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.node.Node;

public class ElasticNode implements CompassConfigurable {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(ElasticNode.class);

	private static final String ELASTIC_NODE_KEY = ElasticNode.class.getName();

	private NodeHolder _nodeHolder;
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
			_nodeHolder = (NodeHolder) settings.getRegistry(ELASTIC_NODE_KEY);
			if (_nodeHolder == null) {
				boolean local = _settings.getLocal();
				
				ImmutableSettings.Builder settingsBuilder = ImmutableSettings.settingsBuilder()
						.put("cluster.name", _settings.getClusterName());
				
				if (local) {
					settingsBuilder.put("index.store.type", "ram");
				}
				
				Node node = nodeBuilder().local(local).client(!local).data(local)
						.settings(settingsBuilder.build()).node();
				
				_nodeHolder = new NodeHolder(node, this);
				
				settings.setRegistry(ELASTIC_NODE_KEY, _nodeHolder);

				// wait for cluster to become ready if necessary
				String nodeCount = _settings.getLocal() ? "1" : ">1";
				log.info("waiting for " + nodeCount + " nodes");
				node.client().admin().cluster().prepareHealth().setWaitForNodes(nodeCount)
						.execute().actionGet();
			} else {
				_nodeHolder.add(this);
			}
		}

		_index = new ElasticIndex(_settings, getNode().client(), _searchEngineFactory.getMapping());
	}

	public ElasticClient client() {
		log.debug("creating new ElasticClient for ElasticNode");
		return new ElasticClient(_searchEngineFactory, _index, getNode().client());
	}

	public void start() {
		// noop
	}

	public void stop() {
		// noop
	}

	public void close() {
		if (_nodeHolder != null) {
			_nodeHolder.remove(this);
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

		_index = new ElasticIndex(_settings, getNode().client(), _searchEngineFactory.getMapping());
	}

	private Node getNode() {
		if (_nodeHolder == null) {
			throw new SearchEngineException("ElasticNode not started");
		}
		return _nodeHolder.getNode();
	}
	
	private static final class NodeHolder {
		private final Node _node;
		final IdentityHashSet<ElasticNode> _elasticNodes = new IdentityHashSet<ElasticNode>();
		
		public NodeHolder(Node node, ElasticNode elasticNode) {
			if (node == null) {
				throw new NullPointerException("node");
			}
			if (elasticNode == null) {
				throw new NullPointerException("elasticNode");
			}
			_node = node;
			_elasticNodes.add(elasticNode);
		}
		
		public Node getNode() {
			return _node;
		}

		public boolean add(ElasticNode elasticNode) {
			if (_elasticNodes.size() == 0) {
				throw new IllegalStateException("node already closed");
			}
			return _elasticNodes.add(elasticNode);
		}
		
		public boolean remove(ElasticNode elasticNode) {
			boolean removed = _elasticNodes.remove(elasticNode);
			if (_elasticNodes.size() == 0) {
				_node.close();
			}
			return removed;
		}
		
	}
}
