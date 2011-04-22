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
