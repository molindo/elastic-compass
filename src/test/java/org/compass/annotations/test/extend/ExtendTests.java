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

package org.compass.annotations.test.extend;

import org.compass.annotations.test.AbstractAnnotationsTestCase;
import org.compass.core.CompassHits;
import org.compass.core.CompassSession;
import org.compass.core.config.CompassConfiguration;
import org.compass.core.mapping.ResourceMapping;
import org.compass.core.spi.InternalCompass;
import org.junit.Ignore;

/**
 * @author kimchy
 */
public class ExtendTests extends AbstractAnnotationsTestCase {

    protected void addExtraConf(CompassConfiguration conf) {
        conf.addClass(A.class);
    }

    public void testExtendsAliases() {
        ResourceMapping resourceMapping = ((InternalCompass) getCompass()).getMapping().getMappingByAlias("A");
        assertEquals(2, resourceMapping.getExtendedAliases().length);
    }

    public void testCpmAndAnnotations() throws Exception {
        CompassSession session = openSession();

        A a = new A();
        a.setId(1);
        a.setValue("value");
        a.setValue2("value2");
        session.save(a);

        refresh(session);
        
        a = (A) session.load(A.class, 1);
        assertEquals("value", a.getValue());
        assertEquals("value2", a.getValue2());

        CompassHits hits = session.find("value");
        assertEquals(1, hits.length());

        hits = session.find("value2");
        assertEquals(1, hits.length());

        session.close();
    }

    @Ignore("poly alias query not yet supported")
    public void testPolyAliasQuery() {
        CompassSession session = openSession();

        A a = new A();
        a.setId(1);
        a.setValue("value");
        a.setValue2("value2");
        session.save(a);

        refresh(session);
        
        CompassHits hits = session.queryBuilder().alias("A").hits();
        assertEquals(1, hits.length());
        hits = session.queryBuilder().polyAlias("A").hits();
        assertEquals(1, hits.length());
        hits = session.queryBuilder().polyAlias("A-contract").hits();
        assertEquals(1, hits.length());
        hits = session.queryBuilder().polyAlias("A-contract2").hits();
        assertEquals(1, hits.length());

        session.close();
    }

    public void testSetAliases() {
        CompassSession session = openSession();

        A a = new A();
        a.setId(1);
        a.setValue("value");
        a.setValue2("value2");
        session.save(a);
        
        refresh(session);
        
        CompassHits hits = session.queryBuilder().matchAll().setAliases(new String[] {"A"}).hits();
        assertEquals(1, hits.length());

        session.close();
    }

    public void testSetClases() {
        CompassSession session = openSession();

        A a = new A();
        a.setId(1);
        a.setValue("value");
        a.setValue2("value2");
        session.save(a);

        refresh(session);
        
        CompassHits hits = session.queryBuilder().matchAll().setTypes(new Class[] {A.class}).hits();
        assertEquals(1, hits.length());

        session.close();
    }
}
