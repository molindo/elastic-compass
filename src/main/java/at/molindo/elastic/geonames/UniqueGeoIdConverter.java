package at.molindo.elastic.geonames;

import org.compass.core.Property;
import org.compass.core.Resource;
import org.compass.core.converter.ConversionException;
import org.compass.core.converter.mapping.osem.ComponentMappingConverter;
import org.compass.core.mapping.Mapping;
import org.compass.core.marshall.MarshallingContext;

import at.molindo.setlist.model.venue.missing.UniqueGeoId;

public class UniqueGeoIdConverter extends ComponentMappingConverter {

	@Override
	public UniqueGeoId unmarshall(final Resource resource, final Mapping mapping, final MarshallingContext context) throws ConversionException {
		// TODO Auto-generated method stub
		final Property p = resource.getProperty("$/missingCity/geoId/uniqueId");
		final String id = p.getStringValue();
		final UniqueGeoId g = new UniqueGeoId();
		g.setUniqueId(id);

		return g;
	}

}
