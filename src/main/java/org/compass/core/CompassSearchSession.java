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

import at.molindo.elastic.compass.CompassAdapted;

@CompassAdapted
public interface CompassSearchSession {

	CompassSettings getSettings();
	
    Object get(String alias, Object id) throws CompassException;
    
    <T> T get(Class<T> clazz, Object id) throws CompassException;
	
    Object get(String alias, Object... ids) throws CompassException;
    
    <T> T load(Class<T> clazz, Object id) throws CompassException;

	Object load(String alias, Object id);

	Resource getResource(Class<?> clazz, Object id);

	Resource getResource(String alias, Object id);

	Resource loadResource(Class<?> clazz, Object id);
	
	Resource loadResource(String alias, Object id);
	
	CompassHits find(String query);
	
	void close();
	
}
