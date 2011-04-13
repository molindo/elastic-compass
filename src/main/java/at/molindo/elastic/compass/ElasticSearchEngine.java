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

import java.util.Arrays;

import org.compass.core.Resource;
import org.compass.core.config.RuntimeCompassSettings;
import org.compass.core.engine.SearchEngine;
import org.compass.core.engine.SearchEngineException;
import org.compass.core.engine.SearchEngineHits;
import org.compass.core.engine.SearchEngineInternalSearch;
import org.compass.core.engine.SearchEngineQuery;
import org.compass.core.engine.SearchEngineQueryBuilder;
import org.compass.core.mapping.ResourceMapping;
import org.compass.core.spi.InternalResource;
import org.compass.core.spi.MultiResource;
import org.compass.core.spi.ResourceKey;
import org.compass.core.util.StringUtils;

import at.molindo.utils.collections.ArrayUtils;

import com.sun.org.apache.regexp.internal.recompile;

public class ElasticSearchEngine implements SearchEngine {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(ElasticSearchEngine.class);

	private final RuntimeCompassSettings _runtimeSettings;
	private final ElasticSearchEngineFactory _searchEngineFactory;
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

    public Resource load(Resource idResource) throws SearchEngineException {
        Resource resource = get(idResource);
        if (resource == null) {
            throw new SearchEngineException("Failed to find resource with alias [" + idResource.getAlias() + "] and ids ["
                    + StringUtils.arrayToCommaDelimitedString(idResource.getIds()) + "]");
        }
        return resource;
    }
    
	public void create(Resource resource) throws SearchEngineException {
		createOrUpdate(resource, false);
	}

    public void save(Resource resource) throws SearchEngineException {
        createOrUpdate(resource, true);
    }
    
	private void createOrUpdate(final Resource resource, boolean update) throws SearchEngineException {
		verifyNotReadOnly();
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

    public void delete(Resource resource) throws SearchEngineException {
        verifyNotReadOnly();
        if (resource instanceof MultiResource) {
            MultiResource multiResource = (MultiResource) resource;
            for (int i = 0; i < multiResource.size(); i++) {
                delete(((InternalResource) multiResource.resource(i)).getResourceKey());
            }
        } else {
            delete(((InternalResource) resource).getResourceKey());
        }
    }

    private void delete(ResourceKey resourceKey) throws SearchEngineException {
        if (resourceKey.getIds().length == 0) {
            throw new SearchEngineException("Cannot delete a resource with no ids and alias [" + resourceKey.getAlias() + "]");
        }
        doDelete(resourceKey);
        String[] extendingAliases = resourceKey.getResourceMapping().getExtendingAliases();
        for (String extendingAlias : extendingAliases) {
            ResourceMapping extendingMapping = getSearchEngineFactory().getMapping().getMappingByAlias(extendingAlias);
            ResourceKey key = new ResourceKey(extendingMapping, resourceKey.getIds());
            doDelete(key);
        }
        if (log.isTraceEnabled()) {
            log.trace("RESOURCE DELETE {" + resourceKey.getAlias() + "} " + StringUtils.arrayToCommaDelimitedString(resourceKey.getIds()));
        }
    }
    
	@Override
	public SearchEngineQueryBuilder queryBuilder() {
		return _searchEngineFactory.queryBuilder();
	}

	public SearchEngineHits find(SearchEngineQuery searchEngineQuery) {
		return doFind(searchEngineQuery);
	}

	@Override
	public void delete(SearchEngineQuery searchEngineQuery) {
		doDelete(searchEngineQuery);
	}

	@Override
	public void flush() {
		// TODO implement bulk api
	}

	protected Resource[] doGet(ResourceKey key) {
		return _client.get(key);
	}

	protected void doCreate(InternalResource resource) {
		_client.create((ElasticResource) resource);
	}

	protected void doUpdate(InternalResource resource) {
		_client.update((ElasticResource) resource);
	}

	protected void doDelete(ResourceKey key) {
		_client.delete(key);
	}
	
	protected SearchEngineHits doFind(SearchEngineQuery searchEngineQuery) {
		return _client.find((ElasticSearchEngineQuery) searchEngineQuery);
	}

	protected void doDelete(SearchEngineQuery searchEngineQuery) {
		_client.delete((ElasticSearchEngineQuery) searchEngineQuery);
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

	@Override
	public SearchEngineInternalSearch internalSearch(String[] subIndexes, String[] aliases) throws SearchEngineException {
		if (!ArrayUtils.empty(subIndexes)) {
			log.warn("sub indexes not supported, ignoring " + Arrays.toString(aliases));
		}
		return new ElasticSearchEngineInternalSearch(_client, aliases);
	}

	
}
