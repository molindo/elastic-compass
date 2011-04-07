package at.molindo.setlist.model.venue;

import java.io.Serializable;

import org.compass.annotations.Index;
import org.compass.annotations.Searchable;
import org.compass.annotations.SearchableProperty;
import org.compass.annotations.Store;

@Searchable(root = false)
public class CityAlias implements Serializable {

	private static final long serialVersionUID = 1L;
	private CityAliasLanguage _lang;
	private String _name;

	protected CityAlias() {

	}

	public CityAlias(final String name, final CityAliasLanguage lang) {
		_name = name;
		_lang = lang;
	}

	@SearchableProperty(index = Index.NO, store = Store.YES)
	public CityAliasLanguage getLang() {
		return _lang;
	}

	@SearchableProperty(index = Index.NO, store = Store.YES)
	public String getName() {
		return _name;
	}

	protected void setLang(final CityAliasLanguage lang) {
		_lang = lang;
	}

	protected void setName(final String name) {
		_name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (_lang == null ? 0 : _lang.hashCode());
		result = prime * result + (_name == null ? 0 : _name.hashCode());
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
		if (!(obj instanceof CityAlias)) {
			return false;
		}
		final CityAlias other = (CityAlias) obj;
		if (_lang == null) {
			if (other._lang != null) {
				return false;
			}
		} else if (!_lang.equals(other._lang)) {
			return false;
		}
		if (_name == null) {
			if (other._name != null) {
				return false;
			}
		} else if (!_name.equals(other._name)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + getName() + "; lang: " + getLang();
	}

}
