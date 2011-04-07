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

}
