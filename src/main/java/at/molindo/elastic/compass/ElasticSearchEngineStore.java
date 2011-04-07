package at.molindo.elastic.compass;

public interface ElasticSearchEngineStore {

	
	String[] polyCalcSubIndexes(String[] aliases, Class<?>[] types);

}
