package at.molindo.elastic.compass;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import java.io.IOException;
import java.util.UUID;

import org.compass.core.CompassException;
import org.compass.core.Property.Index;
import org.compass.core.Property.Store;
import org.compass.core.Property.TermVector;
import org.compass.core.config.CompassConfigurable;
import org.compass.core.config.CompassSettings;
import org.compass.core.engine.SearchEngineException;
import org.compass.core.mapping.AllMapping;
import org.compass.core.mapping.BoostPropertyMapping;
import org.compass.core.mapping.ExcludeFromAll;
import org.compass.core.mapping.ResourceMapping;
import org.compass.core.mapping.ResourcePropertyMapping;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.status.IndexStatus;
import org.elasticsearch.action.admin.indices.status.IndicesStatusResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.node.Node;

import at.molindo.utils.collections.CollectionUtils;

public class ElasticNode implements CompassConfigurable {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(ElasticNode.class);

	private Node _node;
	private ElasticSearchEngineFactory _searchEngineFactory;

	private ElasticSettings _settings;

	private String _index;
	private String _alias;

	public ElasticNode(ElasticSearchEngineFactory searchEngineFactory) {
		if (searchEngineFactory == null) {
			throw new NullPointerException("searchEngineFactory");
		}
		_searchEngineFactory = searchEngineFactory;
	}

	@Override
	public void configure(CompassSettings settings) throws CompassException {
		_settings = new ElasticSettings(settings);
	}

	public void start() {
		if (_node == null) {
			if (_settings == null) {
				throw new IllegalStateException("ElasticNode not configured");
			}

			log.debug("starting ElasticNode");

			_node = nodeBuilder().client(true).node();

			Client client = _node.client();
			AdminClient adminClient = client.admin();
			IndicesAdminClient indicesAdminClient = adminClient.indices();

			_alias = _settings.getAliasName();

			IndexStatus indexStatus;
			try {
				IndicesStatusResponse response = indicesAdminClient.prepareStatus(_alias).execute()
						.actionGet();
				indexStatus = CollectionUtils.firstValue(response.getIndices());
				_index = indexStatus.getIndex();
				if (_index.equals(_alias)) {
					throw new SearchEngineException("alias name must not point to index, was "
							+ _alias);
				}
			} catch (IndexMissingException e) {
				// alias unknown, create new index
				indicesAdminClient.prepareCreate(_index = generateIndexName()).execute().actionGet();
				indicesAdminClient.prepareAliases().addAlias(_index, _alias).execute().actionGet();
			}

			// TODO configure index / check configuration

			for (ResourceMapping mapping : _searchEngineFactory.getMapping().getRootMappings()) {

				PutMappingResponse resp = indicesAdminClient.preparePutMapping(_index)
						.setType(mapping.getAlias()).setSource(toMappingSource(mapping)).execute()
						.actionGet();

				if (!resp.acknowledged()) {
					throw new SearchEngineException("failed to put mapping for type "
							+ mapping.getAlias());
				}
			}

		}
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

	public ElasticClient client() {
		if (_node == null) {
			throw new SearchEngineException("ElasticNode not started");
		}
		log.debug("creating new ElasticClient for ElasticNode");
		return new ElasticClient(_searchEngineFactory, _index, _node.client());
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
}
