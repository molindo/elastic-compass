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

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.compass.core.Property.Index;
import org.compass.core.Property.Store;
import org.compass.core.Property.TermVector;
import org.compass.core.engine.SearchEngineException;
import org.compass.core.mapping.AllMapping;
import org.compass.core.mapping.BoostPropertyMapping;
import org.compass.core.mapping.CompassMapping;
import org.compass.core.mapping.ExcludeFromAll;
import org.compass.core.mapping.ResourceMapping;
import org.compass.core.mapping.ResourcePropertyMapping;
import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.status.IndexStatus;
import org.elasticsearch.action.admin.indices.status.IndicesStatusResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.indices.IndexMissingException;

import at.molindo.utils.collections.CollectionUtils;

/**
 * manages an index
 */
public class ElasticIndex {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(ElasticIndex.class);

	private final String _alias;
	private final Client _client;
	private final CompassMapping _mapping;

	private final ElasticSettings _settings;
	private String _index;

	public ElasticIndex(ElasticSettings settings, Client client, CompassMapping mapping) {
		if (settings == null) {
			throw new NullPointerException("settings");
		}
		_settings = settings;
		_alias = _settings.getAliasName();

		if (client == null) {
			throw new NullPointerException("client");
		}
		_client = client;

		if (mapping == null) {
			throw new NullPointerException("mapping");
		}
		_mapping = mapping;
	}

	private void createIndex() {
		_index = generateIndexName();

		// fix dangling aliases
		IndicesAdminClient indicesAdminClient = indicesAdminClient();
		try {
			IndicesStatusResponse response = indicesAdminClient.prepareStatus(_alias).execute()
					.actionGet();
			for (Map.Entry<String, IndexStatus> e : response.getIndices().entrySet()) {
				// check for unknown indexes
				try {
					indicesAdminClient.prepareStatus(e.getKey()).execute().actionGet();
					// index exists - don't delete
					throw new SearchEngineException("can't crate index for alias '" + _alias
							+ "' as it is mapped to '" + e.getKey() + "'");
				} catch (IndexMissingException e1) {
					// alias unknown pointing to unknown index, delete alias
					indicesAdminClient.prepareAliases().removeAlias(e.getKey(), _alias);
				}
			}
		} catch (IndexMissingException e) {
			// alias unknown, that's what we want
		}

		indicesAdminClient.prepareCreate(getIndex()).execute().actionGet();
		indicesAdminClient.prepareAliases().addAlias(getIndex(), _alias).execute().actionGet();

		// push mappings
		for (ResourceMapping mapping : _mapping.getRootMappings()) {

			PutMappingResponse resp = indicesAdminClient().preparePutMapping(getIndex())
					.setType(mapping.getAlias()).setSource(toMappingSource(mapping)).execute()
					.actionGet();

			if (!resp.acknowledged()) {
				throw new SearchEngineException("failed to put mapping for type "
						+ mapping.getAlias());
			}
		}
	}

	public synchronized void deleteIndex() {
		String index = getIndex(false);
		if (index != null) {
			log.info("deleting alias '" + _alias + "' of index '" + index + "'");
			IndicesAdminClient client = indicesAdminClient();
			client.prepareAliases().removeAlias(index, _alias).execute().actionGet();

			ClusterStateResponse state = adminClient().cluster().prepareState().execute()
					.actionGet();
			IndexMetaData indexState = state.getState().getMetaData().getIndices().get(index);
			if (indexState != null) {
				if (indexState.getAliases().size() == 0) {
					log.info("deleting index '" + index + "' without aliases");
					indicesAdminClient().prepareDelete(index).execute().actionGet();
				} else {
					log.info("keeping index '" + index + "' with aliases " + indexState.getAliases());
				}
			}
		}
	}

	public synchronized void verifyIndex() {
		IndicesAdminClient indicesAdminClient = indicesAdminClient();

		log.info("verifying index with alias '" + _alias + "'");

		try {
			IndicesStatusResponse response = indicesAdminClient.prepareStatus(_alias).execute()
					.actionGet();

			Map<String, IndexStatus> indices = response.getIndices();
			if (indices.size() > 1) {
				throw new SearchEngineException("alias name points to more than one index, was '"
						+ _alias + "'");
			}

			_index = CollectionUtils.firstValue(indices).getIndex();
			if (getIndex().equals(_alias)) {
				throw new SearchEngineException("alias name must not point to index, was '"
						+ _alias + "'");
			}
			log.info("index '" + _alias + "' verified successfully");
		} catch (IndexMissingException e) {
			// alias unknown, create new index
			createIndex();
		} catch (ElasticSearchException e) {
			throw new SearchEngineException("failed to verify index '" + _alias + "'", e);
		}
	}

	private AdminClient adminClient() {
		return _client.admin();
	}

	private IndicesAdminClient indicesAdminClient() {
		return adminClient().indices();
	}

	// @formatter:off
	private XContentBuilder toMappingSource(ResourceMapping mapping) {
		try {
			XContentBuilder builder = jsonBuilder().startObject();

			// start alias
			builder.startObject(mapping.getAlias());

			// start properties
			builder.startObject("properties");
			for (ResourcePropertyMapping property : mapping.getResourcePropertyMappings()) {
				// TODO should we really use string only?
				ElasticType type = ElasticType.STRING;
				
				builder
					.startObject(property.getPath().getPath())
						.field("type", type.getName())
						.field("index", index(property.getIndex()))
						.field("store", store(property.getStore()))
						.field("include_in_all", includeInAll(property.getExcludeFromAll()))
						.field("term_vector", termVector(property.getTermVector()))
						.field("boost", property.getBoost())
					.endObject();
			}
			builder.endObject();
			// end properties

			AllMapping allMapping = mapping.getAllMapping();
			
			if (allMapping.isExcludeAlias()) {
				log.warn("excluding _type from _all not supported, type " + mapping.getAlias());
			}
			
			builder
				.startObject("_all")
					.field("enabled", allMapping.isSupported() != Boolean.FALSE)
					.field("term_vector", termVector(allMapping.getTermVector()))
				.endObject();

			BoostPropertyMapping boostMapping = mapping.getBoostPropertyMapping();
			if (boostMapping != null) {
				builder	
					.startObject("_boost")
						.field("name", boostMapping.getPath().getPath())
						.field("null_value", boostMapping.getDefaultBoost())
					.endObject();
			} else {
				// TODO what about mapping.getBoost()?
			}
			
			builder
				.startObject("_source")
					.field("enabled", false)
				.endObject();

			builder.endObject();
			// end alias

			return builder.endObject();
		} catch (IOException e) {
			throw new SearchEngineException("failed to create mapping source", e);
		}
	}
	// @formatter:on

	@SuppressWarnings("deprecation")
	private String index(Index index) {
		switch (index) {
		case NOT_ANALYZED:
		case UN_TOKENIZED:
			return "not_analyzed";
		case NO:
			return "no";
		case ANALYZED:
		case TOKENIZED:
			return "analyzed";
		default:
			throw new SearchEngineException("unknown index type: " + index);
		}
	}

	private String store(Store store) {
		switch (store) {
		case NO:
			return "no";
		case YES:
		case COMPRESS:
			return "yes";
		default:
			throw new SearchEngineException("unknown store type: " + store);
		}
	}

	private boolean includeInAll(ExcludeFromAll excludeFromAll) {
		switch (excludeFromAll) {
		case NO:
		case NO_ANALYZED:
			return true;
		case YES:
			return false;
		default:
			throw new SearchEngineException("unknown excludeFromAll type: " + excludeFromAll);
		}
	}

	private String termVector(TermVector termVector) {
		switch (termVector) {
		case NO:
			return "no";
		case YES:
			return "yes";
		case WITH_OFFSETS:
			return "with_offsets";
		case WITH_POSITIONS:
			return "with_positions";
		case WITH_POSITIONS_OFFSETS:
			return "with_positions_offsets";
		default:
			throw new SearchEngineException("unknown termVector type: " + termVector);
		}
	}

	private String generateIndexName() {
		return UUID.randomUUID().toString();
	}

	public String getAlias() {
		return _alias;
	}

	private String getIndex() {
		return getIndex(true);
	}

	private String getIndex(boolean create) {
		if (_index == null && create) {
			verifyIndex();
		}
		return _index;
	}

	public ElasticSettings getSettings() {
		return _settings;
	}

	public void addAlias(String alias) {
		String index = getIndex();
		try {
			indicesAdminClient().prepareAliases().addAlias(index, alias).execute().actionGet();
		} catch (ElasticSearchException e) {
			throw new SearchEngineException("failed to add alias '" + alias + "' to index '"
					+ index + "'");
		}
	}

}