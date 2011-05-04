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

package org.compass.annotations.test.component.inheritance1;

import org.compass.annotations.test.AbstractAnnotationsTestCase;
import org.compass.core.CompassSession;
import org.compass.core.config.CompassConfiguration;

/**
 * @author kimchy
 */
public class NullIdValueTests extends AbstractAnnotationsTestCase {


    protected void addExtraConf(CompassConfiguration conf) {
        conf.addClass(A.class).addClass(X.class);
    }

    public void testNullIdValue() {
        CompassSession session = openSession();

        A a = new A();
        a.id = 1l;
        a.r = new X();

        session.save(a);
        
        session.load(A.class, 1);

        session.close();
    }
}
