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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.compass.core.CompassToken;
import org.compass.core.Resource;
import org.compass.core.engine.SearchEngineAnalyzerHelper;
import org.compass.core.engine.SearchEngineException;
import org.compass.core.mapping.ResourceAnalyzerController;
import org.compass.core.mapping.ResourceMapping;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse.AnalyzeToken;

import at.molindo.utils.io.ReaderUtils;

/**
 * @author kimchy
 */
public class ElasticSearchEngineAnalyzerHelper implements SearchEngineAnalyzerHelper {

	private ElasticSearchEngine _searchEngine;
	private String _analyzer;

	public ElasticSearchEngineAnalyzerHelper(ElasticSearchEngine searchEngine) {
		_searchEngine = searchEngine;
	}

	public SearchEngineAnalyzerHelper setAnalyzer(String analyzerName) {
		_analyzer = analyzerName;
		return this;
	}

	public SearchEngineAnalyzerHelper setAnalyzer(Resource resource) throws SearchEngineException {
        String alias = resource.getAlias();
        ResourceMapping resourceMapping = _searchEngine.getSearchEngineFactory().getMapping().getRootMappingByAlias(alias);
        if (resourceMapping.getAnalyzerController() == null) {
            _analyzer = getAnalyzerByMapping(resourceMapping);
        } else {
	        ResourceAnalyzerController analyzerController = resourceMapping.getAnalyzerController();
	        String analyzerPropertyName = analyzerController.getAnalyzerResourcePropertyName();
	        String analyzerName = resource.getValue(analyzerPropertyName);
	        if (analyzerName == null) {
	            analyzerName = analyzerController.getNullAnalyzer();
	        }
	        _analyzer = analyzerName;
        }
		return this;
	}

	private String getAnalyzerByMapping(ResourceMapping resourceMapping) {
		if (resourceMapping.getAnalyzer() != null) {
			return resourceMapping.getAnalyzer();
		} else {
			return ElasticEnvironment.Analyzer.DEFAULT_GROUP;
		}
	}

	public SearchEngineAnalyzerHelper setAnalyzerByAlias(String alias) throws SearchEngineException {
		_analyzer = getAnalyzerByMapping(_searchEngine.getSearchEngineFactory().getMapping().getRootMappingByAlias(alias));
		return this;
	}

	public CompassToken analyzeSingle(String text) throws SearchEngineException {
		CompassToken[] tokens = analyze(text);
		if (tokens == null || tokens.length == 0) {
			return null;
		}
		return tokens[0];
	}

	public CompassToken[] analyze(String text) {
		return analyze(new StringReader(text));
	}

	public CompassToken[] analyze(String propertyName, String text) throws SearchEngineException {
		List<AnalyzeToken> tokens = _searchEngine.analyze(_analyzer, text);
		CompassToken[] cTokens = new CompassToken[tokens.size()];

		int i = 0;
		for (AnalyzeToken token : tokens) {
			cTokens[i++] = new ElasticToken(token);
		}

		return cTokens;
	}

	public CompassToken[] analyze(Reader textReader) throws SearchEngineException {
		return analyze(null, textReader);
	}

	public CompassToken[] analyze(String propertyName, Reader textReader) throws SearchEngineException {
		try {
			return analyze(propertyName, ReaderUtils.text(textReader));
		} catch (IOException e) {
			throw new SearchEngineException("failed to read text from reader", e);
		}
	}
}
