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

package org.compass.core.transaction;

import org.compass.core.Compass;
import org.compass.core.CompassException;
import org.compass.core.CompassSession;
import org.compass.core.CompassSessionFactory;
import org.compass.core.config.CompassSettings;

/**
 * @deprecated use {@link CompassSessionFactory} instead for clearity (no transactions!)
 */
@Deprecated
public interface TransactionFactory {

    /**
     * Configures the transaction factory.
     */
    void configure(Compass compass, CompassSettings settings) throws CompassException;

    /**
     * Retuns a transaction bound session, or <code>null</code> if none is found.
     */
    CompassSession getTransactionBoundSession() throws CompassException;

}
