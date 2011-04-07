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
