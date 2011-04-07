package at.molindo.elastic.compass;

import org.compass.core.Property;
import org.compass.core.converter.mapping.ResourcePropertyConverter;
import org.compass.core.mapping.ResourcePropertyMapping;

public class ElasticProperty implements Property {

	private static final long serialVersionUID = 6784513899681532880L;

	private final ElasticField _field;
	private ResourcePropertyMapping _mapping;

	public ElasticProperty(ElasticField field) {
		if (field == null) {
			throw new NullPointerException("field");
		}
		_field = field;
	}

	@Override
	public String getName() {
		return _field.getName();
	}

	@Override
	public String getStringValue() {
		return _field.getStringValue();
	}

	@Override
	public Object getObjectValue() {
		String value = getStringValue();
		if (_mapping == null) {
			return value;
		}
		ResourcePropertyConverter<?> converter = _mapping.getResourcePropertyConverter();
		if (converter == null) {
			return null;
		}
		return converter.fromString(value, _mapping);
	}

	@Override
	public byte[] getBinaryValue() {
		return _field.getBinaryValue();
	}

	@Override
	public float getBoost() {
		return _field.getBoost();
	}

	@Override
	public void setBoost(float boost) {
		_field.setBoost(boost);
	}

	@Override
	public boolean isIndexed() {
		return _field.isIndexed();
	}

	@Override
	public boolean isStored() {
		return _field.isStored();
	}

	@Override
	public boolean isCompressed() {
		return _field.isCompressed();
	}

	@Override
	public boolean isTokenized() {
		return _field.isTokenized();
	}

	@Override
	public boolean isTermVectorStored() {
		return _field.isTermVectorStored();
	}

	@Override
	public boolean isBinary() {
		return _field.isBinary();
	}

	@Override
	public boolean isOmitNorms() {
		return _field.isOmitNorms();
	}

	@Override
	public void setOmitNorms(boolean omitNorms) {
		_field.setOmitNorms(omitNorms);
	}

	@Override
	public boolean isOmitTf() {
		return _field.isOmitTf();
	}

	@Override
	public void setOmitTf(boolean omitTf) {
		_field.setOmitTf(omitTf);
	}

	public void setPropertyMapping(ResourcePropertyMapping mapping) {
		_mapping = mapping;
	}

	@Override
	public String toString() {
		return "ElasticProperty [field=" + _field + "]";
	}

	
}
