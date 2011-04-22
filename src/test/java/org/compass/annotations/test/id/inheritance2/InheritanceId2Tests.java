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

package org.compass.annotations.test.id.inheritance2;

import org.compass.annotations.test.AbstractAnnotationsTestCase;
import org.compass.core.CompassSession;
import org.compass.core.config.CompassConfiguration;
import org.junit.Ignore;

/**
 * @author kimchy
 */
public class InheritanceId2Tests extends AbstractAnnotationsTestCase {

    protected void addExtraConf(CompassConfiguration conf) {
        conf.addClass(B.class);
    }

    @Ignore("multiple @SearchableId not supported")
    public void testInheritanceId() throws Exception {
        CompassSession session = openSession();

        B b = new B();
        b.id = 1;
        b.id2 = "value";
        session.save(b);

        refresh(session);
        
        b = session.load(B.class, 1, "value");
        assertEquals(1, b.id);
        assertEquals("value", b.id2);

        session.close();
    }
}