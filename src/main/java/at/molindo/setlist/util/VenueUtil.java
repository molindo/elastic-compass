package at.molindo.setlist.util;

import java.net.URL;
import java.text.CollationKey;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;

import at.molindo.setlist.model.venue.City;
import at.molindo.util.Country;
import at.molindo.util.hibernate.CollationUtil;

public final class VenueUtil {

	private VenueUtil() {

	}

	private static final String BASE_DIR = "geodata";
	// private static final BidiMap ENGLISH_US_STATES, ENGLISH_CANADA_STATES,
	// ENGLISH_UK_STATES;
	private static final Map<CollationKey, Country> ENGLISH_NAME2COUNTRY;
	private static final Map<String, BidiMap> COUNTRY2STATES;
	private static final Set<Country> OMMITING_COUNTRIES, STATE_CODE_USING;

	static {

		{
			OMMITING_COUNTRIES = Collections.unmodifiableSet(new HashSet<Country>(Arrays
					.asList(new Country[] { Country.GB })));

			STATE_CODE_USING = Collections.unmodifiableSet(new HashSet<Country>(Arrays
					.asList(new Country[] { Country.CANADA, Country.US })));
		}

		{
			final Map<CollationKey, Country> map = new HashMap<CollationKey, Country>();
			for (final Country c : Country.getAvailableCountries()) {
				map.put(CollationUtil.getKeyIgnoreAccents(c.getDisplayName(Locale.ENGLISH)), c);
			}
			/*
			 * some custom ones
			 */
			map.put(CollationUtil.getKeyIgnoreAccents("Russian Federation"), Country.get("RU"));
			map.put(CollationUtil.getKeyIgnoreAccents("Holland"), Country.get("NL"));
			map.put(CollationUtil.getKeyIgnoreAccents("The Netherlands"), Country.get("NL"));
			map.put(CollationUtil.getKeyIgnoreAccents("West Germany"), Country.GERMANY);
			ENGLISH_NAME2COUNTRY = Collections.unmodifiableMap(map);
		}
		{
			// TODO i18n
			final Map<String, BidiMap> map = new HashMap<String, BidiMap>();
			for (final String lang : new String[] { "en" }) {
				for (final Country c : Country.getAvailableCountries()) {
					final String bundle = String.format("%s/statelist_%s", BASE_DIR, c.getCode());
					final String properties = String.format("%s_%s.properties", bundle, lang);
					final URL url = VenueUtil.class.getClassLoader().getResource(properties);

					if (url == null) {
						continue;
					}

					final ResourceBundle usa = ResourceBundle.getBundle(bundle, Locale.ENGLISH);
					final Enumeration<String> keys = usa.getKeys();

					final BidiMap countryMap = new DualHashBidiMap();
					while (keys.hasMoreElements()) {
						final String key = keys.nextElement();
						final String stateCode = key.trim();
						final String state = usa.getString(key).trim();
						countryMap.put(stateCode, state);
					}

					if (countryMap.size() > 0) {
						map.put(c.getCode(), countryMap);
					}
				}
			}

			COUNTRY2STATES = Collections.unmodifiableMap(map);
		}
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> getStates(final Country c) {
		return COUNTRY2STATES.get(c.getCode());
	}

	private static BidiMap getMap(final Country c) {
		return COUNTRY2STATES.get(c.getCode());
	}

	public static Country getCountryForEnglishName(final String englishName) {
		if (getMap(Country.GB).containsValue(englishName)) {
			return Country.GB;
		}
		return englishName == null ? null : ENGLISH_NAME2COUNTRY.get(CollationUtil
				.getKeyIgnoreAccents(englishName));
	}

	public static boolean isUSAStateAbbreviation(final String abb) {
		return abb != null && getMap(Country.US).containsKey(abb.toUpperCase());
	}

	public static boolean isCanadaStateAbbreviation(final String abb) {
		return abb != null && getMap(Country.CANADA).containsKey(abb.toUpperCase());
	}

	public static boolean isBrazilStateAbbreviation(final String abb) {
		return abb != null && getMap(Country.BRAZIL).containsKey(abb.toUpperCase());
	}

	public static boolean isAustraliaStateAbbreviation(final String abb) {
		return abb != null && getMap(Country.AUSTRALIA).containsKey(abb.toUpperCase());
	}

	public static boolean isUSAStateName(final String name) {
		return name != null && getMap(Country.US).containsValue(name);
	}

	public static boolean isCanadaStateName(final String name) {
		return name != null && getMap(Country.CANADA).containsValue(name);
	}

	public static boolean isBrazilStateName(final String name) {
		return name != null && getMap(Country.BRAZIL).containsValue(name);
	}

	public static String getCanadaStateAbbreviationForName(final String name) {
		return (String) getMap(Country.CANADA).getKey(name);
	}

	public static boolean isAustraliaStateName(final String name) {
		return name != null && getMap(Country.AUSTRALIA).containsValue(name);
	}

	public static boolean isMexicoStateName(final String name) {
		return name != null && getMap(Country.MEXICO).containsValue(name);
	}

	public static String getAustraliaStateAbbreviationForName(final String name) {
		return (String) getMap(Country.AUSTRALIA).getKey(name);
	}

	public static String getAustraliaNameForAbbreviation(final String abb) {
		return (String) getMap(Country.AUSTRALIA).get(abb.toUpperCase());
	}

	public static String getBrazilNameForAbbreviation(final String abb) {
		return (String) getMap(Country.BRAZIL).get(abb.toUpperCase());
	}

	public static String getCanadaNameForAbbreviation(final String abb) {
		return (String) getMap(Country.CANADA).get(abb.toUpperCase());
	}

	public static String getUSANameForAbbreviation(final String abb) {
		return (String) getMap(Country.US).get(abb.toUpperCase());
	}

	public static String getUSAStateAbbreviationForName(final String name) {
		return (String) getMap(Country.US).getKey(name);
	}

	public static String getStateCodeReplacement(final Country c, final String stateName) {
		final BidiMap m = getMap(c);

		if (m != null) {
			return (String) m.getKey(stateName);
		}
		return null;
	}

	public static boolean isStateCodeUsingCountry(final Country c) {
		return STATE_CODE_USING.contains(c);
	}

	public static boolean isUKCountryName(final String name) {
		return name != null && getMap(Country.GB).containsValue(name);
	}

	public static boolean isUKCountryAbbreviation(final String name) {
		return name != null && getMap(Country.GB).containsKey(name.toUpperCase());
	}

	public static String getUKCountryAbbreviationForName(final String name) {
		return (String) getMap(Country.GB).getKey(name);
	}

	public static String getUKCountryNameForAbbreviation(final String name) {
		return (String) getMap(Country.GB).get(name);
	}

	public static boolean isKnownState(final String abb) {
		return isUSAStateAbbreviation(abb) || isUSAStateAbbreviation(abb);
	}

	/**
	 * 
	 * @param lon
	 * @param lat
	 * @param one
	 * @param two
	 * @return the nearest of the two cities, null if both cities are null or
	 *         the first city if both cities are equally near
	 */
	public static City getNearerCity(final double lon, final double lat, final City one, final City two) {

		if (one == null) {
			return two == null ? null : two;
		}

		if (two == null || one.getCoordinates().equals(two.getCoordinates())) {
			return one;
		}

		final double diffOne = getAbsoluteLongitudeDifference(lon, one.getCoordinates()
				.getLongitude())
				+ getAbsoluteLatitudeDifference(lat, one.getCoordinates().getLatitude());

		final double diffTwo = getAbsoluteLongitudeDifference(lon, two.getCoordinates()
				.getLongitude())
				+ getAbsoluteLatitudeDifference(lat, two.getCoordinates().getLatitude());

		if (diffOne > diffTwo) {
			return two;
		} else if (diffOne < diffTwo) {
			return one;
		}
		return null;
	}

	public static double distance(final double lat1, final double lon1, final double lat2, final double lon2, final DistanceUnit unit) {
		final double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1))
				* Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		if (unit == DistanceUnit.KILOMETERS) {
			dist = dist * 1.609344;
		} else if (unit == DistanceUnit.NAUTICAL_MILES) {
			dist = dist * 0.8684;
		}
		return dist;
	}

	private static double deg2rad(final double deg) {
		return deg * Math.PI / 180.0;
	}

	private static double rad2deg(final double rad) {
		return rad * 180.0 / Math.PI;
	}

	public static double getAbsoluteLongitudeDifference(final double coordOne, final double coordTwo) {
		return getAbsoluteDifference(coordOne, coordTwo, 180);
	}

	public static double getAbsoluteLatitudeDifference(final double coordOne, final double coordTwo) {
		return getAbsoluteDifference(coordOne, coordTwo, 90);
	}

	public static boolean isCountryOmmitingCountry(final Country country) {
		return OMMITING_COUNTRIES.contains(country);
	}

	private static double getAbsoluteDifference(final double coordOne, final double coordTwo, final int max) {
		final double distance = Math.max(coordOne, coordTwo) - Math.min(coordOne, coordTwo);

		/*
		 * if the distance bigger than the maximum to each side, the distance is
		 * shorter if we go the other way. E.g. the distance between longitude
		 * -89 and 89 is 2, not 178
		 */
		if (distance > max) {
			// two times max is kind of the circumferential
			return 2 * max - distance;
		}
		return distance;
	}

}
