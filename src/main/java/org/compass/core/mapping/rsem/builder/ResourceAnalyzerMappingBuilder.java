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
import org.compass.core.engine.naming.StaticPropertyPath;
import org.compass.core.mapping.rsem.RawResourcePropertyAnalyzerController;

/**
 * A builder allowing to construct a resource analyzer property mapping. Resource analyzer property mapping
 * allows to dynamically define the analyzer that will be used to analyzer the resource (properties that are
 * specificed as analyzed). The value of the analyzer property will be used to lookup a registered analyzer
 * within Compass. If no analyzer is found, the {@link #nullAnalyzer(String)} will be used (if specified).
 *
 * @author kimchy
 * @see RSEM#analyzer(String) 
 */
public class ResourceAnalyzerMappingBuilder {

    final RawResourcePropertyAnalyzerController mapping;

    /**
     * Constructs a new resource analyzer property using the provided name.
     */
    public ResourceAnalyzerMappingBuilder(String name) {
        this.mapping = new RawResourcePropertyAnalyzerController();
        mapping.setName(name);
        mapping.setPath(new StaticPropertyPath(name));
    }

    /**
     * The name of the analyzer that will be used if the property has the null value.
     */
    public ResourceAnalyzerMappingBuilder nullAnalyzer(String nullAnalyzer) {
        mapping.setNullAnalyzer(nullAnalyzer);
        return this;
    }

    /**
     * Sets the lookup converter name (registered with Compass) that will be used to convert the value
     * of the property.
     */
    public ResourceAnalyzerMappingBuilder converter(String converterName) {
        mapping.setConverterName(converterName);
        return this;
    }

    /**
     * Sets an actual converter that will be used to convert this property value.
     */
    public ResourceAnalyzerMappingBuilder converter(Converter converter) {
        mapping.setConverter(converter);
        return this;
    }
}