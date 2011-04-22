/*
 * Copyright 2004-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.compass.core.mapping.rsem.builder;

import org.compass.core.converter.Converter;
import org.compass.core.converter.mapping.ResourcePropertyConverter;
import org.compass.core.engine.naming.StaticPropertyPath;
import org.compass.core.mapping.rsem.RawBoostPropertyMapping;

/**
 * Allows to dynamically define the boost value of the resource based on a resource property value.
 *
 * @author kimchy
 * @see RSEM#boost(String) 
 */
public class ResourceBoostMappingBuilder {

    final RawBoostPropertyMapping mapping;

    /**
     * Constructs a new boost resource property mapping.
     */
    public ResourceBoostMappingBuilder(String name) {
        this.mapping = new RawBoostPropertyMapping();
        mapping.setName(name);
        mapping.setPath(new StaticPropertyPath(name));
    }

    /**
     * The default boost value that will be used of the resrouce property to be used
     * has <code>null</code> value. Defaults to <code>1.0f</code>.
     */
    public ResourceBoostMappingBuilder defaultBoost(float defaultBoost) {
        mapping.setDefaultBoost(defaultBoost);
        return this;
    }

    /**
     * Sets the lookup converter name (registered with Compass) that will be used to convert the value
     * of the property.
     */
    public ResourceBoostMappingBuilder converter(String converterName) {
        mapping.setConverterName(converterName);
        return this;
    }

    /**
     * Sets an actual converter that will be used to convert this property value.
     */
    public ResourceBoostMappingBuilder converter(Converter converter) {
        mapping.setConverter(converter);
        return this;
    }

    /**
     * Sets an actual converter that will be used to convert this property value.
     */
    public ResourceBoostMappingBuilder converter(ResourcePropertyConverter converter) {
        mapping.setConverter(converter);
        return this;
    }
}