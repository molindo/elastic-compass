package at.molindo.setlist.model.venue.missing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.compass.annotations.ExcludeFromAll;
import org.compass.annotations.Index;
import org.compass.annotations.Searchable;
import org.compass.annotations.SearchableComponent;
import org.compass.annotations.SearchableIdComponent;
import org.compass.annotations.SearchableProperty;
import org.compass.annotations.SpellCheck;
import org.compass.annotations.Store;

import at.molindo.model.IPersistentObject;
import at.molindo.setlist.model.venue.CityAliasLanguage;
import at.molindo.setlist.util.VenueUtil;
import at.molindo.util.Country;
import at.molindo.util.hibernate.CollationUtil;

/**
 * implements IPersistentObject just for search purposes
 * 
 * 
 */
@Searchable(alias = MissingCity.SEARCH_ALIAS, spellCheck = SpellCheck.EXCLUDE)
public class MissingCity implements Serializable, IPersistentObject {

	private static final long serialVersionUID = 1L;
	public static final String SEARCH_ALIAS = "missingCity";
	public static final String SEARCH_COUNTRY = "countryCode";
	public static final String SEARCH_FEATURE = "countryCode";

	private Integer _geonameId; // integer id of record in geonames database
	private String _name; // name of geographical point (utf8) varchar(200)
	private String _asciiname; // name of geographical point in plain ascii
	// characters, varchar(200)
	private List<MissingCityAlias> _aliases;
	private Double _latitude; // latitude in decimal degrees (wgs84)
	private Double _longitude; // longitude in decimal degrees (wgs84)
	private Character _featureClass; // see
	// http://www.geonames.org/export/codes.html,
	// char(1)
	private String _featureCode; // see
	// http://www.geonames.org/export/codes.html
	// , varchar(10)
	private String _countryCode; // ISO-3166 2-letter country code, 2 characters
	private String _cc2; // alternate country codes, comma separated, ISO-3166
	// 2-letter country code, 60 characters
	private String _admin1Code;
	private String _admin1Name;
	// fipscode (subject to change to iso code),
	// isocode for the us and ch, see file
	// admin1Codes.txt for display names of this
	// code; varchar(20)
	private String _admin2Code; // code for the second administrative division,
	// a county in the US, see file admin2Codes.txt;
	// varchar(80)
	private String _admin3Code; // code for third level administrative division,
	// varchar(20)
	private String _admin4Code; // code for fourth level administrative
	// division, varchar(20)
	private Integer _population; // bigint (4 byte int)
	private Integer _elevation; // in meters, integer
	private Integer _gtopo30; // average elevation of 30'x30' (ca 900mx900m)
	// area in
	// meters, integer
	private String _timezone; // the timezone id (see file timeZone.txt)
	private Date _modificationDate; // date of last modification in yyyy-MM-dd

	private UniqueGeoId _uniqueGeoId;

	public String getId() {
		return getGeoId().getUniqueId();
	}

	protected MissingCity() {

	}

	public MissingCity(final UniqueGeoId id) {
		_uniqueGeoId = id;
	}

	// format

	@SearchableIdComponent(converter = "geoIdConverter")
	public UniqueGeoId getGeoId() {
		return _uniqueGeoId;
	}

	public void setGeoId(final UniqueGeoId id) {
		_uniqueGeoId = id;
	}

	protected Integer getGeonameId() {
		return _geonameId;
	}

	@SearchableProperty
	public String getName() {
		return _name;
	}

	@SearchableProperty
	public String getAsciiname() {
		return _asciiname;
	}

	@SearchableProperty(index = Index.NO, store = Store.YES)
	public Double getLatitude() {
		return _latitude;
	}

	@SearchableProperty(index = Index.NO, store = Store.YES)
	public Double getLongitude() {
		return _longitude;
	}

	@SearchableProperty(name = SEARCH_FEATURE, excludeFromAll = ExcludeFromAll.YES, index = Index.NOT_ANALYZED)
	public Character getFeatureClass() {
		return _featureClass;
	}

	@SearchableProperty(index = Index.NO, store = Store.YES)
	public String getFeatureCode() {
		return _featureCode;
	}

	@SearchableProperty(store = Store.NO, spellCheck = SpellCheck.EXCLUDE)
	public List<String> getCountryNames() {

		final Country c = Country.get(getCountryCode());
		if (!Country.NONE.equals(c) && !Country.NULL.equals(c)) {
			final Set<String> set = new HashSet<String>();
			if (getAdmin1Name() != null) {
				set.add(getAdmin1Name());
			}
			if (VenueUtil.isStateCodeUsingCountry(Country.get(getCountryCode()))) {
				set.add(getAdmin1Code());
			}
			if (Country.GB.equals(c)) {
				set.add(VenueUtil.getUKCountryNameForAbbreviation(getAdmin1Code()));
			}
			for (final CityAliasLanguage lang : CityAliasLanguage.values()) {
				final String name = c.getDisplayName(new Locale(lang.code()));
				if (name != null && !"".equals(name.trim())) {
					set.add(name);
				}
			}
			return new ArrayList<String>(set);
		}
		return Collections.emptyList();
	}

	@SearchableProperty(name = SEARCH_COUNTRY, excludeFromAll = ExcludeFromAll.YES, index = Index.NOT_ANALYZED)
	public String getCountryCode() {
		return _countryCode;
	}

	@SearchableProperty(index = Index.NO, store = Store.YES)
	public String getCc2() {
		return _cc2;
	}

	@SearchableProperty(index = Index.NO, store = Store.YES)
	public String getAdmin1Code() {
		return _admin1Code;
	}

	@SearchableProperty(index = Index.NO, store = Store.YES)
	public String getAdmin2Code() {
		return _admin2Code;
	}

	@SearchableProperty(index = Index.NO, store = Store.YES)
	public String getAdmin3Code() {
		return _admin3Code;
	}

	@SearchableProperty(index = Index.NO, store = Store.YES)
	public String getAdmin4Code() {
		return _admin4Code;
	}

	@SearchableProperty(index = Index.NO, store = Store.YES)
	public Integer getPopulation() {
		return _population;
	}

	@SearchableProperty(index = Index.NO, store = Store.YES)
	public Integer getElevation() {
		return _elevation;
	}

	@SearchableProperty(index = Index.NO, store = Store.YES)
	public Integer getGtopo30() {
		return _gtopo30;
	}

	@SearchableProperty(index = Index.NO, store = Store.YES)
	public String getTimezone() {
		return _timezone;
	}

	@SearchableProperty(index = Index.NO, store = Store.YES)
	public Date getModificationDate() {
		return _modificationDate;
	}

	public void setGeonameId(final Integer geonameId) {
		_geonameId = geonameId;
	}

	public void setName(final String name) {
		_name = name;
	}

	public void setAsciiname(final String asciiname) {
		_asciiname = asciiname;
	}

	public void setLatitude(final Double latitude) {
		_latitude = latitude;
	}

	public void setLongitude(final Double longitude) {
		_longitude = longitude;
	}

	public void setFeatureClass(final Character featureClass) {
		_featureClass = featureClass;
	}

	public void setFeatureCode(final String featureCode) {
		_featureCode = featureCode;
	}

	public void setCountryCode(final String countryCode) {
		_countryCode = countryCode;
	}

	public void setCc2(final String cc2) {
		_cc2 = cc2;
	}

	public void setAdmin1Code(final String admin1Code) {
		_admin1Code = admin1Code;
	}

	public void setAdmin2Code(final String admin2Code) {
		_admin2Code = admin2Code;
	}

	public void setAdmin3Code(final String admin3Code) {
		_admin3Code = admin3Code;
	}

	public void setAdmin4Code(final String admin4Code) {
		_admin4Code = admin4Code;
	}

	public void setPopulation(final Integer population) {
		_population = population;
	}

	public void setElevation(final Integer elevation) {
		_elevation = elevation;
	}

	public void setGtopo30(final Integer gtopo30) {
		_gtopo30 = gtopo30;
	}

	public void setTimezone(final String timezone) {
		_timezone = timezone;
	}

	public void setModificationDate(final Date modificationDate) {
		_modificationDate = modificationDate;
	}

	@SearchableComponent
	public List<MissingCityAlias> getAliases() {
		return _aliases;
	}

	public void setAliases(final List<MissingCityAlias> aliases) {
		_aliases = aliases;
	}

	@SearchableProperty(index = Index.NO, store = Store.YES)
	public String getAdmin1Name() {
		return _admin1Name;
	}

	public void setAdmin1Name(final String admin1Name) {
		_admin1Name = admin1Name;
	}

	public boolean hasNameOrAlias(final String venueName) {
		if (venueName == null) {
			return false;
		}

		if (getName() != null) {
			if (CollationUtil.equalsIgnoreAccents(venueName, getName())) {
				return true;
			}
		}

		if (getAliases() != null) {
			for (final MissingCityAlias alias : getAliases()) {
				if (CollationUtil.equalsIgnoreAccents(alias.getAlternateName(), venueName)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "name: " + getName() + "; admin1code: " + getAdmin1Code();
	}

	public String getDisplayString() {
		final Country c = Country.get(getCountryCode());

		if (c == null || Country.NULL.equals(c) || Country.NONE.equals(c)) {
			return getName();
		}

		if (Country.US.equals(c)) {
			return getName() + ", " + getAdmin1Code();
		}

		if (getAdmin1Name() != null) {
			return getName() + ", " + getAdmin1Name() + ", " + c.getDisplayName(Locale.ENGLISH);
		}
		return getName() + ", " + c.getDisplayName(Locale.ENGLISH);
	}

}
