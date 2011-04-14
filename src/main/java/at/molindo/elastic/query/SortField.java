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
		FIELD,
		SCORE,
		DISTANCE,
		DOC
	}

	private final String _property;
	private final SortType _type;
	private final Locale _locale;
	private final boolean _reverse;

	public static SortField reverseOrder(SortField field) {
		return new SortField(field.getProperty(), field.getType(), field.getLocale(), !field.isReverse());
	}

	public SortField(String propertyName) {
		this(propertyName, null, null, false);
	}

	public SortField(String propertyName, boolean sortReverse) {
		this(propertyName, null, null, sortReverse);
	}
	
	public SortField(String propertyName, SortType type) {
		this(propertyName, type, null, false);
	}

	public SortField(String propertyName, SortType type, boolean sortReverse) {
		this(propertyName, type, null, sortReverse);
	}

	public SortField(String propertyName, Locale locale) {
		this(propertyName, null, locale, false);
	}

	public SortField(String propertyName, Locale locale, boolean sortReverse) {
		this(propertyName, null, locale, sortReverse);
	}

	public SortField(String propertyName, SortType sortType, Locale locale, boolean sortReverse) {
		_property = propertyName;
		_type = sortType == null ? SortType.FIELD : sortType;
		_locale = locale;
		_reverse = sortReverse;
	}

	public String getProperty() {
		return _property;
	}

	public SortType getType() {
		return _type;
	}

	public Locale getLocale() {
		return _locale;
	}

	public boolean isReverse() {
		return _reverse;
	}

}
