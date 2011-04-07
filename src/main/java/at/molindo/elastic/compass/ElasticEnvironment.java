
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
