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

package org.compass.core.test.all;

import java.util.Arrays;

import org.compass.core.CompassHits;
import org.compass.core.CompassSession;
import org.compass.core.Resource;
import org.compass.core.config.CompassEnvironment;
import org.compass.core.config.CompassSettings;
import org.compass.core.test.AbstractTestCase;
import org.junit.Ignore;

import at.molindo.elastic.compass.ElasticEnvironment;
import at.molindo.elastic.compass.ElasticHelper;
import at.molindo.elastic.term.TermFreqVector;
import at.molindo.elastic.term.TermPositionVector;
import at.molindo.elastic.term.TermVectorOffsetInfo;

/**
 * @author kimchy
 */
public class AllTests extends AbstractTestCase {

    protected String[] getMappings() {
        return new String[]{"all/All.cpm.xml"};
    }

    protected void addSettings(CompassSettings settings) {
        settings.setBooleanSetting(CompassEnvironment.All.EXCLUDE_ALIAS, false);
    }

    @Ignore("alias not stored in all property")
    public void testAll() {
        CompassSession session = openSession();

        Long id = (long) 1;
        A a = new A();
        a.setId(id);
        a.setValue1("test1");
        a.setValue2("test2");
        session.save("a1", a);

        refresh(session);
        
        CompassHits result = session.find("test1");
        assertEquals(1, result.getLength());
        a = (A) result.data(0);
        assertEquals("test1", a.getValue1());
        assertEquals("test2", a.getValue2());

        // verify that we can find the alias in the all property as well
        result = session.find("a1");
        assertEquals(1, result.getLength());

        result = session.find("test2");
        assertEquals(1, result.getLength());
        a = (A) result.data(0);
        assertEquals("test1", a.getValue1());
        assertEquals("test2", a.getValue2());

        result = session.find(ElasticEnvironment.Mapping.ALL_FIELD + ":test2");
        assertEquals(1, result.getLength());
        a = (A) result.data(0);
        assertEquals("test1", a.getValue1());
        assertEquals("test2", a.getValue2());

    }

    @Ignore("alias not stored in all property")
    public void testAllWithTermVectorOnProperties() {
        CompassSession session = openSession();

        Long id = (long) 1;
        A a = new A();
        a.setId(id);
        a.setValue1("test1");
        a.setValue2("test2");
        session.save("a10", a);

        refresh(session);
        
        CompassHits result = session.find("test1");
        assertEquals(1, result.getLength());
        a = (A) result.data(0);
        assertEquals("test1", a.getValue1());
        assertEquals("test2", a.getValue2());

        // verify that we can find the alias in the all property as well
        result = session.find("a10");
        assertEquals(1, result.getLength());

        result = session.find("test2");
        assertEquals(1, result.getLength());
        a = (A) result.data(0);
        assertEquals("test1", a.getValue1());
        assertEquals("test2", a.getValue2());

        result = session.find(ElasticEnvironment.Mapping.ALL_FIELD + ":test2");
        assertEquals(1, result.getLength());
        a = (A) result.data(0);
        assertEquals("test1", a.getValue1());
        assertEquals("test2", a.getValue2());

    }

    @Ignore("renaming all property not supported")
    public void testAllWithDifferentAllMetaData() {
        CompassSession session = openSession();

        Long id = new Long(1);
        A a = new A();
        a.setId(id);
        a.setValue1("test1");
        a.setValue2("test2");
        session.save("a2", a);

        refresh(session);
        
        CompassHits result = session.find("zzz-everything:test1");
        assertEquals(1, result.getLength());
        a = (A) result.data(0);
        assertEquals("test1", a.getValue1());
        assertEquals("test2", a.getValue2());

        result = session.find("test2");
        assertEquals(0, result.getLength());

        result = session.find("zzz-everything:test2");
        assertEquals(1, result.getLength());
        a = (A) result.data(0);
        assertEquals("test1", a.getValue1());
        assertEquals("test2", a.getValue2());

        result = session.find(ElasticEnvironment.Mapping.ALL_FIELD + ":test2");
        assertEquals(0, result.getLength());
    }

    public void testAllNoAll() {
        CompassSession session = openSession();

        Long id = new Long(1);
        A a = new A();
        a.setId(id);
        a.setValue1("test1");
        a.setValue2("test2");
        session.save("a3", a);

        refresh(session);
        
        CompassHits result = session.find("test2");
        assertEquals(0, result.getLength());

        result = session.find(ElasticEnvironment.Mapping.ALL_FIELD + ":test2");
        assertEquals(0, result.getLength());

    }

    public void testExcludeAll() {
        CompassSession session = openSession();

        Long id = new Long(1);
        A a = new A();
        a.setId(id);
        a.setValue1("test1111");
        a.setValue2("test2222");
        session.save("a4", a);

        refresh(session);
        
        CompassHits result = session.find("test1111");
        assertEquals(0, result.getLength());

        result = session.find("test2222");
        assertEquals(1, result.getLength());

        result = session.find("cValue11");
        assertEquals(0, result.getLength());

        result = session.find("cValue21");
        assertEquals(1, result.getLength());

        session.close();
    }

    public void testAllWithTermVector() {
        CompassSession session = openSession();

        Long id = new Long(1);
        A a = new A();
        a.setId(id);
        a.setValue1("test1");
        a.setValue2("test2");
        session.save("a5", a);

        refresh(session);
        
        Resource r = session.loadResource("a5", id);
        TermFreqVector termInfoVector = ElasticHelper.getTermFreqVector(session, r, ElasticEnvironment.Mapping.ALL_FIELD);
        assertNotNull(termInfoVector);
        try {
            int[] positions = ((TermPositionVector) termInfoVector).getTermPositions(0);
            fail(Arrays.toString(positions));
        } catch (ClassCastException e) {

        }

        try {
            TermVectorOffsetInfo[] offsets = ((TermPositionVector) termInfoVector).getOffsets(0);
            fail(Arrays.toString(offsets));
        } catch (ClassCastException e) {

        }

        session.close();
    }

    public void testAllWithTermVectorPositions() {
        CompassSession session = openSession();

        Long id = new Long(1);
        A a = new A();
        a.setId(id);
        a.setValue1("test1");
        a.setValue2("test2");
        session.save("a6", a);

        refresh(session);
        
        Resource r = session.loadResource("a6", id);
        TermFreqVector termInfoVector = ElasticHelper.getTermFreqVector(session, r, ElasticEnvironment.Mapping.ALL_FIELD);
        assertNotNull(termInfoVector);
        int[] positions = ((TermPositionVector) termInfoVector).getTermPositions(0);
        assertNotNull(positions);

        TermVectorOffsetInfo[] offsets = ((TermPositionVector) termInfoVector).getOffsets(0);
        assertNull(offsets);

        session.close();
    }

    public void testAllWithTermVectorOffsets() {
        CompassSession session = openSession();

        Long id = new Long(1);
        A a = new A();
        a.setId(id);
        a.setValue1("test1");
        a.setValue2("test2");
        session.save("a7", a);

        refresh(session);
        
        Resource r = session.loadResource("a7", id);
        TermFreqVector termInfoVector = ElasticHelper.getTermFreqVector(session, r, ElasticEnvironment.Mapping.ALL_FIELD);
        assertNotNull(termInfoVector);
        int[] positions = ((TermPositionVector) termInfoVector).getTermPositions(0);
        assertNull(positions);

        TermVectorOffsetInfo[] offsets = ((TermPositionVector) termInfoVector).getOffsets(0);
        assertNotNull(offsets);

        session.close();
    }

    public void testAllWithTermVectorPositionsAndOffsets() {
        CompassSession session = openSession();

        Long id = new Long(1);
        A a = new A();
        a.setId(id);
        a.setValue1("test1");
        a.setValue2("test2");
        session.save("a8", a);

        refresh(session);
        
        Resource r = session.loadResource("a8", id);
        TermFreqVector termInfoVector = ElasticHelper.getTermFreqVector(session, r, ElasticEnvironment.Mapping.ALL_FIELD);
        assertNotNull(termInfoVector);
        int[] positions = ((TermPositionVector) termInfoVector).getTermPositions(0);
        assertNotNull(positions);

        TermVectorOffsetInfo[] offsets = ((TermPositionVector) termInfoVector).getOffsets(0);
        assertNotNull(offsets);

        session.close();
    }

    public void testExcludeAliasFromAll() {
        CompassSession session = openSession();

        Long id = new Long(1);
        A a = new A();
        a.setId(id);
        a.setValue1("test1");
        a.setValue2("test2");
        session.save("a9", a);

        refresh(session);
        
        CompassHits hits = session.find("a9");
        assertEquals(0, hits.length());

        session.close();
    }
}
