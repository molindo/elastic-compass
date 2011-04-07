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

package org.compass.core.engine.naming;

/**
 * A naming strategy that uses {@link DefaultPropertyPath} when building
 * {@link PropertyPath}.
 *
 * @author kimchy
 * @author lexi
 * @see PropertyPath
 * @see DefaultPropertyPath
 * @see PropertyNamingStrategyFactory
 * @see DefaultPropertyNamingStrategyFactory
 */
public class DefaultPropertyNamingStrategy implements PropertyNamingStrategy {

    public boolean isInternal(String name) {
        return name.charAt(0) == '$';
    }

    public PropertyPath getRootPath() {
        return new StaticPropertyPath("$");
    }

    public PropertyPath buildPath(PropertyPath root, String name) {
        return new DefaultPropertyPath(root, name);
    }
}
