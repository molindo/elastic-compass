package at.molindo.elastic.mapping;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.compass.core.Compass;
import org.compass.core.cache.first.NullFirstLevelCache;
import org.compass.core.config.CompassConfiguration;
import org.compass.core.config.CompassEnvironment;
import org.compass.core.mapping.ResourceMapping;
import org.compass.core.spi.InternalCompass;
import org.junit.Test;

public class MappingTest {

    protected Compass buildCompass() throws IOException {
        CompassConfiguration conf = buildConf();
        return conf.buildCompass();
    }

    protected CompassConfiguration buildConf() throws IOException {
        CompassConfiguration conf = createConfiguration()
                .configure("/at/molindo/elastic/mapping/compass.cfg.xml");
        File testPropsFile = new File("compass.test.properties");
        if (testPropsFile.exists()) {
            Properties testProps = new Properties();
            testProps.load(new FileInputStream(testPropsFile));
            conf.getSettings().addSettings(testProps);
        }
        for (String mapping : getMappings()) {
            conf.addResource(getPackagePrefix() + mapping, MappingTest.class.getClassLoader());
        }
        conf.getSettings().setSetting(CompassEnvironment.Cache.FirstLevel.TYPE, NullFirstLevelCache.class.getName());
        conf.getSettings().setBooleanSetting(CompassEnvironment.DEBUG, true);
        addExtraConf(conf);
        return conf;
    }
	
    protected CompassConfiguration createConfiguration() {
        return new CompassConfiguration();
    }
    
    protected String[] getMappings() {
        return new String[0];
    }
    
    protected String getPackagePrefix() {
        return "at/molindo/elastic/mapping/";
    }
    
    protected void addExtraConf(CompassConfiguration conf) {
        conf.addClass(A.class);
        conf.addClass(B.class);
    }
    
	@Test
	public void test() throws Exception {
		InternalCompass compass = (InternalCompass) buildCompass();
		
		ResourceMapping mappingA = compass.getMapping().getMappingByClass(A.class);
		assertEquals("A", mappingA.getAlias());
		
		ResourceMapping mappingB = compass.getMapping().getMappingByClass(B.class);
		assertEquals("foo", mappingB.getAlias());
		
	}
}
