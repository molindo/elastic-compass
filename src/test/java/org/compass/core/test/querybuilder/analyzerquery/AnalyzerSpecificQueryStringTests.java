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

package org.compass.core.test.querybuilder.analyzerquery;

import org.compass.core.CompassHits;
import org.compass.core.CompassSession;
import org.compass.core.config.CompassSettings;
import org.compass.core.lucene.LuceneEnvironment;
import org.compass.core.test.AbstractTestCase;
import org.junit.Ignore;

/**
 * @author kimchy
 */
@Ignore("analyzers not supported")
@SuppressWarnings("deprecation")
public class AnalyzerSpecificQueryStringTests extends AbstractTestCase {

    protected String[] getMappings() {
        return new String[]{"querybuilder/analyzerquery/mapping.cpm.xml"};
    }

    protected void addSettings(CompassSettings settings) {
        settings.setGroupSettings(LuceneEnvironment.Analyzer.PREFIX, "ws",
                new String[]{LuceneEnvironment.Analyzer.TYPE},
                new String[]{LuceneEnvironment.Analyzer.CoreTypes.WHITESPACE});
    }

    public void testAnalyzerSpecificQueryString() {
        CompassSession session = openSession();

        A a = new A();
        a.id = 1;
        a.value = "that test";
        a.value1 = "the more";
        session.save("a", a);

        // first find based on prefix

        // this one will find a hit since we use just whitespace analyzer
        CompassHits hits = session.find("value:that");
        assertEquals(1, hits.length());
        // this one will find none since it uses the standard analyzer
        hits = session.find("value1:the");
        assertEquals(0, hits.length());

        // we just test with the all property
        // since we can't know which analyzer to use, we simply default to the search
        // one
        hits = session.find("that");
        assertEquals(0, hits.length());
        hits = session.find("the");
        assertEquals(0, hits.length());

        session.close();
    }
}
