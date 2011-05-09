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

package org.compass.core.test.sort;

import org.compass.core.CompassHits;
import org.compass.core.CompassQuery;
import org.compass.core.CompassQuery.SortDirection;
import org.compass.core.CompassQuery.SortImplicitType;
import org.compass.core.CompassSession;
import org.compass.core.config.CompassSettings;
import org.compass.core.test.AbstractTestCase;
import org.junit.Ignore;

public class SortTests extends AbstractTestCase {

    protected String[] getMappings() {
        return new String[] { "sort/sort.cpm.xml" };
    }

    @Override
    protected void addSettings(CompassSettings settings) {
        // set no concurrent operations so sort by id and relevance will worok
//        settings.setBooleanSetting(LuceneEnvironment.Transaction.Processor.ReadCommitted.CONCURRENT_OPERATIONS, false);
    }

    protected void setUp() throws Exception {
        super.setUp();
        CompassSession session = openSession();
        
        A a = new A(new Long(1), "aab test testA", 1, 1.1f, new B(new Integer(2), "aab"));
        session.save(a);
        a = new A(new Long(2), "aac test testA", 2, 1.0f, new B(new Integer(1), "aac"));
        session.save(a);
        a = new A(new Long(3), "bbc test testB", 10, -1.0f, new B(new Integer(3), "aaa"));
        session.save(a);
        a = new A(new Long(4), "zx test testB", -10, 1.3f, new B(new Integer(4), "aad"));
        session.save(a);
        
        session.close();
    }

    public void testSortComponent() {
        CompassSession session = openSession();

        CompassHits hits = session.queryBuilder().queryString("test").toQuery()
                .addSort("a.id", SortDirection.AUTO).hits();
        assertEquals(4, hits.length());
        assertAId(1, 0, hits);
        assertAId(2, 1, hits);
        assertAId(3, 2, hits);
        assertAId(4, 3, hits);

        hits = session.queryBuilder().queryString("test").toQuery()
                .addSort("a.b.id", SortDirection.AUTO).hits();
        assertEquals(4, hits.length());
        assertAId(2, 0, hits);
        assertAId(1, 1, hits);
        assertAId(3, 2, hits);
        assertAId(4, 3, hits);

        hits = session.queryBuilder().queryString("test").toQuery()
                .addSort("a.value2", SortDirection.AUTO).hits();
        assertEquals(4, hits.length());
        assertAId(1, 0, hits);
        assertAId(2, 1, hits);
        assertAId(3, 2, hits);
        assertAId(4, 3, hits);

        hits = session.queryBuilder().queryString("test").toQuery()
                .addSort("a.b.value2", SortDirection.AUTO).hits();
        assertEquals(4, hits.length());
        assertAId(3, 0, hits);
        assertAId(1, 1, hits);
        assertAId(2, 2, hits);
        assertAId(4, 3, hits);

        session.close();
    }
    
    public void testSortRelevance() {
        CompassSession session = openSession();

        CompassQuery query = session.queryBuilder().queryString("aab^4 OR aac^3 OR bbc^2 OR zx^1").toQuery();
        query.addSort(SortImplicitType.SCORE);
        CompassHits hits = query.hits();
        assertEquals(4, hits.length());
        assertAId(1, 0, hits);
        assertAId(2, 1, hits);
        assertAId(3, 2, hits);
        assertAId(4, 3, hits);

        session.close();
    }

    public void testSortRelevanceReverse() {
        CompassSession session = openSession();

        CompassQuery query = session.queryBuilder().queryString("aab^4 OR aac^3 OR bbc^2 OR zx^1").toQuery();
        query.addSort(SortImplicitType.SCORE, SortDirection.REVERSE);
        CompassHits hits = query.hits();
        assertEquals(4, hits.length());
        assertAId(1, 3, hits);
        assertAId(2, 2, hits);
        assertAId(3, 1, hits);
        assertAId(4, 0, hits);

        session.close();
    }

    @Ignore("sorting by doc not supported")
    public void testSortDoc() {
        CompassSession session = openSession();

        CompassQuery query = session.queryBuilder().queryString("test").toQuery();
        query.addSort(SortImplicitType.DOC);
        CompassHits hits = query.hits();
        assertEquals(4, hits.length());
        assertAId(1, 0, hits);
        assertAId(2, 1, hits);
        assertAId(3, 2, hits);
        assertAId(4, 3, hits);
        
        session.close();
    }

    @Ignore("sorting by doc not supported")
    public void testSortDocReverse() {
        CompassSession session = openSession();

        CompassQuery query = session.queryBuilder().queryString("test").toQuery();
        query.addSort(SortImplicitType.DOC, SortDirection.REVERSE);
        CompassHits hits = query.hits();
        assertEquals(4, hits.length());
        assertAId(4, 0, hits);
        assertAId(3, 1, hits);
        assertAId(2, 2, hits);
        assertAId(1, 3, hits);
        
        session.close();
    }

    public void testSortString() {
        CompassSession session = openSession();

        CompassQuery query = session.queryBuilder().queryString("test").toQuery();
        query.addSort("value", CompassQuery.SortPropertyType.STRING);
        CompassHits hits = query.hits();
        assertEquals(4, hits.length());
        assertAId(1, 0, hits);
        assertAId(2, 1, hits);
        assertAId(3, 2, hits);
        assertAId(4, 3, hits);
        
        session.close();
    }

    public void testSortStringReverse() {
        CompassSession session = openSession();

        CompassQuery query = session.queryBuilder().queryString("test").toQuery();
        query.addSort("value", CompassQuery.SortPropertyType.STRING, CompassQuery.SortDirection.REVERSE);
        CompassHits hits = query.hits();
        assertEquals(4, hits.length());
        assertAId(4, 0, hits);
        assertAId(3, 1, hits);
        assertAId(2, 2, hits);
        assertAId(1, 3, hits);
        
        session.close();
    }

    public void testSortInt() {
        CompassSession session = openSession();

        CompassQuery query = session.queryBuilder().queryString("test").toQuery();
        query.addSort("intValue", CompassQuery.SortPropertyType.INT);
        CompassHits hits = query.hits();
        assertEquals(4, hits.length());
        assertAId(4, 0, hits);
        assertAId(1, 1, hits);
        assertAId(2, 2, hits);
        assertAId(3, 3, hits);
        
        session.close();
    }
    
    public void testSortIntReverse() {
        CompassSession session = openSession();

        CompassQuery query = session.queryBuilder().queryString("test").toQuery();
        query.addSort("intValue", CompassQuery.SortPropertyType.INT, CompassQuery.SortDirection.REVERSE);
        CompassHits hits = query.hits();
        assertEquals(4, hits.length());
        assertAId(3, 0, hits);
        assertAId(2, 1, hits);
        assertAId(1, 2, hits);
        assertAId(4, 3, hits);
        
        session.close();
    }

    public void testSortFloat() {
        CompassSession session = openSession();

        CompassQuery query = session.queryBuilder().queryString("test").toQuery();
        query.addSort("floatValue", CompassQuery.SortPropertyType.FLOAT);
        CompassHits hits = query.hits();
        assertEquals(4, hits.length());
        assertAId(3, 0, hits);
        assertAId(2, 1, hits);
        assertAId(1, 2, hits);
        assertAId(4, 3, hits);
        
        session.close();
    }

    public void testSortFloatReverse() {
        CompassSession session = openSession();

        CompassQuery query = session.queryBuilder().queryString("test").toQuery();
        query.addSort("floatValue", CompassQuery.SortPropertyType.FLOAT, CompassQuery.SortDirection.REVERSE);
        CompassHits hits = query.hits();
        assertEquals(4, hits.length());
        assertAId(4, 0, hits);
        assertAId(1, 1, hits);
        assertAId(2, 2, hits);
        assertAId(3, 3, hits);
        
        session.close();
    }

    private void assertAId(long id, int hitNum, CompassHits hits) {
        A a = (A) hits.data(hitNum);
        assertEquals("score: " + hits.score(hitNum), id, a.getId().longValue());
    }

}
