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

import org.compass.core.Compass;
import org.compass.core.CompassHits;
import org.compass.core.CompassQuery;
import org.compass.core.CompassQueryFilter;
import org.compass.core.CompassSession;
import org.compass.core.engine.SearchEngineException;
import org.compass.core.engine.SearchEngineQuery;
import org.compass.core.engine.SearchEngineQueryFilter;
import org.compass.core.impl.DefaultCompassHits;
import org.compass.core.impl.DefaultCompassQuery;
import org.compass.core.impl.DefaultCompassQueryFilter;
import org.compass.core.spi.InternalCompass;
import org.compass.core.spi.InternalCompassQuery;
import org.compass.core.spi.InternalCompassSession;

import at.molindo.elastic.filter.Filter;
import at.molindo.elastic.query.Query;

/**
 * Allows to create Compass related objects based on external (internally no supported by Compass)
 * Lucene objects.
 *
 * @author kimchy
 */
public abstract class ElasticHelper {

    /**
     * Creates a new {@link CompassQuery} based on a Lucene {@link Query}.
     *
     * <p>Allows to create {@link CompassQuery} based on external Lucene {@link Query} that is not supported
     * by one of Compass query builders.
     *
     * @param compass Compass instance
     * @param query   The lucene query to wrap
     * @return A compass query wrapping the lucene query
     */
    public static CompassQuery createCompassQuery(Compass compass, Query query) {
        InternalCompass internalCompass = (InternalCompass) compass;
        SearchEngineQuery searchEngineQuery =
                new ElasticSearchEngineQuery((ElasticSearchEngineFactory) internalCompass.getSearchEngineFactory(), query);
        return new DefaultCompassQuery(searchEngineQuery, internalCompass);
    }

    /**
     * Creates a new {@link CompassQuery} based on a Lucene {@link Query}.
     *
     * <p>Allows to create {@link CompassQuery} based on external Lucene {@link Query} that is not supported
     * by one of Compass query builders.
     *
     * @param session Compass session
     * @param query   The lucene query to wrap
     * @return A compass query wrapping the lucene query
     */
    public static CompassQuery createCompassQuery(CompassSession session, Query query) {
        InternalCompassSession internalCompassSession = (InternalCompassSession) session;
        SearchEngineQuery searchEngineQuery =
                new ElasticSearchEngineQuery((ElasticSearchEngineFactory) internalCompassSession.getCompass().getSearchEngineFactory(), query);
        InternalCompassQuery compassQuery = new DefaultCompassQuery(searchEngineQuery, internalCompassSession.getCompass());
        compassQuery.attach(session);
        return compassQuery;
    }

    /**
     * Returns the underlying {@link LuceneSearchEngineQuery} of the given {@link CompassQuery}.
     * <p/>
     * Can be used for example to add custom Sorting using
     * {@link LuceneSearchEngineQuery#addSort(org.apache.lucene.search.SortField)}, or get the actual lucene query
     * using {@link org.compass.core.lucene.engine.LuceneSearchEngineQuery#getQuery()}.
     *
     * @param query The compass query to extract the lucene search engine query from
     * @return The lucene search engine query extracted from the compass query
     */
    public static ElasticSearchEngineQuery getLuceneSearchEngineQuery(CompassQuery query) {
        return (ElasticSearchEngineQuery) ((DefaultCompassQuery) query).getSearchEngineQuery();
    }

    /**
     * Creates a new {@link CompassQueryFilter} based on a Lucene {@link Filter}.
     * <p/>
     * Allows to create {@link CompassQueryFilter} based on external Lucene {@link Filter} that is not supported
     * by one fo Comapss query filter builders.
     *
     * @param session Comapss session
     * @param filter  The lucene filter to wrap
     * @return A compass query filter wrapping lucene query.
     */
    public static CompassQueryFilter createCompassQueryFilter(CompassSession session, Filter filter) {
        SearchEngineQueryFilter searchEngineQueryFilter = new ElasticSearchEngineQueryFilter(filter);
        return new DefaultCompassQueryFilter(searchEngineQueryFilter);
    }

    /**
     * Returns the underlying {@link ElasticSearchEngineQueryFilter} of the given {@link CompassQueryFilter}.
     * <p/>
     * Can be used to get the actual Lucene {@link Filter} using
     * {@link org.compass.core.lucene.engine.ElasticSearchEngineQueryFilter#getFilter()}.
     *
     * @param filter The compass query filter to extract the lucene search engine query filter from
     * @return The lucene search engine query filter extracted from the compass query filter
     */
    public static ElasticSearchEngineQueryFilter getElasticSearchEngineQueryFilter(CompassQueryFilter filter) {
        return (ElasticSearchEngineQueryFilter) ((DefaultCompassQueryFilter) filter).getFilter();
    }

    /**
     * Returns the underlying {@link ElasticSearchEngineHits} of the given {@link CompassHits}.
     * <p/>
     * Used mainly to access the actual Lucene {@link org.apache.lucene.search.Hits}, or get
     * Lucene {@link org.apache.lucene.search.Explanation}.
     */
    public static ElasticSearchEngineHits getElasticSearchEngineHits(CompassHits hits) {
        return (ElasticSearchEngineHits) ((DefaultCompassHits) hits).getSearchEngineHits();
    }

    /**
     * Returns the given search engine "internals" used for search. For Lucene, returns
     * {@link LuceneSearchEngineInternalSearch} which allows to access Lucene
     * {@link org.apache.lucene.index.IndexReader} and {@link org.apache.lucene.search.Searcher}.
     * <p/>
     * The search intenrals will be ones that are executed against the whole index. In order to search on
     * specific aliases or sub indexes, please use {@link #getLuceneInternalSearch(org.compass.core.CompassSession,String[],String[])} .
     *
     * @param session A compass session within a transaction
     * @return Lucene search "internals"
     */
    public static ElasticSearchEngineInternalSearch getElasticInternalSearch(CompassSession session) {
        return getElasticInternalSearch(session, null);
    }

    /**
     * Returns the given search engine "internals" used for search. For Lucene, returns
     * {@link LuceneSearchEngineInternalSearch} which allows to access Lucene
     * {@link org.apache.lucene.index.IndexReader} and {@link org.apache.lucene.search.Searcher}.
     * <p/>
     * The search can be narrowed down to specific sub indexes or aliases. A <code>null</code> value
     * means all the sub indexes/aliases.
     *
     * @param session    A compass sessino within a transaction
     * @param subIndexes A set of sub indexes to narrow down the index scope
     * @param aliases    A set of aliases to narrow down the index scope
     * @return Lucene search "internals"
     */
    public static ElasticSearchEngineInternalSearch getElasticInternalSearch(CompassSession session, String[] aliases) {
        return (ElasticSearchEngineInternalSearch) ((InternalCompassSession) session).getSearchEngine().internalSearch(null, aliases);
    }
    
    /**
     * Returns all the values of for the given propery name.
     */
    public static String[] findPropertyValues(CompassSession session, String propertyName) throws SearchEngineException {
       	return getElasticInternalSearch(session).getClient().findPropertyValues(propertyName);
    }
}
