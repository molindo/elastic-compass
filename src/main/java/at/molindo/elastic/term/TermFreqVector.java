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
