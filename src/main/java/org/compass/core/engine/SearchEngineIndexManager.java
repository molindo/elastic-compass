/*
 * Copyright 2004-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.compass.core.engine;

import org.compass.gps.impl.DefaultReplaceIndexCallback;

import at.molindo.elastic.compass.CompassAdapted;

@CompassAdapted
public interface SearchEngineIndexManager {

	void verifyIndex();

	String[] polyCalcSubIndexes(String[] subIndexes, String[] aliases, Class[] types);

	void clearCache();

	void cleanIndex();

	void replaceIndex(SearchEngineIndexManager searchEngineIndexManager, DefaultReplaceIndexCallback defaultReplaceIndexCallback);

	void deleteIndex();
	
    public static interface IndexOperationPlan {

        String[] getSubIndexes();

        String[] getAliases();

        Class[] getTypes();
    }

    /**
     * A callback interface that works with.
     *
     * @author kimchy
     */
    public static interface IndexOperationCallback {

        /**
         * First step is called just after the index is locked for any dirty operations.
         * <p>
         * Return <code>true</code> if after the first step, the system should continue
         * to the second step.
         */
        boolean firstStep() throws SearchEngineException;

        /**
         * Second step is called just after the index is locked for read operations
         * (on top of the dirty operations).
         */
        void secondStep() throws SearchEngineException;
    }
	
    /**
     * A callback to replace the current index.
     *
     * @author kimchy
     */
    public static interface ReplaceIndexCallback {

        /**
         * Provides the ability to be notified when the index can be built
         * during the replace operation. There is no need to actually build the
         * index, if one already exists.
         */
        void buildIndexIfNeeded() throws SearchEngineException;
    }
}
