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

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.compass.core.Property;
import org.compass.core.Resource;
import org.compass.core.converter.mapping.ResourcePropertyConverter;
import org.compass.core.engine.SearchEngineException;
import org.compass.core.engine.SearchEngineFactory;
import org.compass.core.mapping.ResourceMapping;
import org.compass.core.mapping.ResourcePropertyMapping;
import org.compass.core.spi.AliasedObject;
import org.compass.core.spi.InternalResource;
import org.compass.core.spi.ResourceKey;

public class ElasticResource implements AliasedObject, InternalResource, Map<String, Property[]> {

	private static final long serialVersionUID = -724311219313513581L;

	private final static String[] NO_STRINGS = new String[0];
	public static final Resource[] NO_RESOURCES = new Resource[0];

	private ArrayList<Property> properties = new ArrayList<Property>();

	private float boost = 1.0f;

	private transient ElasticSearchEngineFactory searchEngineFactory;

	private transient ResourceMapping resourceMapping;

	private transient ResourceKey resourceKey;

	private String alias;

	public ElasticResource(String alias, ElasticSearchEngineFactory searchEngineFactory) {
		this.alias = alias;
		setSearchEngineFactory(searchEngineFactory);
	}

	@Override
	public String getAlias() {
		return alias;
	}

	public String getUID() {
		return getResourceKey().buildUID();
	}

	public String getId() {
		String[] ids = getIds();
		return ids[0];
	}

	public String[] getIds() {
		Property[] idProperties = getIdProperties();
		String[] ids = new String[idProperties.length];
		for (int i = 0; i < idProperties.length; i++) {
			if (idProperties[i] != null) {
				ids[i] = idProperties[i].getStringValue();
			}
		}
		return ids;
	}

	public Property getIdProperty() {
		Property[] idProperties = getIdProperties();
		return idProperties[0];
	}

	public Property[] getIdProperties() {
		return getResourceKey().getIds();
	}

	public String getValue(String name) {
		for (Property property : properties) {
			if (property.getName().equals(name) && (!property.isBinary()))
				return property.getStringValue();
		}
		return null;
	}

	public Object getObject(String name) {
		Property prop = getProperty(name);
		if (prop == null) {
			return null;
		}
		return prop.getObjectValue();
	}

	public Object[] getObjects(String name) {
		Property[] props = getProperties(name);
		Object[] ret = new Object[props.length];
		for (int i = 0; i < props.length; i++) {
			ret[i] = props[i].getObjectValue();
		}
		return ret;
	}

	public String[] getValues(String name) {
		List<String> result = new ArrayList<String>();
		for (Property property : properties) {
			if (property.getName().equals(name) && (!property.isBinary()))
				result.add(property.getStringValue());
		}

		if (result.size() == 0)
			return NO_STRINGS;

		return result.toArray(new String[result.size()]);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Resource addProperty(String name, Object value) throws SearchEngineException {
		String alias = getAlias();

		ResourcePropertyMapping propertyMapping = resourceMapping.getResourcePropertyMapping(name);
		if (propertyMapping == null) {
			throw new SearchEngineException("No resource property mapping is defined for alias ["
					+ alias + "] and resource property [" + name + "]");
		}
		ResourcePropertyConverter converter = propertyMapping.getResourcePropertyConverter();
		if (converter == null) {
			converter = (ResourcePropertyConverter) this.searchEngineFactory.getMapping()
					.getConverterLookup().lookupConverter(value.getClass());
		}
		String strValue = converter.toString(value, propertyMapping);

		Property property = this.searchEngineFactory.getResourceFactory()
				.createProperty(strValue, propertyMapping);
		property.setBoost(propertyMapping.getBoost());
		return addProperty(property);
	}

	public Resource addProperty(String name, Reader value) throws SearchEngineException {
		String alias = getAlias();

		ResourcePropertyMapping propertyMapping = resourceMapping.getResourcePropertyMapping(name);
		if (propertyMapping == null) {
			throw new SearchEngineException("No resource property mapping is defined for alias ["
					+ alias + "] and resource property [" + name + "]");
		}

		ElasticProperty property = new ElasticProperty(new ElasticField(name, value).setTermVector(propertyMapping
				.getTermVector()));
		property.setBoost(propertyMapping.getBoost());
		property.setPropertyMapping(propertyMapping);
		return addProperty(property);
	}

	public Resource addProperties(Property[] props) {
		for (Property property : props) {
			addProperty(property);
		}
		return this;
	}
	
	public Resource addProperty(Property property) {
		ElasticProperty lProperty = (ElasticProperty) property;
		lProperty
				.setPropertyMapping(resourceMapping.getResourcePropertyMapping(property.getName()));
		properties.add(property);
		return this;
	}

	public Resource setProperty(String name, Object value) throws SearchEngineException {
		removeProperties(name);
		return addProperty(name, value);
	}

	public Resource setProperty(String name, Reader value) throws SearchEngineException {
		removeProperties(name);
		return addProperty(name, value);
	}

	public Resource setProperty(Property property) {
		removeProperties(property.getName());
		return addProperty(property);
	}

	public Resource removeProperty(String name) {
		Iterator<Property> it = properties.iterator();
		while (it.hasNext()) {
			Property property = it.next();
			if (property.getName().equals(name)) {
				it.remove();
				return this;
			}
		}
		return this;
	}

	public Resource removeProperties(String name) {
		Iterator<Property> it = properties.iterator();
		while (it.hasNext()) {
			Property property = it.next();
			if (property.getName().equals(name)) {
				it.remove();
			}
		}
		return this;
	}

	public Property getProperty(String name) {
		for (Property property : properties) {
			if (property.getName().equals(name)) {
				return property;
			}
		}
		return null;
	}

	public Property[] getProperties(String name) {
		List<Property> result = new ArrayList<Property>();
		for (int i = 0; i < properties.size(); i++) {
			Property property = properties.get(i);
			if (property.getName().equals(name)) {
				result.add(property);
			}
		}

		if (result.size() == 0)
			return new Property[0];

		return result.toArray(new Property[result.size()]);
	}

	public Property[] getProperties() {
		return properties.toArray(new Property[properties.size()]);
	}

	public float getBoost() {
		return boost;
	}

	public Resource setBoost(float boost) {
		this.boost = boost;
		return this;
	}

	public void copy(Resource resource) {
		ElasticResource eResource = (ElasticResource) resource;
		this.properties = eResource.properties;
		this.alias = eResource.alias;
		if (eResource.searchEngineFactory != null) {
			this.setSearchEngineFactory(eResource.searchEngineFactory);
		}
	}

	public ResourceKey getResourceKey() {
		if (resourceKey == null) {
			resourceKey = new ResourceKey(resourceMapping, this);
		}
		return resourceKey;
	}

	public ResourceMapping getResourceMapping() {
		return this.resourceMapping;
	}

	public String getSubIndex() {
		return getResourceKey().getSubIndex();
	}

	@Override
	public void addUID() {
		removeProperties(resourceMapping.getUIDPath());
		Property uidProp = this.searchEngineFactory
				.getResourceFactory()
				.createProperty(resourceMapping.getUIDPath(), getResourceKey().buildUID(), Property.Store.YES, Property.Index.NOT_ANALYZED);
		uidProp.setOmitNorms(true);
		uidProp.setOmitTf(true);
		addProperty(uidProp);
	}

	@Override
	public void attach(SearchEngineFactory searchEngineFactory) {
		setSearchEngineFactory((ElasticSearchEngineFactory) searchEngineFactory);
	}

	// methods from the Map interface
	// ------------------------------

	public void clear() {
		throw new UnsupportedOperationException("Map operations are supported for read operations only");
	}

	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException("Map operations are supported for read operations only");
	}

	public void putAll(Map<? extends String, ? extends Property[]> t) {
		for (Map.Entry<? extends String, ? extends Property[]> entry : t.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	public Property[] remove(Object key) {
		removeProperties(key.toString());
		// TODO should return the old value
		return null;
	}

	public Property[] put(String key, Property[] value) {
		removeProperties(key);
		for (Property aValue : value) {
			addProperty(aValue);
		}
		// TODO should return the old value
		return null;
	}

	public Set<Map.Entry<String, Property[]>> entrySet() {
		Set<String> keySey = keySet();
		Set<Entry<String, Property[]>> entrySet = new HashSet<Entry<String, Property[]>>();
		for (Iterator<String> it = keySey.iterator(); it.hasNext();) {
			final String name = it.next();
			final Property[] props = getProperties(name);
			entrySet.add(new Map.Entry<String, Property[]>() {
				public String getKey() {
					return name;
				}

				public Property[] getValue() {
					return props;
				}

				public Property[] setValue(Property[] value) {
					put(name, value);
					// TODO should return the old value
					return null;
				}
			});
		}
		return Collections.unmodifiableSet(entrySet);
	}

	public Set<String> keySet() {
		Set<String> keySet = new HashSet<String>();
		for (Property property : properties) {
			keySet.add((property).getName());
		}
		return Collections.unmodifiableSet(keySet);
	}

	public boolean containsKey(Object key) {
		return getProperty(key.toString()) != null;
	}

	public int size() {
		return this.properties.size();
	}

	public boolean isEmpty() {
		return this.properties.isEmpty();
	}

	public Collection<Property[]> values() {
		Set<String> keySet = keySet();
		List<Property[]> values = new ArrayList<Property[]>();
		for (String name : keySet) {
			values.add(getProperties(name));
		}
		return Collections.unmodifiableList(values);
	}

	public Property[] get(Object key) {
		return getProperties(key.toString());
	}

	/**
	 * @param searchEngineFactory
	 *            the searchEngineFactory to set
	 */
	private void setSearchEngineFactory(ElasticSearchEngineFactory searchEngineFactory) {
		if (searchEngineFactory == null) {
			throw new NullPointerException("searchEngineFactory");
		}
		this.searchEngineFactory = searchEngineFactory;
		this.resourceMapping = searchEngineFactory.getMapping().getRootMappingByAlias(getAlias());
	}

	public Resource addProperties(Map<String, Object> source) {
		for (Map.Entry<String, Object> e : source.entrySet()) {
			addProperty(e.getKey(), e.getValue());
		}
		return this;
	}

	@Override
	public String toString() {
		return "ElasticResource [alias=" + alias + ", properties=" + properties + ", boost="
				+ boost + "]";
	}

}
