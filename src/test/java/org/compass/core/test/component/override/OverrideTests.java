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

package org.compass.core.test.component.override;

import org.compass.core.CompassHits;
import org.compass.core.CompassSession;
import org.compass.core.config.CompassEnvironment;
import org.compass.core.config.CompassSettings;
import org.compass.core.test.AbstractTestCase;

/**
 * @author kimchy
 */
public class OverrideTests extends AbstractTestCase {

    protected String[] getMappings() {
        return new String[]{"component/override/mapping.cpm.xml"};
    }

    protected void addSettings(CompassSettings settings) {
        settings.setGroupSettings(CompassEnvironment.Converter.PREFIX,
                "bconv",
                new String[]{CompassEnvironment.Converter.TYPE},
                new String[]{BConverter.class.getName()});
    }

    public void testCompositeId() {
        CompassSession session = openSession();

        A a = new A();
        a.id = "1";
        a.b = new B();
        a.b.value = "test";
        session.save(a);
        
        a = (A) session.load(A.class, "1");
        assertEquals(a.b.value, "test");

        CompassHits hits = session.find("value:test");
        assertEquals(1, hits.length());
        hits = session.find("flatten:test");
        assertEquals(1, hits.length());

        session.close();
    }
}
