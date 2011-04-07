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

import org.apache.lucene.analysis.TokenStream;
import org.compass.core.Property;
import org.compass.core.Resource;
import org.compass.core.ResourceFactory;
import org.compass.core.converter.mapping.ResourcePropertyConverter;
import org.compass.core.engine.SearchEngineException;
import org.compass.core.mapping.ResourcePropertyMapping;
import org.compass.core.mapping.ReverseType;
import org.compass.core.util.StringUtils;
import org.compass.core.util.reader.ReverseStringReader;

public class ElasticResourceFactory implements ResourceFactory {

	private ElasticSearchEngineFactory searchEngineFactory;

	public ElasticResourceFactory(ElasticSearchEngineFactory searchEngineFactory) {
		this.searchEngineFactory = searchEngineFactory;
	}

	public String getNullValue() {
		return "";
	}

	public boolean isNullValue(String value) {
		return value == null || value.length() == 0;
	}

	@Override
	public Resource createResource(String alias) throws SearchEngineException {
		return new ElasticMultiResource(alias, searchEngineFactory);
	}

	public Property createProperty(String value, ResourcePropertyMapping mapping) throws SearchEngineException {
		return createProperty(mapping.getPath().getPath(), value, mapping);
	}

	public Property createProperty(String value, ResourcePropertyMapping mapping, Property.Store store, Property.Index index) throws SearchEngineException {
		return createProperty(mapping.getPath().getPath(), value, mapping, store, index);
	}

	public Property createProperty(String name, String value, ResourcePropertyMapping mapping) throws SearchEngineException {
		return createProperty(name, value, mapping, mapping.getStore(), mapping.getIndex());
	}

	public Property createProperty(String name, String value, @SuppressWarnings("rawtypes") ResourcePropertyConverter converter) {
		Property.Store store = converter.suggestStore();
		if (store == null) {
			store = Property.Store.YES;
		}
		Property.Index index = converter.suggestIndex();
		if (index == null) {
			index = Property.Index.ANALYZED;
		}
		Property.TermVector termVector = converter.suggestTermVector();
		if (termVector == null) {
			termVector = Property.TermVector.NO;
		}
		Property property = createProperty(name, value, store, index, termVector);
		if (converter.suggestOmitNorms() != null) {
			property.setOmitNorms(converter.suggestOmitNorms());
		}
		if (converter.suggestOmitTf() != null) {
			property.setOmitTf(converter.suggestOmitTf());
		}
		return property;
	}

	public Property createProperty(String name, String value, ResourcePropertyMapping mapping, Property.Store store, Property.Index index) throws SearchEngineException {
		Property property;
		if (mapping.getReverse() == ReverseType.NO) {
			property = createProperty(name, value, store, index, mapping.getTermVector());
		} else if (mapping.getReverse() == ReverseType.READER) {
			property = createProperty(name, new ReverseStringReader(value), mapping.getTermVector());
		} else if (mapping.getReverse() == ReverseType.STRING) {
			property = createProperty(name, StringUtils.reverse(value), store, index, mapping.getTermVector());
		} else {
			throw new SearchEngineException("Unsupported Reverse type [" + mapping.getReverse()
					+ "]");
		}
		property.setBoost(mapping.getBoost());
		if (mapping.isOmitNorms() != null) {
			property.setOmitNorms(mapping.isOmitNorms());
		}
		if (mapping.isOmitTf() != null) {
			property.setOmitTf(mapping.isOmitTf());
		}
		property.setBoost(mapping.getBoost());
		((ElasticProperty) property).setPropertyMapping(mapping);
		return property;
	}

	public Property createProperty(String name, String value, Property.Store store, Property.Index index) throws SearchEngineException {
		return createProperty(name, value, store, index, Property.TermVector.NO);
	}

	public Property createProperty(String name, String value, Property.Store store, Property.Index index, Property.TermVector termVector) throws SearchEngineException {
		return new ElasticProperty(new ElasticField(name, value).setStore(store).setIndex(index).setTermVector(termVector));
	}

	public Property createProperty(String name, TokenStream tokenStream, Property.TermVector termVector) {
		return new ElasticProperty(new ElasticField(name, tokenStream).setTermVector(termVector));
	}

	public Property createProperty(String name, Reader value) {
		return createProperty(name, value, Property.TermVector.NO);
	}

	public Property createProperty(String name, byte[] value, Property.Store store) throws SearchEngineException {
		return new ElasticProperty(new ElasticField(name, value).setStore(store));
	}

	public Property createProperty(String name, Reader value, Property.TermVector termVector) {
		return new ElasticProperty(new ElasticField(name, value).setTermVector(termVector));
	}

}
