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

package at.molindo.elastic.query;

import java.util.Locale;

public class SortField {

	public enum SortType {
		// TODO check what's even possible with ES
		DOC, SCORE, STRING, INT, FLOAT, LONG, DOUBLE, SHORT, CUSTOM, BYTE, STRING_VAL;
	}

	public SortField(String propertyName, SortType type) {
		// TODO Auto-generated constructor stub
	}

	public SortField(String propertyName, SortType type, boolean sortReverse) {
		// TODO Auto-generated constructor stub
	}

	public SortField(String propertyName, Locale locale, boolean sortReverse) {
		// TODO Auto-generated constructor stub
	}

	public SortField(String propertyName, Locale locale) {
		// TODO Auto-generated constructor stub
	}
}