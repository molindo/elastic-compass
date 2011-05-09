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

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import at.molindo.utils.collections.CollectionUtils;

public enum ElasticType {
	// TODO finish
	STRING("string", "store", "index"),

	INTEGER("integer"),
	LONG("long"),
	FLOAT("float"),
	DOUBLE("double"),

	DATE,
	BOOLEAN,
	BINARY;

	private final String _name;
	private final Set<String> _supportedProperties;

	private ElasticType() {
		_name = name().toLowerCase();
		_supportedProperties = Collections.emptySet();
	}

	private ElasticType(String name, String... supportedProperties) {
		this(name, CollectionUtils.set(Arrays.asList(supportedProperties)));
	}

	private ElasticType(String name, Set<String> supportedProperties) {
		_name = name;
		_supportedProperties = CollectionUtils.unmodifiableSet(supportedProperties);
	}

	public String getName() {
		return _name;
	}

	public boolean isPropertySupported(String property) {
		return _supportedProperties.contains(property);
	}

	@Override
	public String toString() {
		return getName();
	}

}
