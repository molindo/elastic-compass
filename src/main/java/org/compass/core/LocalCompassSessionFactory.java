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

package org.compass.core;

import java.util.HashMap;
import java.util.Map;

import org.compass.core.Compass;
import org.compass.core.CompassException;
import org.compass.core.CompassSession;
import org.compass.core.CompassSessionFactory;
import org.compass.core.config.CompassEnvironment;
import org.compass.core.config.CompassSettings;
import org.compass.core.spi.InternalCompassSession;

public class LocalCompassSessionFactory implements CompassSessionFactory {

	/**
	 * A ThreadLocal maintaining current sessions for the given execution
	 * thread. The actual ThreadLocal variable is a java.util.Map to account for
	 * the possibility for multiple Compass instances being used during
	 * execution of the given thread.
	 */
	private static final ThreadLocal<Map<Compass, CompassSession>> context = new ThreadLocal<Map<Compass, CompassSession>>();

	private boolean disableThreadBoundTx = false;

	private Compass compass;

	@Override
	public void configure(Compass compass, CompassSettings settings) throws CompassException {
		this.compass = compass;
		disableThreadBoundTx = settings
				.getSettingAsBoolean(CompassEnvironment.Transaction.DISABLE_THREAD_BOUND_LOCAL_TRANSATION, false);
	}

	protected boolean isWithinExistingTransaction(InternalCompassSession session) throws CompassException {
		return getTransactionBoundSession() == session;
	}

	public CompassSession getTransactionBoundSession() throws CompassException {
		if (disableThreadBoundTx) {
			return null;
		}
		Map<Compass, CompassSession> sessionMap = sessionMap();
		if (sessionMap == null) {
			return null;
		} else {
			return sessionMap.get(compass);
		}
	}

	
	
	@Override
	public void setTransactionBoundSession(CompassSession session) {
        if (disableThreadBoundTx) {
            return;
        }
        if (session == null) {
            Map<Compass, CompassSession> sessionMap = sessionMap();
            if (sessionMap != null) {
            	sessionMap.remove(compass);
            }
        } else {
            Map<Compass, CompassSession> sessionMap = sessionMap();
            if (sessionMap == null) {
                sessionMap = new HashMap<Compass, CompassSession>();
                context.set(sessionMap);
            }
            sessionMap.put(compass, session);
        }
	}

	private static Map<Compass, CompassSession> sessionMap() {
		return context.get();
	}

}
