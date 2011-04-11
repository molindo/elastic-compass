/**
 * Copyright 2011 Molindo GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.molindo.elastic.compass.mapping;

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

import at.molindo.elastic.compass.ElasticEnvironment;

public class MappingTest {

    protected Compass buildCompass() throws IOException {
        CompassConfiguration conf = buildConf();
        return conf.buildCompass();
    }

    protected CompassConfiguration buildConf() throws IOException {
        CompassConfiguration conf = createConfiguration()
                .configure(getPackagePrefix() + "/compass.cfg.xml");
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
        conf.getSettings().setBooleanSetting(ElasticEnvironment.LOCAL, true);
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
        return  File.separatorChar + MappingTest.class.getPackage().getName().replace('.', File.separatorChar);
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
