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

package org.compass.annotations.test.metadata;

import org.compass.annotations.test.AbstractAnnotationsTestCase;
import org.compass.core.CompassHits;
import org.compass.core.CompassSession;
import org.compass.core.config.CompassConfiguration;

/**
 * @author kimchy
 */
public class MetaDataTests extends AbstractAnnotationsTestCase {

    protected void addExtraConf(CompassConfiguration conf) {
        conf.addClass(A.class);
    }

    public void testMultipleMetaDatas() throws Exception {
        CompassSession session = openSession();

        A a = new A();
        a.setId(1);
        a.setValue("value");
        session.save(a);

        a = (A) session.load(A.class, 1);
        assertEquals("value", a.getValue());

        CompassHits hits = session.find("value:value");
        assertEquals(0, hits.length());
        hits = session.find("value1:value");
        assertEquals(1, hits.length());
        hits = session.find("value2:value");
        assertEquals(1, hits.length());

        session.close();
    }

}
