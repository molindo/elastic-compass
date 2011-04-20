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

package org.compass.annotations.test.cascade;

import org.compass.annotations.test.AbstractAnnotationsTestCase;
import org.compass.core.CompassSession;
import org.compass.core.config.CompassConfiguration;

/**
 * @author kimchy
 */
public class SimpleCascadeTests extends AbstractAnnotationsTestCase {

    protected void addExtraConf(CompassConfiguration conf) {
        conf.addClass(A.class).addClass(B.class);
    }

    public void testSimpleCascading() {
        CompassSession session = openSession();

        A a = new A();
        a.id = 1;
        a.value = "valuea";

        B b = new B();
        b.id = 1;
        b.value = "valueb";

        a.b = b;

        session.save(a);

        refresh(session);
        
        assertEquals(1, session.find("valuea").length());
        assertEquals(1, session.find("valueb").length());

        session.close();
    }
}