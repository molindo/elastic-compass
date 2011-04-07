package at.molindo.setlist.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.molindo.setlist.model.venue.City;
import at.molindo.setlist.model.venue.CityAlias;
import at.molindo.setlist.model.venue.CityAliasLanguage;
import at.molindo.setlist.model.venue.Coordinates;
import at.molindo.setlist.model.venue.missing.MissingCity;
import at.molindo.setlist.model.venue.missing.MissingCityAlias;
import at.molindo.setlist.model.venue.missing.UniqueGeoId;
import at.molindo.svc.IPersistentObjectService;
import at.molindo.util.Country;
import at.molindo.utils.data.StringUtils;
import at.molindo.utils.io.CharsetUtils;
import at.molindo.utils.io.Compression;
import at.molindo.utils.io.FileUtils;

import com.csvreader.CsvReader;

public class CityImportBean {

	private static final Logger log = LoggerFactory.getLogger(CityImportBean.class);

	private IPersistentObjectService _persistentObjectService;
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	private static final String concat(final String[] s) {
		final StringBuilder build = new StringBuilder(s.length);
		for (final String st : s) {
			build.append(st);
		}
		return build.toString();
	}

	public static interface MissingCityCallback {
		void onMissingCityCreated(int indexed, MissingCity mc);
	}

	public void parseGeonameFile(final File mainFile, final File aliasFile, final File adminCodes, final MissingCityCallback mcc) throws IOException {

		BufferedReader reader = null;
		try {
			final Map<UniqueGeoId, List<MissingCityAlias>> aliases = loadAliases(aliasFile);
			final Map<String, Map<String, String>> admin1Areas = loadAdminCodes(adminCodes);

			reader = new BufferedReader(new InputStreamReader(newInputStream(mainFile), Charset
					.forName("UTF-8")));

			int i = 0;
			String line;
			while ((line = reader.readLine()) != null) {
				final String[] arr = line.split("\t");

				if (arr.length != 19) {
					log.warn("Line elements != 19, skipping: " + line);
					continue;
				}

				final MissingCity mc = new MissingCity(new UniqueGeoId(Integer.parseInt(arr[0])));
				{
					// mandatory fields
					mc.setGeonameId(Integer.parseInt(arr[0]));
					mc.setName(arr[1]);
					mc.setAsciiname(arr[2]);
				}

				{
					// skipping arr[3] - setting alternate names later
					mc.setLatitude(doubleOrNull(arr[4]));
					mc.setLongitude(doubleOrNull(arr[5]));
					mc.setFeatureClass(characterOrNull(arr[6]));
					mc.setFeatureCode(stringOrNull(arr[7]));
					mc.setCountryCode(stringOrNull(arr[8]));
					mc.setCc2(stringOrNull(arr[9]));
					mc.setAdmin1Code(stringOrNull(arr[10]));
					mc.setAdmin2Code(stringOrNull(arr[11]));
					mc.setAdmin3Code(stringOrNull(arr[12]));
					mc.setAdmin4Code(stringOrNull(arr[13]));
					mc.setPopulation(integerOrNull(arr[14]));
					mc.setElevation(integerOrNull(arr[15]));
					mc.setGtopo30(integerOrNull(arr[16]));
					mc.setTimezone(stringOrNull(arr[17]));
					mc.setModificationDate(dateOrNull(arr[18]));
				}

				{
					if (mc.getAdmin1Code() != null) {
						final Map<String, String> admin1 = admin1Areas.get(mc.getCountryCode());

						if (admin1 != null) {
							mc.setAdmin1Name(stringOrNull(admin1.get(mc.getAdmin1Code())));
						}
					}
				}

				final List<MissingCityAlias> als = aliases.get(mc.getGeoId());

				if (als != null) {
					mc.setAliases(als);
				}

				mcc.onMissingCityCreated(i++, mc);

			}
			log.info("finished: " + i);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	private Map<String, Map<String, String>> loadAdminCodes(final File file) throws IOException {

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(newInputStream(file), Charset
					.forName("UTF-8")));

			// Map<CountryCode, Map<StateNumber, StateName>
			final Map<String, Map<String, String>> country2States = new HashMap<String, Map<String, String>>();

			String line;
			while ((line = reader.readLine()) != null) {
				final String[] arr = line.split("\t");

				if (arr.length != 2) {
					log.warn("Line elements != 2, skipping: " + line);
					continue;
				}
				final int dot = arr[0].indexOf('.');
				final String countryAndNumber = stringOrNull(arr[0]);
				final String name = stringOrNull(arr[1]);
				if (dot == -1 || countryAndNumber == null || name == null) {
					log.warn("malformed state: " + line);
					continue;
				}

				final String countryCode = countryAndNumber.substring(0, dot);
				final String number = countryAndNumber
						.substring(dot + 1, countryAndNumber.length());

				Map<String, String> number2Name = country2States.get(countryCode);

				if (number2Name == null) {
					number2Name = new HashMap<String, String>();
					country2States.put(countryCode, number2Name);
				}

				number2Name.put(number, name);
			}

			return country2States;
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	private InputStream newInputStream(final File file) throws FileNotFoundException, IOException {
		return FileUtils.in(file, Compression.AUTO);
	}

	private Map<UniqueGeoId, List<MissingCityAlias>> loadAliases(final File file) throws IOException {

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(newInputStream(file), Charset
					.forName("UTF-8")));

			final Map<UniqueGeoId, List<MissingCityAlias>> aliases = new HashMap<UniqueGeoId, List<MissingCityAlias>>();

			String line;
			long i = 0;
			while ((line = reader.readLine()) != null) {
				final String[] arr = line.split("\t");

				if (arr.length < 4 || arr.length > 6) {
					log.warn("Line elements < 4 || > 6, skipping: " + line);
					continue;
				}
				++i;
				final MissingCityAlias alias = new MissingCityAlias();
				{
					// three mandatory fields
					final Integer alternateId = integerOrNull(arr[0]);
					final Integer geonameId = integerOrNull(arr[1]);
					final String alternateName = stringOrNull(arr[3]);

					if (alternateId == null || geonameId == null || alternateName == null) {
						log.warn("Skipping incomplete line: " + line);
						continue;
					}

					alias.setAlternateNameId(alternateId);
					alias.setGeonameId(geonameId);
					alias.setAlternateName(alternateName);
				}

				{
					// optional fields
					alias.setIsoLanguage(stringOrNull(arr[2]));

					if (alias.getIsoLanguage() == null) {
						continue;
					}

					if (arr.length > 4) {
						alias.setPreferredName(fromString(arr[4]));

						if (arr.length > 5) {
							alias.setShortName(fromString(arr[5]));
						}
					}
				}

				final UniqueGeoId uniqueId = new UniqueGeoId(alias.getGeonameId());

				List<MissingCityAlias> list = aliases.get(uniqueId);

				if (list == null) {
					list = new LinkedList<MissingCityAlias>();
					aliases.put(uniqueId, list);
				}
				list.add(alias);

			}
			log.info("Loaded " + i + " missing city aliases");
			return aliases;
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	private static String stringOrNull(final String s) {
		if (StringUtils.empty(s)) {
			return null;
		}
		return s;
	}

	private static Double doubleOrNull(final String s) {
		if (!StringUtils.empty(s)) {
			try {
				return Double.parseDouble(s);
			} catch (final NumberFormatException ex) {
			}
		}
		return null;
	}

	private static Integer integerOrNull(final String s) {
		if (!StringUtils.empty(s)) {
			try {
				return Integer.parseInt(s);
			} catch (final NumberFormatException ex) {
			}
		}
		return null;
	}

	private static boolean fromString(final String s) {
		if (StringUtils.empty(s)) {
			return false;
		}

		if ("1".equals(s) || "true".equals(s)) {
			return true;
		}
		return false;
	}

	private static Date dateOrNull(final String s) {
		if (!StringUtils.empty(s)) {
			try {
				return DATE_FORMAT.parse(s);
			} catch (final ParseException e) {
			}
		}
		return null;
	}

	private static Character characterOrNull(final String s) {
		if (!StringUtils.empty(s)) {
			return s.charAt(0);
		}
		return null;
	}

	public void saveInitialImport() {

		try {
			final Map<String, City> cities = getCities();

			{
				final InputStream is = CityImportBean.class.getClassLoader()
						.getResourceAsStream("alternateNames.txt");
				final CsvReader reader = new CsvReader(is, Charset.forName("UTF-8"));
				reader.setDelimiter((char) Integer.parseInt("09", 16));
				reader.setSafetySwitch(false);
				reader.setRecordDelimiter('\n');

				while (reader.readRecord()) {
					final String cityID = reader.get(1);
					final String lang = reader.get(2);
					final String cityName = reader.get(3);
					final City city = cities.get(cityID);

					// Wien = 2761369 first = 13252 last = 6831256 433561
					if (concat(reader.getValues()).indexOf("\n") > -1) {
						log.warn("Value with linebreak in it - records might be incomplete!");
					}
					if (city == null) {
						continue;
					}

					CityAliasLanguage cal = null;
					if (CharsetUtils.is(cityName, CharsetUtils.ISO_8859_1)
							&& (cal = CityAliasLanguage.fromCode(lang.toLowerCase())) != null) {
						city.getAliases().add(new CityAlias(cityName, cal));
					}
				}

				final Map<String, CityAliasLanguage> langs = getCountries();

				for (final City c : cities.values()) {

					if (c.getAliases().size() > 0) {
						CityAlias def = getDefault(c);

						if (def == null) {

							final CityAliasLanguage suggestedLang = langs.get(c.getCountry());

							int langOccurences = 0;
							for (final CityAlias alias : c.getAliases()) {
								if (alias.getLang().equals(suggestedLang)) {
									++langOccurences;
									def = alias;
								}
							}

							if (langOccurences == 1) {
								/*
								 * if the only alias is the same as the alias
								 * and in the same language, we can delete the
								 * alias
								 */
								if (def.getLang().equals(suggestedLang)
										&& def.getName().equals(c.getImportName())) {
									c.getAliases().remove(def);
								}

							} else if (langOccurences > 1 && c.getImportName() == null) {
								log
										.warn("No import name but more aliases in country's language for	"
												+ c);
							} else {
								def = null;
							}

						}
						if (def != null) {
							c.setDisplayName(def.getName());
						}
					}

					final Set<CityAlias> copy = new HashSet<CityAlias>(c.getAliases());
					for (final CityAlias s : c.getAliases()) {

						/*
						 * remove obsolete aliases. e.g. - importName: London -
						 * en -> London (remove) - de -> London (remove) - fr ->
						 * Londres (keep) - es -> Londres (keep)
						 */
						if (c.getName().equals(c.getImportName())
								&& s.getName().equals(c.getImportName())) {
							copy.remove(s);
						}
					}
					c.setAliases(copy);
				}
			}
			_persistentObjectService.saveOrUpdateAll(cities.values());
			log.info("Import of {} cities successful", cities.size());
		} catch (final Exception ex) {
			log.warn("Import of cities failed", ex);
		}

	}

	private static CityAlias getDefault(final City city) {

		final Set<CityAlias> list = city.getAliases();

		for (final CityAlias p : list) {
			if (CityAliasLanguage.DEFAULT_LANG.equals(p.getLang())) {
				return p;
			}
		}

		return null;
	}

	private static Map<String, City> getCities() throws Exception {
		final HashMap<String, City> cities = new HashMap<String, City>();

		final InputStream is = CityImportBean.class.getClassLoader()
				.getResourceAsStream("cities1000.txt");

		final CsvReader reader = new CsvReader(is, Charset.forName("UTF-8"));
		reader.setDelimiter((char) Integer.parseInt("09", 16));
		reader.setSafetySwitch(false);

		while (reader.readRecord()) {
			final String cityName = reader.get(1);
			final String code = reader.get(0);
			final String countryCode = reader.get(8);
			final String latitude = reader.get(4);
			final String longitude = reader.get(5);
			final Country country = Country.get(countryCode);

			if (country == null) {
				continue;
			}

			if (country.getCode() == null) {
				log.warn("No code for: \"" + countryCode + "\"; city: " + cityName + " (" + code
						+ ") - skipping");
			} else {
				/*
				 * take ASCII representation of city name if city name isnt
				 * ascii
				 */
				final City city = new City(new UniqueGeoId(Integer.parseInt(code)), CharsetUtils
						.is(cityName, CharsetUtils.ISO_8859_1) ? cityName : reader.get(2), country);

				if (!StringUtils.empty(latitude) && !StringUtils.empty(longitude)) {
					try {
						final double lo = Double.parseDouble(longitude);
						final double la = Double.parseDouble(latitude);

						city.setCoordinates(new Coordinates(lo, la));
					} catch (final NumberFormatException ex) {
						log.warn("couldnt parse coordinates: " + latitude + ", " + longitude);
					}
				}

				if (countryCode.equals("GB") || countryCode.equals("US")) {
					final String state = reader.get(10);
					if (state != null && !"".equals(state)) {
						city.setStateCode(state);
					}
				}
				cities.put(code, city);
			}

		}

		return cities;
	}

	private static Map<String, CityAliasLanguage> getCountries() {
		final HashMap<String, CityAliasLanguage> countries = new HashMap<String, CityAliasLanguage>();
		final InputStream is = CityImportBean.class.getClassLoader()
				.getResourceAsStream("countryInfo.txt");

		final CsvReader reader = new CsvReader(is, Charset.forName("UTF-8"));
		reader.setDelimiter((char) Integer.parseInt("09", 16));
		reader.setComment('#');
		try {
			while (reader.readRecord()) {
				final String raw = reader.get(15);
				final int firstComma = raw.indexOf(',');
				final String langAndCountry = firstComma > -1 ? raw.substring(0, firstComma) : raw;
				final String lang = langAndCountry.indexOf('-') > -1 ? langAndCountry
						.substring(0, langAndCountry.indexOf('-')) : langAndCountry;

				final CityAliasLanguage cal = CityAliasLanguage.fromCode(lang.toLowerCase());

				if (cal != null) {
					countries.put(reader.get(0), cal);
				}
			}
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return countries;
	}

	public void setPersistentObjectService(final IPersistentObjectService persistentObjectService) {
		_persistentObjectService = persistentObjectService;
	}

}
