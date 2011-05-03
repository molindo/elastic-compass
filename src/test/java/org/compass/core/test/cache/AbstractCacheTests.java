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

package org.compass.core.test.cache;

import org.compass.core.Compass;
import org.compass.core.CompassHits;
import org.compass.core.CompassSession;
import org.compass.core.config.CompassEnvironment;
import org.compass.core.config.CompassSettings;
import org.compass.core.lucene.LuceneEnvironment;
import org.compass.core.test.AbstractTestCase;

/**
 * 
 * @author kimchy
 * 
 */
@SuppressWarnings("deprecation")
public abstract class AbstractCacheTests extends AbstractTestCase {

    protected String[] getMappings() {
        return new String[] { "cache/cache.cpm.xml" };
    }

	protected void addSettings(CompassSettings settings) {
        super.addSettings(settings);
        settings.setLongSetting(LuceneEnvironment.SearchEngineIndex.CACHE_INTERVAL_INVALIDATION, 100);
    }

    public void testSimpleCacheInvalidation() throws Exception {

        if (getCompass().getSettings().getSetting(CompassEnvironment.CONNECTION).startsWith("ram://")) {
            // since we open two compass instances, this test won't work with ram based index
            return;
        }

        Long id = new Long(1);

        CompassSession session = openSession();
        A a = new A();
        a.setId(id);
        a.setValue("value1");
        session.save("a1", a);
        a.setValue("value2");
        session.save("a2", a);

        session.close();

        Compass compass2 =  buildCompass();
        // this should be visible to the new compass instance
        // since no caching has been done on this instance yet...
        session = compass2.openSession();

        a = (A) session.load("a1", id);
        assertEquals("value1", a.getValue());
        a = (A) session.load("a2", id);
        assertEquals("value2", a.getValue());

        // now update the instances for the first compass instance
        session = openSession();
        a = new A();
        a.setId(id);
        a.setValue("newvalue1");
        session.save("a1", a);
        a.setValue("newvalue2");
        session.save("a2", a);

        session.close();

        Thread.sleep(1000);

        // now check that the cache was invalidated for BOTH a1 and a2
        session = compass2.openSession();

        CompassHits hits = session.find("newvalue1 OR newvalue2");
        assertEquals(2, hits.length());

        session.close();

        compass2.close();
    }
}
