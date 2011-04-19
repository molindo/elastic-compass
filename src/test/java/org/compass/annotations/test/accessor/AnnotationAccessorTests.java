package org.compass.annotations.test.accessor;

import org.compass.annotations.test.AbstractAnnotationsTestCase;
import org.compass.core.CompassSession;
import org.compass.core.config.CompassConfiguration;

/**
 * @author kimchy
 */
public class AnnotationAccessorTests extends AbstractAnnotationsTestCase {

    protected void addExtraConf(CompassConfiguration conf) {
        conf.addClass(A.class);
    }

    public void testFieldLevelAccessors() {
        CompassSession session = openSession();

        A a = new A();
        a.id = 1;
        a.Name = "test";
        session.save(a);

        refresh(session);
        
        assertNotNull(session.load(A.class, 1));

        session.close();
    }
}
