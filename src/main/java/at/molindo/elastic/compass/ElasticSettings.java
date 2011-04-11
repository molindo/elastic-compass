/**
 * Copyright 2011 Molindo GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.molindo.elastic.compass;

import org.compass.core.config.CompassEnvironment;
import org.compass.core.config.CompassSettings;
import org.compass.core.config.ConfigurationException;
import org.elasticsearch.index.query.xcontent.QueryStringQueryBuilder.Operator;

public class ElasticSettings {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(ElasticSettings.class);
	
	private String _aliasName;

	private String _aliasProperty;
	private String _extendedAliasProperty;

	private String _defaultSearchPropery;
	private Operator _defaultOperator;
	
	public ElasticSettings() {

	}

	public ElasticSettings(CompassSettings settings) {
		_aliasName = settings.getSetting(CompassEnvironment.CONNECTION_SUB_CONTEXT, "index");
		
        _extendedAliasProperty = settings.getSetting(CompassEnvironment.Alias.EXTENDED_ALIAS_NAME, CompassEnvironment.Alias.DEFAULT_EXTENDED_ALIAS_NAME);
        if (log.isDebugEnabled()) {
            log.debug("Using extended alias property [" + _extendedAliasProperty + "]");
        }

        // get the default search term, defaults to the all property
        _defaultSearchPropery = settings.getSetting(ElasticEnvironment.DEFAULT_SEARCH, ElasticEnvironment.Mapping.ALL_FIELD);
        if (log.isDebugEnabled()) {
            log.debug("Using default search property [" + _defaultSearchPropery + "]");
        }

        String sDefaultOperator = settings.getSetting(ElasticEnvironment.QueryParser.DEFAULT_PARSER_DEFAULT_OPERATOR, "AND");
        if ("and".equalsIgnoreCase(sDefaultOperator)) {
            _defaultOperator = Operator.AND;
        } else if ("or".equalsIgnoreCase(sDefaultOperator)) {
            _defaultOperator = Operator.OR;
        } else {
            throw new ConfigurationException("Defualt query string operator [" + sDefaultOperator + "] not recognized.");
        }
        if (log.isDebugEnabled()) {
            log.debug("Using default search operator [" + _defaultOperator + "]");
        }
	}

	public String getAliasName() {
		return _aliasName;
	}

	public String getExtendedAliasProperty() {
		return _extendedAliasProperty;
	}

	public String getDefaultSearchPropery() {
		return _defaultSearchPropery;
	}

	public Operator getDefaultOperator() {
		return _defaultOperator;
	}
	
	
}
