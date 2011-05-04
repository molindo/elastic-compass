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

package org.compass.annotations.test.component.prefix.simple;

import org.compass.annotations.test.AbstractAnnotationsTestCase;
import org.compass.core.CompassSession;
import org.compass.core.Resource;
import org.compass.core.config.CompassConfiguration;
import org.compass.core.mapping.ResourcePropertyMapping;
import org.junit.Ignore;

/**
 * @author kimchy
 */
public class SimplePrefixTests extends AbstractAnnotationsTestCase {

    protected void addExtraConf(CompassConfiguration conf) {
        conf.addClass(A.class).addClass(B.class);
    }

    @Ignore("dotted paths not supported")
    public void testSimpleComponentPrefix() {
        CompassSession session = openSession();

        A a = new A();
        a.id = 1;
        a.b = new B();
        a.b.id = 2;
        a.b.value = "value1";
        session.save(a);
        
        Resource resource = session.loadResource(A.class, 1);
        assertEquals("value1", resource.getValue("test_value"));
        assertEquals("2", resource.getValue("$/A/b/id"));

        ResourcePropertyMapping rp = getCompass().getMapping().getResourcePropertyMappingByPath("A.b.value");
        assertNotNull(rp);
        assertEquals("test_value", rp.getName());

        rp = getCompass().getMapping().getResourcePropertyMappingByPath("A.b.id");
        assertNotNull(rp);
        assertEquals("id", rp.getName());

        assertEquals(1, session.find("A.b.value:value1").length());
        assertEquals(1, session.find("A.b.id:2").length());

        session.close();
    }
}
