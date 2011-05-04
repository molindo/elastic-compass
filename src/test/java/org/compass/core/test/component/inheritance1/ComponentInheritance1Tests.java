package org.compass.core.test.component.inheritance1;

import org.compass.core.CompassSession;
import org.compass.core.Resource;
import org.compass.core.config.CompassEnvironment;
import org.compass.core.config.CompassSettings;
import org.compass.core.test.AbstractTestCase;
import org.junit.Ignore;

/**
 * This holds a pretty complex relationship that has been reported as
 * broken in Compass. The main goal here is to test that the relationship
 * is supported.
 *
 * @author kimchy
 */
public class ComponentInheritance1Tests extends AbstractTestCase {


    protected String[] getMappings() {
        return new String[] {"component/inheritance1/Father.cpm.xml", "component/inheritance1/Child.cpm.xml"};
    }

    protected void addSettings(CompassSettings settings) {
        settings.setBooleanSetting(CompassEnvironment.Osem.FILTER_DUPLICATES, true);
    }

    @Ignore("alias property missing")
    public void testCorrectFatherSave() throws Exception {
        CompassSession session = openSession();

        FatherImpl.resetId();
        ChildImpl.resetId();
        
        FatherImpl father = new FatherImpl("Sir Ivan");
        FavouriteSonImpl favouriteSon = new FavouriteSonImpl("Ivan Jr", father);
        father.setFavouriteSon(favouriteSon);

        DaughterImpl daughter = new DaughterImpl("Betty Jr", father);
        father.getChildren().add(daughter);

        session.save(father);
        
        father = (FatherImpl) session.load("father", father.getId());
        assertEquals("Sir Ivan", father.getName());
        assertNotNull(father.getFavouriteSon());
        assertEquals("Ivan Jr", father.getFavouriteSon().getName());
        assertSame(father, father.getFavouriteSon().getFather());
        assertEquals(1, father.getChildren().size());
        assertEquals("Betty Jr", ((DaughterImpl)father.getChildren().iterator().next()).getName());
        assertSame(father, ((DaughterImpl)father.getChildren().iterator().next()).getFather());

        Resource resource = session.loadResource("father", father.getId());
        assertEquals(18, resource.getProperties().length);
        assertEquals("father", resource.getValue("alias"));
        assertEquals("0", resource.getValue("$/father/id"));
        assertEquals("Sir Ivan", resource.getProperties("name")[0].getStringValue());
        assertEquals("Sir Ivan", resource.getValue("$/father/name"));
        assertEquals(FavouriteSonImpl.class.getName(), resource.getValue("$/father/favouriteSon/class"));
        assertEquals("0", resource.getValue("$/father/favouriteSon/id"));
        assertEquals("Ivan Jr", resource.getProperties("name")[1].getStringValue());
        assertEquals("Ivan Jr", resource.getValue("$/father/favouriteSon/name"));
        assertEquals("0", resource.getValue("$/father/favouriteSon/father/id"));
        assertEquals("child", resource.getProperties("childalias")[0].getStringValue());
        assertEquals(DaughterImpl.class.getName(), resource.getValue("$/father/children/class"));
        assertEquals("1", resource.getValue("$/father/children/id"));
        assertEquals("Betty Jr", resource.getProperties("name")[2].getStringValue());
        assertEquals("Betty Jr", resource.getValue("$/father/children/name"));
        assertEquals("0", resource.getValue("$/father/children/father/id"));
        assertEquals("child", resource.getProperties("childalias")[1].getStringValue());
        assertEquals("1", resource.getValue("$/father/children/colSize"));

        session.close();
    }
}
