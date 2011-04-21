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

package org.compass.annotations.test.component;

import org.compass.annotations.ManagedIdIndex;
import org.compass.annotations.Searchable;
import org.compass.annotations.SearchableProperty;

@Searchable(root = false)
public class Community {
    private String name;

    public Community() {
    }

    public Community(String s) {
        setName(s);
    }

    @SearchableProperty(name = "communityName", managedIdIndex = ManagedIdIndex.NOT_ANALYZED)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
