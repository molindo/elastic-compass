package at.molindo.elastic.query;

import org.elasticsearch.index.query.xcontent.QueryBuilders;
import org.elasticsearch.index.query.xcontent.XContentQueryBuilder;

public abstract class Term<T> {

	private String _name;
	private T _value;

	public Term(String name, T value) {
		_name = name;
		_value = value;
	}

	public String getName() {
		return _name;
	}

	public T getValue() {
		return _value;
	}

	protected abstract XContentQueryBuilder buildQuery();

	public static class StringTerm extends Term<String> {

		public StringTerm(String name, String value) {
			super(name, value);
		}

		@Override
		protected XContentQueryBuilder buildQuery() {
			return QueryBuilders.termQuery(getName(), getValue());
		}

	}
}
