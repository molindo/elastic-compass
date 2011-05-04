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

package org.compass.core.test.converter.nullvalue;

import org.compass.core.CompassHits;
import org.compass.core.CompassSession;
import org.compass.core.config.CompassSettings;
import org.compass.core.test.AbstractTestCase;

/**
 * @author kimchy
 */
public class NullValueConverterTests extends AbstractTestCase {

    protected String[] getMappings() {
        return new String[]{"converter/nullvalue//mapping.cpm.xml"};
    }

    protected void addSettings(CompassSettings settings) {
        settings.setSetting("compass.converter.date.type", NullValueStringConverter.class.getName());
    }

    public void testNullValueConverter() {
        CompassSession session = openSession();

        A a = new A();
        a.id = 1;
        a.value = null;
        session.save("a", a);

        a = (A) session.load("a", "1");
        assertEquals(1, a.id);
        assertNull(a.value);

        CompassHits hits = session.find("value:moo");
        assertEquals(1, hits.length());

        session.close();
    }
}