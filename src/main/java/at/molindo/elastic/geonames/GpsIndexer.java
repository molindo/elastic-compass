package at.molindo.elastic.geonames;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.compass.core.Compass;
import org.compass.core.CompassCallback;
import org.compass.core.CompassException;
import org.compass.core.CompassSession;
import org.compass.core.CompassTemplate;
import org.compass.gps.CompassGpsDevice;
import org.compass.gps.device.support.parallel.ConcurrentParallelIndexExecutor;
import org.compass.gps.impl.SingleCompassGps;

import at.molindo.setlist.model.venue.missing.MissingCity;
import at.molindo.setlist.model.venue.missing.UniqueGeoId;
import at.molindo.setlist.util.CityImportBean;
import at.molindo.setlist.util.compass.MissingCityParallelGpsDevice;

public class GpsIndexer {
	public static void main(String[] args) throws IOException {
		Set<String> argSet = new HashSet<String>(Arrays.asList(args));
		
		Compass compass = new GeonamesCompassFactory().buildCompass();
		
		SingleCompassGps gps = new SingleCompassGps(compass);
			
		MissingCityParallelGpsDevice device = new MissingCityParallelGpsDevice();
		device.setName("missing-city-device");
		device.setCityImportBean(new CityImportBean());
		device.setParallelIndexExecutor(new ConcurrentParallelIndexExecutor(1));
		device.setGeonamesFileLocation("/opt/geonames/data/allCountries.txt.bz2");
		device.setGeonamesAliasesFileLocation("/opt/geonames/data/alternateNames.txt.bz2");
		device.setGeonamesAdmin1CodesLocation("/opt/geonames/data/admin1Codes.txt.bz2");
		
		gps.setGpsDevices(new CompassGpsDevice[]{device});

		compass.start();
		gps.start();
		
		if (argSet.contains("--index")) {
//			gps.index(MissingCity.class);
		}
		

		CompassTemplate template = new CompassTemplate(compass);

		System.out.println("find city #6363106");
		MissingCity city = template.get(MissingCity.class, new UniqueGeoId(6363106));
		System.out.println(city);
		System.out.println();
		
//		System.out.println("find 'london'");
//		for (Object o : template.find("london")) {
//			System.out.println(o);
//		}
//		System.out.println();
		
//		for (MissingCity c : template.execute(new CompassCallback<List<MissingCity>>() {
//
//			@Override
//			public List<MissingCity> doInCompass(CompassSession session) throws CompassException {
//				session.
//				
//				return null;
//			}
//		})) {
//			System.out.println(c);
//		}
		
		gps.stop();
		compass.stop();
	}
}
