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

package org.compass.annotations.test.nounmarshall.component.deep1;

import org.compass.annotations.test.AbstractAnnotationsTestCase;
import org.compass.core.CompassSession;
import org.compass.core.Resource;
import org.compass.core.config.CompassConfiguration;
import org.compass.core.config.CompassEnvironment;
import org.compass.core.config.CompassSettings;
import org.junit.Ignore;

/**
 * @author kimchy
 */
public class Deep1NoUnmarshallTests extends AbstractAnnotationsTestCase {

    protected void addSettings(CompassSettings settings) {
        settings.setBooleanSetting(CompassEnvironment.Osem.SUPPORT_UNMARSHALL, false);
    }

    protected void addExtraConf(CompassConfiguration conf) {
        conf.addClass(Order.class).addClass(Customer.class).addClass(Address.class);
    }

    @Ignore("dotted paths not supported")
    public void testDeepLevelComponentPrefix() {
        CompassSession session = openSession();

        Order order = new Order();
        order.id = 1;
        order.firstCustomer = new Customer();
        order.firstCustomer.id = 2;
        order.firstCustomer.name = "name1";
        order.firstCustomer.homeAddress = new Address();
        order.firstCustomer.homeAddress.id = 3;
        order.firstCustomer.homeAddress.location = "firstHome";
        order.firstCustomer.workAddress = new Address();
        order.firstCustomer.workAddress.id = 4;
        order.firstCustomer.workAddress.location = "firstWork";

        order.secondCustomer = new Customer();
        order.secondCustomer.id = 5;
        order.secondCustomer.name = "name2";
        order.secondCustomer.homeAddress = new Address();
        order.secondCustomer.homeAddress.id = 6;
        order.secondCustomer.homeAddress.location = "secondHome";
        order.secondCustomer.workAddress = new Address();
        order.secondCustomer.workAddress.id = 7;
        order.secondCustomer.workAddress.location = "secondWork";
        session.save(order);

        refresh(session);
        
        Resource resource = session.loadResource(Order.class, 1);
        assertEquals(2, resource.getValues("name").length);

        assertEquals(1, session.find("Order.firstCustomer.name:name1").length());
        assertEquals(1, session.find("Order.firstCustomer.homeAddress.location:firstHome").length());
        assertEquals(1, session.find("Order.firstCustomer.homeAddress.location.location:firstHome").length());

        session.close();
    }
}