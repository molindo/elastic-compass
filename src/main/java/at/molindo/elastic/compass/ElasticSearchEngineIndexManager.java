package at.molindo.elastic.compass;

import org.compass.core.engine.SearchEngineException;
import org.compass.core.engine.SearchEngineIndexManager;
import org.compass.gps.impl.DefaultReplaceIndexCallback;

public class ElasticSearchEngineIndexManager implements SearchEngineIndexManager {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(ElasticSearchEngineIndexManager.class);
	
	private ElasticSearchEngineStore _store;

	public ElasticSearchEngineIndexManager(ElasticSearchEngineStore store) {
		_store = store;
	}
	
	@Override
	public void verifyIndex() {
		// noop
	}

	@Override
	public String[] polyCalcSubIndexes(String[] subIndexes, String[] aliases, @SuppressWarnings("rawtypes") Class[] types) {
		if (subIndexes != null && subIndexes.length > 0) {
			log.warn("sub-indexes are not supported, ignoring");
		}
		return _store.polyCalcSubIndexes(aliases, types);
	}

	@Override
	public void clearCache() {
	}

	@Override
	public void cleanIndex() {
	}

	@Override
	public void replaceIndex(SearchEngineIndexManager indexManager, DefaultReplaceIndexCallback callback) {
		// ignore indexManager?
		doReplaceIndex(callback);
	}

	protected void doReplaceIndex(final ReplaceIndexCallback callback) throws SearchEngineException {
		callback.buildIndexIfNeeded();
	}

	public void deleteIndex() {
	}

}
