package at.molindo.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class Country implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * use {@link NULL} instead to store in DB. Null uses a non-null country
	 * code and therefore is also suited for DB unique keys (where null values
	 * are a bad choice)
	 * 
	 * TODO replace NONE with NULL and check code for country.getCode() == null
	 */
	public static final Country NONE = new Country(null);

	public static final Country NULL = new Country("--");

	private static final List<Country> COUNTRIES = initCountries();
	private static final Map<String, Country> CODE_2_COUNTRY = initCode2Country(COUNTRIES);
	private static final Set<Country> EUROPE = initEurope();
	private static final Set<Country> GERMAN_SPEAKING = initGermanSpeaking();

	public static final Country AUSTRIA = get("AT");
	public static final Country SWITZERLAND = get("CH");
	public static final Country GERMANY = get("DE");
	public static final Country CANADA = get("CA");
	public static final Country AUSTRALIA = get("AU");
	public static final Country GB = get("GB");
	public static final Country US = get("US");
	public static final Country BRAZIL = get("BR");
	public static final Country MEXICO = get("MX");

	private static final Map<Locale, List<Country>> _sortedLocaleCountryList = new HashMap<Locale, List<Country>>();

	public static List<Country> getAvailableCountries() {
		return COUNTRIES;
	}

	public static Country get(String code) {
		if (code == null) {
			return NONE;
		}
		if (code.equals(NULL.getCode())) {
			return NULL;
		}
		code = code.toUpperCase();
		final Country c = CODE_2_COUNTRY.get(code);
		return c != null ? c : Country.NONE;
	}

	public static List<Country> getCountryList(final Locale l) {
		final List<Country> returnList = _sortedLocaleCountryList.get(l);

		if (returnList != null) {
			return returnList;
		}

		final List<Country> all = new ArrayList<Country>(Country.getAvailableCountries());
		Collections.sort(all, new Comparator<Country>() {

			public int compare(final Country o1, final Country o2) {

				return replaceUmlauts(o1.getDisplayName(l)).compareToIgnoreCase(replaceUmlauts(o2
						.getDisplayName(l)));
			}

		});
		_sortedLocaleCountryList.put(l, Collections.unmodifiableList(all));

		return all;
	}

	final static String replaceUmlauts(String s) {

		final char first = s.charAt(0);
		final Character replace;

		switch (first) {
		case '\u00c4':
			replace = 'A';
			break;

		case '\u00d6':
			replace = 'O';
			break;

		case '\u00dc':
			replace = 'U';
			break;

		case '\u00e4':
			replace = 'a';
			break;

		case '\u00f6':
			replace = 'o';
			break;

		case '\u00fc':
			replace = 'u';
			break;

		default:
			replace = null;
			break;
		}

		if (replace != null) {
			s = s.replace(first, replace.charValue());
		}
		return s;
	}

	private static List<Country> initCountries() {
		final String[] codes = Locale.getISOCountries();
		final Country[] countries = new Country[codes.length];
		for (int i = 0; i < codes.length; i++) {
			countries[i] = new Country(codes[i]);
		}
		return Collections.unmodifiableList(Arrays.asList(countries));
	}

	private static Map<String, Country> initCode2Country(final List<Country> countries) {
		final HashMap<String, Country> map = new HashMap<String, Country>();
		for (final Country c : countries) {
			map.put(c.getCode().toUpperCase(), c);
		}
		return Collections.unmodifiableMap(map);
	}

	private static void addIfExists(final Set<Country> set, final String[] codes) {
		for (int i = 0; i < codes.length; i++) {
			final Country c = get(codes[i]);

			if (c != NULL && c != NONE) {
				set.add(c);
			}
		}
	}

	private static Set<Country> initEurope() {

		final Set<Country> set = new HashSet<Country>();
		/* Albania, Andorra, Armenia, Austria, Azerbaijan */
		addIfExists(set, new String[] { "AL", "AD", "AM", "AT", "AZ" });
		/* Belarus, Belgium, Bosnia and Herzegovina, Bulgaria, Croatia */
		addIfExists(set, new String[] { "BY", "BE", "BA", "BG", "HR" });
		/* Cyprus, Czech Republic, Denmark, Estonia, Finland */
		addIfExists(set, new String[] { "CY", "CZ", "DK", "EE", "FI" });
		/* France, Georgia, Germany, Greece, Hungary */
		addIfExists(set, new String[] { "FR", "GE", "DE", "GR", "HU" });
		/* Iceland, Ireland, Italy, Kazahkstan, Latvia */
		addIfExists(set, new String[] { "IS", "IE", "IT", "KZ", "LV" });
		/* Liechtenstein, Lithuania, Luxembourg, Macedonia, Malta */
		addIfExists(set, new String[] { "LI", "LT", "LU", "MK", "MT" });
		/* Moldova, Monaco, Montenegro, Netherlands, Norway */
		addIfExists(set, new String[] { "MD", "MC", "ME", "NL", "NO" });
		/* Poland, Portugal, Romania, Russia, San Marino */
		addIfExists(set, new String[] { "PL", "PT", "RO", "RU", "SM" });
		/* Serbia, Slovakia, Slovenia, Spain, Sweden */
		addIfExists(set, new String[] { "RS", "SK", "SI", "ES", "SE" });
		/* Switzerland, Turkey, Ukraine, United Kingdom, Vatican City */
		addIfExists(set, new String[] { "CH", "TR", "UA", "GB", "VA" });

		/* non-sovereign territories */
		/* Atland, Faroe Islands, Gibraltar, Guernsey, Isle of Man, Jersey */
		addIfExists(set, new String[] { "AX", "FO", "GI", "GG", "IM", "JE" });

		return Collections.unmodifiableSet(set);
	}

	private static Set<Country> initGermanSpeaking() {
		final Set<Country> set = new HashSet<Country>();
		/* Austria, Germany, Switzerland, Liechtenstein, Luxembourg */
		addIfExists(set, new String[] { "AT", "DE", "CH", "LI", "LU" });

		return Collections.unmodifiableSet(set);
	}

	public static boolean isEurope(final Country c) {
		return EUROPE.contains(c);
	}

	public static boolean isGermanSpeaking(final Country c) {
		return GERMAN_SPEAKING.contains(c);
	}

	public static boolean empty(final Country c) {
		return c == null || Country.NULL.equals(c) || Country.NONE.equals(c);
	}

	private String _code;

	/**
	 * intended for hibernate only
	 */
	protected Country() {
	}

	private Country(final String iso2Code) {
		_code = iso2Code;
	}

	/**
	 * intended for hibernate only
	 * 
	 * @param code
	 */

	protected void setCode(final String code) {
		_code = code;
	}

	public String getCode() {
		return _code;
	}

	public String getDisplayName(final Locale l) {
		// first argument is not used, but must not be null
		return new Locale("xx", _code).getDisplayCountry(l);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (_code == null ? 0 : _code.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Country other = (Country) obj;
		if (_code == null) {
			if (other._code != null) {
				return false;
			}
		} else if (!_code.equals(other._code)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return getCode();
	}

}
