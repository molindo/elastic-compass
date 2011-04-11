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

package org.compass.core;

import org.compass.core.config.CompassSettings;

/**
 * A specialized interface that provides only index capabilities.
 *
 * <p>Using the session depends on how transaction managemnet should be done (also see
 * {@link org.compass.core.Compass#openSession()}. The simplest form looks like this:
 *
 * <pre>
 * CompassIndexSession session = compass.openIndexSession();
 * try {
 *      // do operations with the session
 *      session.commit(); // same as session.close()
 * } catch (Exception e) {
 *      session.rollback();
 * } finally {
 *      session.close();
 * }
 * </pre>
 * 
 * @author kimchy
 */
public interface CompassIndexSession {

	CompassSettings getSettings();

    void delete(Resource resource) throws CompassException;

    void delete(Object obj) throws CompassException;

    void delete(String alias, Object obj) throws CompassException;

    void delete(String alias, Object... ids) throws CompassException;

    void delete(Class<?> clazz, Object obj) throws CompassException;

    void delete(Class<?> clazz, Object... ids) throws CompassException;

    void delete(CompassQuery query) throws CompassException;

    void create(Object obj) throws CompassException;

    void create(String alias, Object obj) throws CompassException;

    void save(Object obj) throws CompassException;

    void save(String alias, Object obj) throws CompassException;

    void close() throws CompassException;

    boolean isClosed();
}
