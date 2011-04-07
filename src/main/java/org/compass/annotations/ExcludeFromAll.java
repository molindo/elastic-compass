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

package org.compass.annotations;

/**
 * Specifies if a certain property should be excluded from all property or not.
 *
 * @author kimchy
 */
public enum ExcludeFromAll {
    /**
     * The property will not be excluded from all. If this property is "not_analyzed"
     * it will be added as is to the all property without being analyzed/tokenized.
     */
    NO,
    /**
     * The property will not be excluded from all. If this property is "not_analyzed"
     * it will be added as is to the all property after being analyzed.
     */
    NO_ANALYZED,
    /**
     * The property will be exlcuded from all.
     */
    YES
}