package at.molindo.elastic.compass;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.compass.core.Property;
import org.compass.core.Property.Store;
import org.compass.core.Resource;
import org.compass.core.ResourceFactory;
import org.compass.core.engine.SearchEngineException;
import org.compass.core.engine.naming.DefaultPropertyPath;
import org.compass.core.engine.naming.StaticPropertyPath;
import org.compass.core.mapping.ResourceMapping;
import org.compass.core.mapping.ResourcePropertyMapping;
import org.compass.core.spi.ResourceKey;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.get.GetField;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;

import at.molindo.utils.collections.CollectionUtils;

public class ElasticClient {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(ElasticClient.class);

	private final ElasticSearchEngineFactory _searchEngineFactory;
	private final String _index;
	private final Client _client;
	private final Map<String, String[]> _typeFields;

	private ConcurrentHashMap<String, StaticPropertyPath> _pathCache = new ConcurrentHashMap<String, StaticPropertyPath>();
	
	public ElasticClient(ElasticSearchEngineFactory searchEngineFactory, String index, Client client) {
		if (searchEngineFactory == null) {
			throw new NullPointerException("searchEngineFactory");
		}
		if (index == null) {
			throw new NullPointerException("index");
		}
		if (client == null) {
			throw new NullPointerException("client");
		}

		_searchEngineFactory = searchEngineFactory;
		_index = index;
		_client = client;

		_typeFields = new HashMap<String, String[]>();
		HashSet<String> fields = new HashSet<String>();

		// stored fields
		for (ResourceMapping mapping : _searchEngineFactory.getMapping().getRootMappings()) {

			for (ResourcePropertyMapping property : mapping.getResourcePropertyMappings()) {
				if (property.getStore() != Store.NO) {
					fields.add(property.getPath().getPath());
				}
			}
			_typeFields.put(mapping.getAlias(), fields.toArray(new String[fields.size()]));
			fields.clear();
		}
	}

	public void create(final ElasticResource resource) {
		try {
			_client.prepareIndex(_index, resource.getAlias(), resource.getId())
					.setSource(toXContentBuilder(resource))
					.execute(new ActionListener<IndexResponse>() {

						@Override
						public void onResponse(IndexResponse response) {
							if (log.isTraceEnabled()) {
								log.trace("created id " + resource.getAlias() + "#"
										+ response.getId());
							}
						}

						@Override
						public void onFailure(Throwable e) {
							log.warn("failed to create " + resource.getAlias() + "#"
									+ resource.getId(), e);
						}
					});
		} catch (IOException e) {
			throw new SearchEngineException("failed to create resource", e);
		}
	}

	public void update(final ElasticResource resource) {
		try {
			_client.prepareIndex(_index, resource.getAlias(), resource.getId())
					.setSource(toXContentBuilder(resource))
					.execute(new ActionListener<IndexResponse>() {

						@Override
						public void onResponse(IndexResponse response) {
							if (log.isTraceEnabled()) {
								log.trace("updated id " + resource.getAlias() + "#"
										+ response.getId());
							}
						}

						@Override
						public void onFailure(Throwable e) {
							log.warn("failed to update " + resource.getAlias() + "#"
									+ resource.getId(), e);
						}
					});
		} catch (IOException e) {
			throw new SearchEngineException("failed to create resource", e);
		}
	}

	public Resource[] get(ResourceKey key) {
		Property[] ids = key.getIds();
		if (ids == null || ids.length == 0) {
			return ElasticResource.NO_RESOURCES;
		}

		Resource[] resources = new Resource[ids.length];
		String[] fields = _typeFields.get(key.getAlias());
		if (fields == null) {
			throw new SearchEngineException("unknown alias " + key.getAlias());
		}

		ResourceMapping mapping = _searchEngineFactory.getMapping()
				.getRootMappingByAlias(key.getAlias());

		for (int i = 0; i < ids.length; i++) {
			GetResponse response = _client
					.prepareGet(_index, key.getAlias(), ids[i].getStringValue()).setFields(fields)
					.execute().actionGet();

			resources[i] = new ElasticResource(response.getType(), _searchEngineFactory);
			for (Map.Entry<String, GetField> e : response.getFields().entrySet()) {

				// we handle collections internally, i.e. the Compass-way
				String value = (String) CollectionUtils.first(e.getValue().getValues());

				ResourcePropertyMapping propertyMapping = mapping.getResourcePropertyMappingByPath(toPath(e
						.getKey()));
				if (propertyMapping == null) {
					throw new SearchEngineException("No resource property mapping is defined for alias ["
							+ key.getAlias() + "] and resource property [" + e.getKey() + "]");
				}
				Property property = _searchEngineFactory.getResourceFactory()
						.createProperty(value, propertyMapping);
				property.setBoost(propertyMapping.getBoost());

				resources[i].addProperty(property);
			}
		}
		return resources;
	}

	private StaticPropertyPath toPath(String path) {
		StaticPropertyPath p = _pathCache.get(path);
		if (p == null) {
			p = new StaticPropertyPath(path);
			// p.getPath() now intern()ed
			p = CollectionUtils.putIfAbsent(_pathCache, p.getPath(), p);
		}
		return p;
	}
	
	protected XContentBuilder toXContentBuilder(ElasticResource resource) throws IOException {
		XContentBuilder builder = jsonBuilder().startObject();

		for (Property property : resource.getProperties()) {
			builder.field(property.getName(), property.getStringValue());
		}

		return builder.endObject();
	}
}
