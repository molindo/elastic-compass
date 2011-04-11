package at.molindo.elastic.compass;

import org.compass.core.Resource;
import org.compass.core.engine.SearchEngineException;
import org.compass.core.engine.SearchEngineHighlighter;
import org.compass.core.engine.SearchEngineHits;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

public class ElasticSearchEngineHits implements SearchEngineHits {

	private ElasticClient _client;
	private SearchHits _hits;
	private ElasticResource[] _resources;
	private int _length;
	
	public ElasticSearchEngineHits(ElasticClient client, SearchHits hits) {
		_client = client;
		_hits = hits;
		_length = _hits.hits().length;
	}

	@Override
	public Resource getResource(int n) throws SearchEngineException {
		if (_resources == null) {
			_resources = new ElasticResource[_length];
		}
		ElasticResource resource = _resources[n];
		if (resource == null) {
			SearchHit hit = _hits.getAt(n);
			_resources[n] = resource = _client.toResource(hit);
		}
		return resource;
	}

	@Override
	public int getLength() {
		return _length;
	}

	@Override
	public float score(int i) throws SearchEngineException {
		return _hits.getAt(i).getScore();
	}

	@Override
	public SearchEngineHighlighter getHighlighter() throws SearchEngineException {
		throw new NotImplementedException();
	}

	@Override
	public void close() throws SearchEngineException {
		_hits = null;
		_resources = null;
		_client = null;
	}

}
