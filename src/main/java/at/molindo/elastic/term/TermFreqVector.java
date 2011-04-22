package at.molindo.elastic.term;

import java.util.List;

import org.elasticsearch.search.facet.terms.TermsFacet;
import org.elasticsearch.search.facet.terms.TermsFacet.Entry;

public class TermFreqVector {

	public static final TermFreqVector NULL = new TermFreqVector();
	
	private String[] _terms;
	private int[] _termFrequencies;

	private TermFreqVector() {
		_terms = new String[0];
		_termFrequencies = new int[0];
	}
	
	public TermFreqVector(TermsFacet facet) {
		List<? extends Entry> entries = facet.getEntries();
		
		_terms = new String[entries.size()];
		_termFrequencies = new int[entries.size()];
		
		int i = 0;
		for (Entry e : entries) {
			_terms[i] = e.term();
			_termFrequencies[i++] = e.count();
		}
	}

	public int size() {
		return _terms.length;
	}

	public String[] getTerms() {
		return _terms;
	}

	public int[] getTermFrequencies() {
		return _termFrequencies;
	}

}
