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

import org.elasticsearch.search.facet.terms.TermsFacet;

public class TermPositionVector extends TermFreqVector {

	private boolean _positions;
	private boolean _offsets;

	public TermPositionVector(TermsFacet facet, boolean positions, boolean offsets) {
		super(facet);
		_positions = positions;
		_offsets = offsets;
	}

	public int[] getTermPositions(int i) {
		// TODO it is possible to access term positions?
		return _positions ? new int[0] : null;
	}

	public TermVectorOffsetInfo[] getOffsets(int i) {
		// TODO it is possible to access term offsets?
		return _offsets ? new TermVectorOffsetInfo[0] : null;
	}

}
