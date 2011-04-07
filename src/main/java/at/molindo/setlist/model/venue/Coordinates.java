package at.molindo.setlist.model.venue;

import java.io.Serializable;

import org.compass.annotations.Index;
import org.compass.annotations.Searchable;
import org.compass.annotations.SearchableId;
import org.compass.annotations.SearchableProperty;
import org.compass.annotations.Store;

@Searchable(root = false)
public class Coordinates implements Serializable {

	private static final long serialVersionUID = 1L;
	private Double _longitude;
	private Double _latitude;

	protected Coordinates() {

	}

	@SearchableId
	public String getSearchableId() {
		if (_longitude == null && _latitude == null) {
			return Double.toString(-1) + "/" + Double.toString(-1);
		}
		return Double.toString(_longitude) + "/" + Double.toString(_latitude);
	}

	public Coordinates(final Double longitute, final Double latitude) {
		_longitude = longitute;
		_latitude = latitude;
	}

	@SearchableProperty(index = Index.NO, store = Store.YES)
	public Double getLongitude() {
		return _longitude;
	}

	@SearchableProperty(index = Index.NO, store = Store.YES)
	public Double getLatitude() {
		return _latitude;
	}

	public void setLongitude(final Double longitude) {
		_longitude = longitude;
	}

	public void setLatitude(final Double latitude) {
		_latitude = latitude;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(_latitude);
		result = prime * result + (int) (temp ^ temp >>> 32);
		temp = Double.doubleToLongBits(_longitude);
		result = prime * result + (int) (temp ^ temp >>> 32);
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
		if (!(obj instanceof Coordinates)) {
			return false;
		}
		final Coordinates other = (Coordinates) obj;
		if (Double.doubleToLongBits(_latitude) != Double.doubleToLongBits(other._latitude)) {
			return false;
		}
		if (Double.doubleToLongBits(_longitude) != Double.doubleToLongBits(other._longitude)) {
			return false;
		}
		return true;
	}

}
