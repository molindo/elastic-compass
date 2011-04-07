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

package org.compass.core.converter.basic;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import org.compass.core.converter.ConversionException;
import org.compass.core.converter.basic.format.Formatter;
import org.compass.core.converter.basic.format.FormatterFactory;
import org.compass.core.converter.basic.format.TextFormatFormatter;
import org.compass.core.mapping.ResourcePropertyMapping;
import org.compass.core.marshall.MarshallingContext;

/**
 * Converts dates to String and vice versa. Supports the notion of "now" using
 * {@link org.compass.core.converter.basic.DateMathParser}.
 *
 * @author kimchy
 */
public class DateConverter extends AbstractFormatConverter {

    public static final String DEFAULT_NOW_PREFIX = "now";

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd-HH-mm-ss-S-a";

    private static class DateFormatter implements FormatterFactory {

        private String format;

        private Locale locale;

        public void configure(String format, Locale locale) {
            this.format = format;
            this.locale = locale;
        }

        public Formatter create() {
            DateFormat dateFormat;
            if (locale != null) {
                dateFormat = new SimpleDateFormat(format, locale);
            } else {
                dateFormat = new SimpleDateFormat(format);
            }
            dateFormat.setLenient(false);
            return new TextFormatFormatter(dateFormat);
        }
    }

    @Override
    protected String doGetDefaultFormat() {
        return DEFAULT_DATE_FORMAT;
    }

    protected FormatterFactory doCreateFormatterFactory() {
        return new DateConverter.DateFormatter();
    }

    /**
     * Try all the configured formatters to format the str into an Object.
     */
    protected Object doFromString(String str, ResourcePropertyMapping resourcePropertyMapping, MarshallingContext context) throws ConversionException {
        try {
            if (str.toLowerCase().startsWith(DEFAULT_NOW_PREFIX)) {
                // TODO have timezone pluggable
                DateMathParser p = new DateMathParser(TimeZone.getDefault(), locale);
                return p.parseMath(str.substring(DEFAULT_NOW_PREFIX.length()));
            }
            for (int i = 0; i < formatters.length; i++) {
                try {
                    return formatters[i].parse(str);
                } catch (ParseException e) {
                    // do nothing, continue to the next one
                }
            }
            throw new ConversionException("Failed to parse date [" + str + "]");
        } catch (ParseException e) {
            throw new ConversionException("Failed to parse date [" + str + "]", e);
        }
    }

    /**
     * Uses the first configured formatter (also known as the default one) to convert it to String.
     */
    protected String doToString(Object o, ResourcePropertyMapping resourcePropertyMapping, MarshallingContext context) {
        return formatters[0].format(o);
    }
}
