package at.molindo.setlist.model.venue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum CityAliasLanguage {

	GERMAN("de"),
	ENGLISH("en"),
	FRENCH("fr"),
	SPANISH("es"),
	TURKISH("tr"),
	ITALIAN("it"),
	PORTUGUESE("pt"),
	POLISH("pl");

	private final String _code;
	private Locale _locale;

	public static final CityAliasLanguage DEFAULT_LANG = CityAliasLanguage.ENGLISH;

	private CityAliasLanguage(final String code) {
		_code = code;
		_locale = new Locale(code);
	}

	public String code() {
		return _code;
	}

	public Locale locale() {
		return _locale;
	}

	private static final Map<String, CityAliasLanguage> CODE2LANG;

	static {
		final Map<String, CityAliasLanguage> map = new HashMap<String, CityAliasLanguage>();
		map.put("de", GERMAN);
		map.put("en", ENGLISH);
		map.put("fr", FRENCH);
		map.put("es", SPANISH);
		map.put("tr", TURKISH);
		map.put("it", ITALIAN);
		map.put("pt", PORTUGUESE);
		map.put("pl", POLISH);

		CODE2LANG = Collections.unmodifiableMap(map);
	}

	public static CityAliasLanguage fromCode(final String code) {
		if (code == null) {
			return null;
		}
		return CODE2LANG.get(code.toLowerCase());
	}

}
