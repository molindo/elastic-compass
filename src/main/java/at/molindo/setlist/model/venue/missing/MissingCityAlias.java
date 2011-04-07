package at.molindo.setlist.model.venue.missing;

import java.io.Serializable;

import org.compass.annotations.Index;
import org.compass.annotations.Searchable;
import org.compass.annotations.SearchableProperty;
import org.compass.annotations.Store;

@Searchable(root = false)
public class MissingCityAlias implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer _alternateNameId; // the id of this alternate name, int
	private Integer _geonameId; // geonameId referring to id in table 'geoname',
	// int
	private String _isoLanguage; // iso 639 language code 2- or 3-characters;
	// 4-characters 'post' for postal codes and
	// 'iata' or 'icao' for airport codes, fr-1793
	// for French Revolution names, varchar(7)
	private String _alternateName; // alternate name or name variant,
	// varchar(200)
	private boolean _isPreferredName; // '1', if this alternate name is an
	// official/preferred name
	private boolean _isShortName; // '1', if this is a short name like

	// 'California' for 'State of California'

	public Integer getAlternateNameId() {
		return _alternateNameId;
	}

	@SearchableProperty(index = Index.NO, store = Store.YES)
	public Integer getGeonameId() {
		return _geonameId;
	}

	@SearchableProperty(index = Index.NO, store = Store.YES)
	public String getIsoLanguage() {
		return _isoLanguage;
	}

	@SearchableProperty
	public String getAlternateName() {
		return _alternateName;
	}

	@SearchableProperty(index = Index.NO, store = Store.YES)
	public boolean isPreferredName() {
		return _isPreferredName;
	}

	@SearchableProperty(index = Index.NO, store = Store.YES)
	public boolean isShortName() {
		return _isShortName;
	}

	public void setAlternateNameId(final Integer alternateNameId) {
		this._alternateNameId = alternateNameId;
	}

	public void setGeonameId(final Integer geonameId) {
		this._geonameId = geonameId;
	}

	public void setIsoLanguage(final String isoLanguage) {
		this._isoLanguage = isoLanguage;
	}

	public void setAlternateName(final String alternateName) {
		this._alternateName = alternateName;
	}

	public void setPreferredName(final boolean isPreferredName) {
		this._isPreferredName = isPreferredName;
	}

	public void setShortName(final boolean isShortName) {
		this._isShortName = isShortName;
	}

}
