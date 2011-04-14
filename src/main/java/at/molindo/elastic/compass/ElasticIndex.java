package at.molindo.elastic.compass;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
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
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.status.IndicesStatusResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.indices.IndexMissingException;

import at.molindo.utils.collections.CollectionUtils;

/**
 * manages an index
 */
public class ElasticIndex {

	private final Client _client;
	private final CompassMapping _mapping;

	private final String _alias;
	private String _index;

	public ElasticIndex(String alias, Client client, CompassMapping mapping) {
		_alias = alias;
		_client = client;
		_mapping = mapping;
	}

	private void createIndex() {
		_index = generateIndexName();

		IndicesAdminClient indicesAdminClient = indicesAdminClient();
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
		indicesAdminClient().prepareDelete(getIndex()).execute().actionGet();
		createIndex();
	}

	public synchronized void verifyIndex() {
		IndicesAdminClient indicesAdminClient = indicesAdminClient();

		try {
			IndicesStatusResponse response = indicesAdminClient.prepareStatus(_alias).execute()
					.actionGet();
			_index = CollectionUtils.firstValue(response.getIndices()).getIndex();
			if (getIndex().equals(_alias)) {
				throw new SearchEngineException("alias name must not point to index, was " + _alias);
			}
		} catch (IndexMissingException e) {
			// alias unknown, create new index
			createIndex();
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

}
