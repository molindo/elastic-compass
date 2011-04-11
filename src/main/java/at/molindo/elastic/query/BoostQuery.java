package at.molindo.elastic.query;

/**
 * @param <T> type of query, e.g. StringQuery extends InternalQuery<StringQuery>
 */
public abstract class BoostQuery<T extends BoostQuery<T>> extends Query {

	
	public abstract T setBoost(float boost);
}
