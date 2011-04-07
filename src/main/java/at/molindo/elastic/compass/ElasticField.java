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

package at.molindo.elastic.compass;

import java.io.Reader;

import org.apache.lucene.analysis.TokenStream;
import org.compass.core.Property;
import org.compass.core.Property.Index;
import org.compass.core.Property.Store;
import org.compass.core.Property.TermVector;

/**
 * defaults:
 * <ol>
 * <li>index: ANALYZED</li>
 * <li>store: NO</li>
 * <li>termVector: NO</li>
 * <li>omitTf, omitNorms, binary, lazy: false</li>
 * </ol>
 * 
 * @author stf
 * 
 */
public class ElasticField {
	private final String _name;
	private float _boost = 0.0f;

	private boolean _omitTf = false;
	private boolean _omitNorms = false;
	private boolean _binary = false;
	private boolean _lazy = false;

	private TermVector _termVector = Property.TermVector.NO;
	private Index _index = Index.ANALYZED;
	private Store _store = Store.NO;

	// length/offset for all primitive types
	private int _binaryLength;
	private int _binaryOffset;

	private final Object _data;

	public ElasticField(String name) {
		this(name, (Object) null);
	}

	public ElasticField(String name, String data) {
		this(name, (Object) data);
	}

	public ElasticField(String name, Reader data) {
		this(name, (Object) data);
	}

	public ElasticField(String name, byte[] data) {
		this(name, (Object) data);
	}

	public ElasticField(String name, TokenStream data) {
		this(name, (Object) data);
	}

	private ElasticField(String name, Object data) {
		if (name == null) {
			throw new NullPointerException("name");
		}
		_name = name;
		_data = data;
	}

	public String getName() {
		return _name;
	}

	public TermVector getTermVector() {
		return _termVector;
	}

	public ElasticField setTermVector(TermVector termVector) {
		if (termVector == null) {
			throw new NullPointerException("termVector");
		}
		_termVector = termVector;
		return this;
	}

	public boolean isTermVectorStored() {
		return _termVector != Property.TermVector.NO;
	}

	public float getBoost() {
		return _boost;
	}

	public ElasticField setBoost(float boost) {
		_boost = boost;
		return this;
	}

	public boolean isOmitTf() {
		return _omitTf;
	}

	public ElasticField setOmitTf(boolean omitTf) {
		_omitTf = omitTf;
		return this;
	}

	public boolean isOmitNorms() {
		return _omitNorms;
	}

	public ElasticField setOmitNorms(boolean omitNorms) {
		_omitNorms = omitNorms;
		return this;
	}

	public boolean isBinary() {
		return _binary;
	}

	public ElasticField setBinary(boolean binary) {
		_binary = binary;
		return this;
	}

	public boolean isCompressed() {
		return _store == Store.COMPRESS;
	}

	public boolean isStored() {
		return _store != Store.NO;
	}

	public boolean isLazy() {
		return _lazy;
	}

	public ElasticField setLazy(boolean lazy) {
		_lazy = lazy;
		return this;
	}

	public int getBinaryLength() {
		return _binaryLength;
	}

	public ElasticField setBinaryLength(int binaryLength) {
		_binaryLength = binaryLength;
		return this;
	}

	public int getBinaryOffset() {
		return _binaryOffset;
	}

	public ElasticField setBinaryOffset(int binaryOffset) {
		_binaryOffset = binaryOffset;
		return this;
	}

	public Index getIndex() {
		return _index;
	}

	public ElasticField setIndex(Index index) {
		if (index == null) {
			throw new NullPointerException("index");
		}
		_index = index;
		return this;
	}

	public boolean isIndexed() {
		return _index != Index.NO;
	}

	@SuppressWarnings("deprecation")
	public boolean isTokenized() {
		return _index == Index.ANALYZED || _index == Index.TOKENIZED;
	}

	public Store getStore() {
		return _store;
	}

	public ElasticField setStore(Store store) {
		if (store == null) {
			throw new NullPointerException("store");
		}
		_store = store;
		return this;
	}

	public String getStringValue() {
		return _data instanceof String ? (String) _data : null;
	}

	public byte[] getBinaryValue() {
		if (!_binary) {
			return null;
		}

		final byte[] data = (byte[]) _data;
		if (_binaryOffset == 0 && data.length == _binaryLength) {
			return data; // Optimization
		}

		final byte[] ret = new byte[_binaryLength];
		System.arraycopy(data, _binaryOffset, ret, 0, _binaryLength);
		return ret;
	}

	public Reader getReaderValue() {
		return _data instanceof Reader ? (Reader) _data : null;
	}

	public TokenStream getTokenStreamValue() {
		return _data instanceof TokenStream ? (TokenStream) _data : null;
	}

	@Override
	public String toString() {
		return "ElasticField [name=" + _name + ", boost=" + _boost + ", omitTf=" + _omitTf
				+ ", omitNorms=" + _omitNorms + ", binary=" + _binary + ", lazy=" + _lazy
				+ ", termVector=" + _termVector + ", index=" + _index + ", store=" + _store
				+ ", binaryLength=" + _binaryLength + ", binaryOffset=" + _binaryOffset + ", data="
				+ _data + "]";
	}

}
