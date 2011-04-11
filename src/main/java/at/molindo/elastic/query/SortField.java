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