package at.molindo.setlist.model.venue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.compass.annotations.Index;
import org.compass.annotations.Searchable;
import org.compass.annotations.SearchableComponent;
import org.compass.annotations.SearchableId;
import org.compass.annotations.SearchableProperty;
import org.compass.annotations.SpellCheck;
import org.compass.annotations.Store;

import at.molindo.model.AbstractPersistentObject;
import at.molindo.setlist.model.venue.missing.UniqueGeoId;
import at.molindo.setlist.util.VenueUtil;
import at.molindo.util.Country;
import at.molindo.util.hibernate.CollationUtil;

@Searchable(alias = City.SEARCH_ALIAS, spellCheck = SpellCheck.EXCLUDE)
public class City extends AbstractPersistentObject {

	public static final String SEARCH_ALIAS = "city";

	public static final String UNKNOWN_CITY_NAME = "";

	public static final String UNKNOWN_CITY_DISPLAY_NAME = "Unknown City";

	public static final String SEARCH_COUNTRY_ALIAS = "country";

	private Integer _id;

	public static City getReplacement(City city) {
		if (city == null) {
			return null;
		}
		if (city.isArchived()) {
			// avoid circular replacements by setting a limit to
			// 200
			int i = 0;
			do {
				city = city.getReplacement();
			} while (city.isArchived() && ++i < 200);
		}
		return city;
	}

	@SearchableComponent
	public Coordinates getCoordinates() {
		return _coordinates;
	}

	public void setCoordinates(final Coordinates coordinates) {
		_coordinates = coordinates;
	}

	private String _importName;
	private String _stateCode;
	private String _stateName;
	private Country _country;
	private Coordinates _coordinates;
	private String _displayName;
	private Set<CityAlias> _aliases;
	private Set<String> _aliasNames;
	private UniqueGeoId _geoId;
	private Date _updated;
	private City _metropolis;
	private City _replacement;

	protected City() {
		_aliases = new HashSet<CityAlias>();
		_coordinates = new Coordinates();
	}

	public City(final Date updated) {
		this();
		_updated = updated;
	}

	public City(final UniqueGeoId geoId, final String importName, final Country country) {
		if (geoId == null || importName == null || country == null || country.getCode() == null) {
			throw new NullPointerException("neither geonameId nor importName nor country can be null!");
		}
		_importName = importName;
		_country = country;
		_aliases = new HashSet<CityAlias>();
		_geoId = geoId;
		_updated = new Date();
	}

	public static City newUnknownCity(final Country country) {
		/*
		 * unknown cities don't have a geonameId
		 */
		final City city = new City();
		city.setImportName(UNKNOWN_CITY_NAME);
		city.setCountry(country);
		city.setUpdated(new Date());
		return city;
	}

	@SearchableId
	public Integer getId() {
		return _id;
	}

	public String getName() {
		return _displayName == null ? _importName : _displayName;
	}

	@SearchableProperty(store = Store.YES, index = Index.NO, converter = "countryConverter", name = "countryStored")
	public Country getCountry() {
		return _country;
	}

	@SearchableProperty(store = Store.NO, name = SEARCH_ALIAS)
	public List<String> getNames() {

		final List<String> metropolis;
		if (_metropolis != null) {
			metropolis = _metropolis.getNames();
		} else {
			metropolis = Collections.emptyList();
		}

		final ArrayList<String> cityNames = new ArrayList<String>(metropolis.size() + 2
				+ (getAliases() != null ? getAliases().size() : 0));
		if (getDisplayName() != null) {
			cityNames.add(getDisplayName());
		}
		if (getImportName() != null) {
			cityNames.add(getImportName());
		}
		if (isUnknownCity()) {
			cityNames.add(UNKNOWN_CITY_DISPLAY_NAME);
		}
		cityNames.addAll(getAliasNames());

		final List<String> countryNames = getCountryNames();
		if (countryNames.size() == 0) {
			cityNames.addAll(metropolis);
			return cityNames;
		}

		final ArrayList<String> mergedNames = new ArrayList<String>(metropolis.size()
				+ cityNames.size() * countryNames.size());
		for (final String city : cityNames) {
			for (final String country : countryNames) {
				mergedNames.add(city + " " + country);
			}
		}
		mergedNames.addAll(metropolis);
		return mergedNames;
	}

	@SearchableProperty(store = Store.NO, name = SEARCH_COUNTRY_ALIAS)
	public List<String> getCountryNames() {
		final StringBuilder buf = new StringBuilder();

		if (getStateName() != null) {
			buf.append(getStateName()).append(" ");
		}
		if (VenueUtil.isStateCodeUsingCountry(getCountry())) {
			buf.append(getStateCode()).append(" ");
		}
		if (Country.GB.equals(getCountry())) {
			buf.append(VenueUtil.getUKCountryNameForAbbreviation(getStateCode())).append(" ");
		}

		final Set<String> set = new HashSet<String>();
		final int length = buf.length();
		if (!Country.NONE.equals(getCountry()) && !Country.NULL.equals(getCountry())) {
			for (final CityAliasLanguage lang : CityAliasLanguage.values()) {
				String name = getCountry().getDisplayName(lang.locale());
				if (name != null && !"".equals((name = name.trim()))) {
					if (length > 0) {
						set.add(buf.append(name).toString());
						buf.setLength(length);
					} else {
						set.add(name);
					}
				}
			}
		}
		if (set.size() == 0 && buf.length() > 0) {
			set.add(buf.toString());
		}
		return new ArrayList<String>(set);
	}

	protected Set<String> getAliasNames() {
		if (_aliasNames == null) {
			_aliasNames = new HashSet<String>();
			if (getAliases() != null) {
				for (final CityAlias ca : getAliases()) {
					_aliasNames.add(ca.getName());
				}
			}
		}
		return _aliasNames;
	}

	@SearchableComponent
	public Set<CityAlias> getAliases() {
		return _aliases;
	}

	public void setId(final Integer id) {
		_id = id;
	}

	public void setCountry(final Country country) {
		_country = country;
	}

	public void setAliases(final Set<CityAlias> aliases) {
		_aliases = aliases;
		_aliasNames = null;
	}

	@SearchableProperty(store = Store.YES, index = Index.NO)
	public String getImportName() {
		return _importName;
	}

	public void setImportName(final String importName) {
		_importName = importName;
	}

	@SearchableProperty(store = Store.YES, index = Index.NO)
	public String getDisplayName() {
		return _displayName;
	}

	public void setDisplayName(final String displayName) {
		if (displayName == null || !displayName.equals(getImportName())) {
			_displayName = displayName;
		}
	}

	public boolean hasNameOrAlias(final String venueName) {
		if (venueName == null) {
			return false;
		}

		if (_importName != null) {
			if (CollationUtil.equalsIgnoreAccents(venueName, _importName)) {
				return true;
			}
		}

		if (_displayName != null) {
			if (CollationUtil.equalsIgnoreAccents(venueName, _displayName)) {
				return true;
			}
		}
		return getAliasNames().contains(venueName);
	}

	public String getDisplayString() {
		return CityNameFormat.FULL.getString(this, Locale.ENGLISH);
	}

	public String getLocalizedName(final Locale l) {
		if (l == null || CityAliasLanguage.ENGLISH.code().equals(l.getLanguage())) {
			return getDisplayName() != null ? getDisplayName() : getImportName();
		}
		for (final CityAlias ca : getAliases()) {
			if (ca.getLang().code().equals(l.getLanguage())) {
				return ca.getName();
			}
		}
		return getImportName();
	}

	@Override
	public String toString() {
		return getName() + "; "
				+ (_displayName != null ? " (imported as \"" + _importName + "\"; " : "")
				+ _country + "; aliases: " + getAliases();
	}

	public UniqueGeoId getGeoId() {
		return _geoId;
	}

	protected void setGeoId(final UniqueGeoId geoId) {
		_geoId = geoId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 0;
		result = prime * result + (getCountry() == null ? 0 : getCountry().hashCode());
		result = prime * result + (getDisplayName() == null ? 0 : getDisplayName().hashCode());
		result = prime * result + (getGeoId() == null ? 0 : getGeoId().hashCode());
		result = prime * result + (getImportName() == null ? 0 : getImportName().hashCode());
		result = prime * result + (getStateCode() == null ? 0 : getStateCode().hashCode());
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
		if (!(obj instanceof City)) {
			return false;
		}
		final City other = (City) obj;
		if (getCountry() == null) {
			if (other.getCountry() != null) {
				return false;
			}
		} else if (!getCountry().equals(other.getCountry())) {
			return false;
		}
		if (getDisplayName() == null) {
			if (other.getDisplayName() != null) {
				return false;
			}
		} else if (!getDisplayName().equals(other.getDisplayName())) {
			return false;
		}
		if (getGeoId() == null) {
			if (other.getGeoId() != null) {
				return false;
			}
		} else if (!getGeoId().equals(other.getGeoId())) {
			return false;
		}
		if (getImportName() == null) {
			if (other.getImportName() != null) {
				return false;
			}
		} else if (!getImportName().equals(other.getImportName())) {
			return false;
		}
		if (getStateCode() == null) {
			if (other.getStateCode() != null) {
				return false;
			}
		} else if (!getStateCode().equals(other.getStateCode())) {
			return false;
		}
		return true;
	}

	public boolean isUnknownCity() {
		final String name = getName();
		return name == null || UNKNOWN_CITY_NAME.equals(name);
	}

	@SearchableProperty(store = Store.YES, index = Index.NO)
	public String getStateCode() {
		return _stateCode;
	}

	@SearchableProperty(store = Store.YES, index = Index.NO)
	public String getStateName() {
		return _stateName;
	}

	public void setStateCode(final String stateCode) {
		_stateCode = stateCode;
	}

	public void setStateName(final String stateName) {
		_stateName = stateName;
	}

	public Date getUpdated() {
		return _updated;
	}

	public void setUpdated(final Date updated) {
		_updated = updated;
	}

	public City getMetropolis() {
		return _metropolis;
	}

	public void setMetropolis(final City metropolis) {
		_metropolis = metropolis;
	}

	public City getReplacement() {
		return _replacement;
	}

	public void setReplacement(final City replacement) {
		_replacement = replacement;
	}

	public boolean isArchived() {
		return _replacement != null;
	}

}
