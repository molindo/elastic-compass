/*
 * Copyright 2004-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.compass.annotations.test.analyzer;

import org.compass.annotations.test.AbstractAnnotationsTestCase;
import org.compass.core.CompassHits;
import org.compass.core.CompassSession;
import org.compass.core.config.CompassConfiguration;
import org.compass.core.engine.SearchEngineException;
import org.junit.Ignore;

/**
 * @author kimchy
 */
public class AnalyzerTests extends AbstractAnnotationsTestCase {

	private static final String TEXT = "the quick brown fox jumped over the lazy dogs";

	protected void addExtraConf(CompassConfiguration conf) {
		conf.addClass(A.class).addPackage("org.compass.annotations.test.analyzer");
	}

	@Ignore("_analyzer property not working as expected")
	public void testFieldAnalyzer() {
		CompassSession session = openSession();

		A a = new A();
		a.id = 1;
		a.value = TEXT;
		a.analyzer = "simple";
		session.save(a);

		refresh(session);

		assertTokens(analyze(session, a.analyzer, a.value), "the", "fox");

		CompassHits hits = session.find("value:the");
		assertEquals("'the' was ignored, obviously not using 'simple' analyzer", 1, hits.getLength());

		hits = session.find("value:fox");
		assertEquals("'fox' was ignored, obviously not ignoring stop words", 1, hits.getLength());

		a = new A();
		a.id = 1;
		a.value = TEXT;
		a.analyzer = null;
		try {
			session.save(a);
			fail("attempt to save resource without analyzer didn't fail");
		} catch (SearchEngineException e) {

		}
	}

}
