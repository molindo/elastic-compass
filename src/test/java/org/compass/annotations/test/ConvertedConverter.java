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

package org.compass.annotations.test;

import org.compass.core.CompassException;
import org.compass.core.config.CompassConfigurable;
import org.compass.core.config.CompassSettings;
import org.compass.core.converter.ConversionException;
import org.compass.core.converter.basic.AbstractBasicConverter;
import org.compass.core.mapping.ResourcePropertyMapping;
import org.compass.core.marshall.MarshallingContext;

/**
 * @author kimchy
 */
public class ConvertedConverter extends AbstractBasicConverter<Converted> implements CompassConfigurable {

    private String separator;

    public void configure(CompassSettings settings) throws CompassException {
        separator = settings.getSetting("separator", "/");
    }

    protected Converted doFromString(String str, ResourcePropertyMapping resourcePropertyMapping, MarshallingContext context) throws ConversionException {
        Converted converted = new Converted();
        converted.value1 = str.substring(0, str.indexOf(separator));
        converted.value2 = str.substring(str.indexOf(separator) + separator.length());
        return converted;
    }

    protected String doToString(Converted converted, ResourcePropertyMapping resourcePropertyMapping, MarshallingContext context) {
        return converted.value1 + separator + converted.value2;
    }

}
