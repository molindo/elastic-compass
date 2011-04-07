package at.molindo.elastic.compass;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public enum ElasticType {
	// TODO finish
	STRING("string", "store", "index"),
	
	NUMBER, DATE, BOOLEAN, BINARY; 
	
	private final String _name;
	private final Set<String> _properties;
	
	private ElasticType() {
		_name = name().toLowerCase();
		_properties = Collections.emptySet();
	}
	
	private ElasticType(String name, String ... properties) {
		_name = name().toLowerCase();
		_properties = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(properties)));
	}

	public String getName() {
		return _name;
	}
	
	public boolean isPropertySupported(String property) {
		return _properties.contains(property);
	}

	@Override
	public String toString() {
		return getName();
	}
	
	
}
