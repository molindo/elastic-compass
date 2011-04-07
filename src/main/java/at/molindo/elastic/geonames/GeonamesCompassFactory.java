package at.molindo.elastic.geonames;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.compass.core.Compass;
import org.compass.core.cache.first.NullFirstLevelCache;
import org.compass.core.config.CompassConfiguration;
import org.compass.core.config.CompassEnvironment;

import at.molindo.setlist.model.venue.missing.MissingCity;
import at.molindo.setlist.model.venue.missing.MissingCityAlias;
import at.molindo.setlist.model.venue.missing.UniqueGeoId;

public class GeonamesCompassFactory {

    public Compass buildCompass() throws IOException {
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
        conf.getSettings().setSetting(CompassEnvironment.Cache.FirstLevel.TYPE, NullFirstLevelCache.class.getName());
        conf.getSettings().setBooleanSetting(CompassEnvironment.DEBUG, true);

        addConverters(conf);
        addMappings(conf);
        return conf;
    }
	
    private void addConverters(CompassConfiguration conf) {
    	conf.registerConverter("countryConverter", new CountryConverter());
    	conf.registerConverter("geoIdConverter", new UniqueGeoIdConverter());
	}

	protected CompassConfiguration createConfiguration() {
        return new CompassConfiguration();
    }
    
    protected void addMappings(CompassConfiguration conf) {
        conf.addClass(MissingCity.class);
        conf.addClass(MissingCityAlias.class);
        conf.addClass(UniqueGeoId.class);
    }
    
}
