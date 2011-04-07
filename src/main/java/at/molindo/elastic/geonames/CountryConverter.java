package at.molindo.elastic.geonames;

import org.compass.core.converter.ConversionException;
import org.compass.core.converter.basic.AbstractBasicConverter;
import org.compass.core.mapping.ResourcePropertyMapping;
import org.compass.core.marshall.MarshallingContext;

import at.molindo.util.Country;

public class CountryConverter extends AbstractBasicConverter<Country> {

	@Override
	protected Country doFromString(final String str, final ResourcePropertyMapping resourcePropertyMapping, final MarshallingContext context) throws ConversionException {
		return Country.get(str);
	}

}
