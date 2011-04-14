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

package at.molindo.elastic.compass.query;

import org.compass.core.engine.SearchEngineQuery;
import org.compass.core.engine.SearchEngineQueryBuilder;

import at.molindo.elastic.compass.ElasticSearchEngineFactory;
import at.molindo.elastic.compass.ElasticSearchEngineQuery;
import at.molindo.elastic.query.BooleanQuery;
import at.molindo.elastic.query.BooleanClause;

/**
 * @author kimchy
 */
public class ElasticSearchEngineBooleanQueryBuilder implements SearchEngineQueryBuilder.SearchEngineBooleanQueryBuilder {

    private ElasticSearchEngineFactory searchEngineFactory;

    private BooleanQuery boolQuery;

    public ElasticSearchEngineBooleanQueryBuilder(ElasticSearchEngineFactory searchEngineFactory, boolean disableCoord) {
        this.searchEngineFactory = searchEngineFactory;
        boolQuery = new BooleanQuery().setDisableCoord(disableCoord);
    }

    public SearchEngineQueryBuilder.SearchEngineBooleanQueryBuilder addMust(SearchEngineQuery query) {
        boolQuery.add(((ElasticSearchEngineQuery) query).getQuery(), BooleanClause.Occur.MUST);
        return this;
    }

    public SearchEngineQueryBuilder.SearchEngineBooleanQueryBuilder addMustNot(SearchEngineQuery query) {
        boolQuery.add(((ElasticSearchEngineQuery) query).getQuery(), BooleanClause.Occur.MUST_NOT);
        return this;
    }

    public SearchEngineQueryBuilder.SearchEngineBooleanQueryBuilder addShould(SearchEngineQuery query) {
        boolQuery.add(((ElasticSearchEngineQuery) query).getQuery(), BooleanClause.Occur.SHOULD);
        return this;
    }

    public SearchEngineQueryBuilder.SearchEngineBooleanQueryBuilder setMinimumNumberShouldMatch(int min) {
        boolQuery.setMinimumNumberShouldMatch(min);
        return this;
    }

    public SearchEngineQuery toQuery() {
        return new ElasticSearchEngineQuery(searchEngineFactory, boolQuery);
    }
}
