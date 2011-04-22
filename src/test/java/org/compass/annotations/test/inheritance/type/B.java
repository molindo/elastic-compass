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

package org.compass.annotations.test.inheritance.type;

import org.compass.annotations.Searchable;
import org.compass.annotations.SearchableId;

/**
 * @author kimchy
 */
@Searchable
public class B implements A {

    private String id;

    @SearchableId
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
