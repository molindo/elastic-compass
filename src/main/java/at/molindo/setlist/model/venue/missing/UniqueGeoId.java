package at.molindo.setlist.model.venue.missing;

import java.io.Serializable;

import org.compass.annotations.Searchable;
import org.compass.annotations.SearchableId;

@Searchable(root = false)
public class UniqueGeoId implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String GEONAMES_PREFIX = "";
	private static final String CUSTOM_PREFIX = "cu:";

	private String _uniqueId;

	public UniqueGeoId() {

	}

	public UniqueGeoId(final Integer geonameId) {
		_uniqueId = GEONAMES_PREFIX + geonameId.toString();
	}

	public UniqueGeoId(final String customId) {
		if (customId == null || "".equals(customId)) {
			throw new NullPointerException();
		}
		_uniqueId = CUSTOM_PREFIX + customId;
	}

	public static boolean isCustomId(final String id) {
		return id != null && id.startsWith(CUSTOM_PREFIX);
	}

	public boolean isCustomId() {
		return isCustomId(getUniqueId());
	}

	@SearchableId
	public String getUniqueId() {
		return _uniqueId;
	}

	public void setUniqueId(final String uniqueId) {
		_uniqueId = uniqueId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (_uniqueId == null ? 0 : _uniqueId.hashCode());
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
		if (!(obj instanceof UniqueGeoId)) {
			return false;
		}
		final UniqueGeoId other = (UniqueGeoId) obj;
		if (_uniqueId == null) {
			if (other._uniqueId != null) {
				return false;
			}
		} else if (!_uniqueId.equals(other._uniqueId)) {
			return false;
		}
		return true;
	}

}
