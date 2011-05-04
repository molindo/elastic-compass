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

package org.compass.core.test.querybuilder.count;

import org.compass.core.CompassSession;
import org.compass.core.test.AbstractTestCase;

/**
 * @author kimchy
 */
public class QueryCountTests extends AbstractTestCase {

    protected String[] getMappings() {
        return new String[]{"querybuilder/count/mapping.cpm.xml"};
    }

    public void testSimpleCount() {
        CompassSession session = openSession();

        A a = new A();
        a.id = 1;
        a.value = "test";
        session.save("a", a);

        session.close();

        session = openSession();

        assertEquals(1, session.queryBuilder().queryString("test").toQuery().count());
        assertEquals(0, session.queryBuilder().queryString("nottests").toQuery().count());

        session.close();
    }
}
