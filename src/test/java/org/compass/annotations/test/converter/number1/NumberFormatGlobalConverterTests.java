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

package org.compass.annotations.test.converter.number1;

import org.compass.annotations.test.AbstractAnnotationsTestCase;
import org.compass.core.CompassQuery;
import org.compass.core.CompassQueryBuilder;
import org.compass.core.CompassSession;
import org.compass.core.config.CompassConfiguration;
import org.junit.Ignore;

/**
 * @author kimchy
 */
public class NumberFormatGlobalConverterTests extends AbstractAnnotationsTestCase {

    protected void addExtraConf(CompassConfiguration conf) {
        conf.addClass(A.class);
        conf.setSetting("compass.converter.long.format", "#00000000");
    }

    @Ignore("query.toString() not supported")
    public void testGlobablLongFormat() {
        CompassSession session = openSession();

        A a = new A();
        a.id = 1;
        a.property = 300l;
        session.save(a);

        refresh(session);
        
        CompassQueryBuilder queryBuilder = session.queryBuilder();
        CompassQuery query = queryBuilder.ge("property", 300L);
        assertEquals("property:[00000300 TO *]", query.toString());

        session.close();
    }
}
