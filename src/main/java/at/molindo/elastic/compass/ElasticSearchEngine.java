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

import org.compass.core.Resource;
import org.compass.core.config.RuntimeCompassSettings;
import org.compass.core.engine.SearchEngine;
import org.compass.core.engine.SearchEngineException;
import org.compass.core.engine.SearchEngineHits;
import org.compass.core.engine.SearchEngineQuery;
import org.compass.core.engine.SearchEngineQueryBuilder;
import org.compass.core.mapping.ResourceMapping;
import org.compass.core.spi.InternalResource;
import org.compass.core.spi.MultiResource;
import org.compass.core.spi.ResourceKey;
import org.compass.core.util.StringUtils;

public class ElasticSearchEngine implements SearchEngine {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(ElasticSearchEngine.class);

	private final RuntimeCompassSettings _runtimeSettings;
	private final ElasticSearchEngineFactory _searchEngineFactory;
	private boolean _onlyReadOnlyOperations;
	private boolean _readOnly;

	private ElasticClient _client;

	public ElasticSearchEngine(RuntimeCompassSettings runtimeSettings, ElasticSearchEngineFactory dummySearchEngineFactory) {
		_runtimeSettings = runtimeSettings;
		_searchEngineFactory = dummySearchEngineFactory;

		_client = dummySearchEngineFactory.openElasticClient();
	}

	@Override
	public ElasticSearchEngineFactory getSearchEngineFactory() {
		return _searchEngineFactory;
	}

	public void create(Resource resource) throws SearchEngineException {
		_onlyReadOnlyOperations = false;
		createOrUpdate(resource, false);
	}

	public Resource get(Resource idResource) throws SearchEngineException {
		ResourceKey resourceKey = ((InternalResource) idResource).getResourceKey();
		if (resourceKey.getIds().length == 0) {
			throw new SearchEngineException("Cannot load a resource with no ids and alias ["
					+ resourceKey.getAlias() + "]");
		}
		Resource[] result = doGet(resourceKey);

		if (result.length == 0) {
			// none directly, try and load polymorphic ones
			String[] extendingAliases = resourceKey.getResourceMapping().getExtendingAliases();
			for (String extendingAlias : extendingAliases) {
				ResourceMapping extendingMapping = getSearchEngineFactory().getMapping()
						.getMappingByAlias(extendingAlias);
				ResourceKey key = new ResourceKey(extendingMapping, resourceKey.getIds());
				result = doGet(key);
				if (result.length > 0) {
					return result[result.length - 1];
				}
			}
		} else if (result.length > 1) {
			log.warn("Found several matches in get/load operation for resource alias ["
					+ resourceKey.getAlias() + "] and ids ["
					+ StringUtils.arrayToCommaDelimitedString(resourceKey.getIds()) + "]");
			return result[result.length - 1];
		}
		// did not find in the extending aliases as well
		return result[0];
	}

	private void createOrUpdate(final Resource resource, boolean update) throws SearchEngineException {
		verifyNotReadOnly();
		_onlyReadOnlyOperations = false;
		String alias = resource.getAlias();
		ResourceMapping resourceMapping = getSearchEngineFactory().getMapping()
				.getRootMappingByAlias(alias);
		if (resourceMapping == null) {
			throw new SearchEngineException("Failed to find mapping for alias [" + alias + "]");
		}
		if (resource instanceof MultiResource) {
			MultiResource multiResource = (MultiResource) resource;
			for (int i = 0; i < multiResource.size(); i++) {
				InternalResource resource1 = (InternalResource) multiResource.resource(i);
				if (update) {
					doUpdate(resource1);
					if (log.isTraceEnabled()) {
						log.trace("RESOURCE SAVE " + resource1);
					}
				} else {
					doCreate(resource1);
					if (log.isTraceEnabled()) {
						log.trace("RESOURCE CREATE " + resource1);
					}
				}
			}
		} else {
			InternalResource resource1 = (InternalResource) resource;
			if (update) {
				doUpdate(resource1);
				if (log.isTraceEnabled()) {
					log.trace("RESOURCE SAVE " + resource1);
				}
			} else {
				doCreate(resource1);
				if (log.isTraceEnabled()) {
					log.trace("RESOURCE CREATE " + resource1);
				}
			}
		}
	}

	@Override
	public SearchEngineQueryBuilder queryBuilder() {
		return _searchEngineFactory.queryBuilder();
	}

	public SearchEngineHits find(SearchEngineQuery searchEngineQuery) {
		return _client.find((ElasticSearchEngineQuery) searchEngineQuery);
	}

	@Override
	public void delete(SearchEngineQuery searchEngineQuery) {
		// TODO Auto-generated method stub
	}

	private Resource[] doGet(ResourceKey key) {
		return _client.get(key);
	}

	private void doCreate(InternalResource resource) {
		_client.create((ElasticResource) resource);
	}

	private void doUpdate(InternalResource resource) {
		_client.update((ElasticResource) resource);
	}

	private void doFind(SearchEngineQuery query) {
		_client.find((ElasticSearchEngineQuery) query);
	}

	@Override
	public void setReadOnly() {
		_readOnly = true;
	}

	@Override
	public boolean isReadOnly() {
		return _readOnly;
	}

	public void verifyNotReadOnly() throws SearchEngineException {
		if (_readOnly) {
			throw new SearchEngineException("Transaction is set as read only");
		}
	}

}
