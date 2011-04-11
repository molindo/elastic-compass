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

public class ElasticEnvironment {

    /**
     * The default search that will be used for non prefixed query values.
     * Defaults to the value of the "all" property.
     */
    public static final String DEFAULT_SEARCH = "compass.engine.defaultsearch";
    
    /**
     * Create a local node
     * Defaults to false
     */
	public static final String LOCAL = "compass.engine.local";

	/**
	 * A set of configuration settings for index.
	 */
	public static abstract class Index {

		/**
		 * The prefix for the similarity settings.
		 */
		public static final String PREFIX = "compass.engine.index";

		public static final String NAME_PREFIX = PREFIX + ".prefix";
		
		public static final String ALIAS_NAME = PREFIX + ".alias";
		
		public static final String GPS_ALIAS_NAME = PREFIX + ".gpsalias";

	}

    /**
     * Settings for different query parser implementations.
     */
    public static abstract class QueryParser {

        /**
         * The prefix used for query parser groups.
         */
        public static final String PREFIX = "compass.engine.queryParser";

        /**
         * The default operator when parsing query strings. Defaults to <code>AND</code>. Can be either
         * <code>AND</code> or <code>OR</code>.
         */
        public static final String DEFAULT_PARSER_DEFAULT_OPERATOR = "defaultOperator";
    }

    public static abstract class Mapping {
    	public static final String ALL_FIELD = "_all";
    	public static final String TYPE_FIELD = "_type";
    }
}
