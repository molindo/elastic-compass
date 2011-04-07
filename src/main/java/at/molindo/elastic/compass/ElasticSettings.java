package at.molindo.elastic.compass;

import org.compass.core.config.CompassEnvironment;
import org.compass.core.config.CompassSettings;

public class ElasticSettings {

	private String _aliasName;

	public ElasticSettings() {

	}

	public ElasticSettings(CompassSettings settings) {
		_aliasName = settings.getSetting(CompassEnvironment.CONNECTION_SUB_CONTEXT, "index");
	}

	public String getAliasName() {
		return _aliasName;
	}
	
}
