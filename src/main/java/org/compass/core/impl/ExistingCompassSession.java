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

package org.compass.core.impl;

import org.compass.core.CompassAnalyzerHelper;
import org.compass.core.CompassException;
import org.compass.core.CompassHits;
import org.compass.core.CompassQuery;
import org.compass.core.CompassQueryBuilder;
import org.compass.core.CompassQueryFilterBuilder;
import org.compass.core.Resource;
import org.compass.core.cache.first.FirstLevelCache;
import org.compass.core.config.CompassSettings;
import org.compass.core.engine.SearchEngine;
import org.compass.core.mapping.CompassMapping;
import org.compass.core.marshall.MarshallingContext;
import org.compass.core.marshall.MarshallingStrategy;
import org.compass.core.metadata.CompassMetaData;
import org.compass.core.spi.DirtyOperationContext;
import org.compass.core.spi.InternalCompass;
import org.compass.core.spi.InternalCompassSession;
import org.compass.core.spi.InternalSessionDelegateClose;

/**
 * @author kimchy
 */
public class ExistingCompassSession implements InternalCompassSession {

	private final InternalCompassSession session;

	public ExistingCompassSession(InternalCompassSession session) {
		this.session = session;
	}

	public InternalCompassSession getActualSession() {
		return this.session;
	}

	public void close() throws CompassException {
		// do nothing, works with existing one
	}

	// simple delegates

	public InternalCompass getCompass() {
		return session.getCompass();
	}

	public SearchEngine getSearchEngine() {
		return session.getSearchEngine();
	}

	public FirstLevelCache getFirstLevelCache() {
		return session.getFirstLevelCache();
	}

	public Object get(String alias, Object id, MarshallingContext context) throws CompassException {
		return session.get(alias, id, context);
	}

	public void setReadOnly() {
		session.setReadOnly();
	}

	public boolean isReadOnly() {
		return session.isReadOnly();
	}

	public CompassSettings getSettings() {
		return session.getSettings();
	}

	public void flush() throws CompassException {
		session.flush();
	}

	public boolean isClosed() {
		return session.isClosed();
	}

	@Override
	public void create(Object object) {
		session.create(object);
	}

	@Override
	public Object get(String alias, Object id) throws CompassException {
		return session.get(alias, id);
	}

	@Override
	public Object get(String alias, Object... ids) throws CompassException {
		return session.get(alias, ids);
	}

	@Override
	public void evictAll() {
		session.evictAll();
	}

	public <T> T get(Class<T> clazz, Object id) throws CompassException {
		return session.get(clazz, id);
	}

	public <T> T load(Class<T> clazz, Object id) throws CompassException {
		return session.load(clazz, id);
	}

	public Object load(String alias, Object id) {
		return session.load(alias, id);
	}

	public void create(String alias, Resource obj) {
		session.create(alias, obj);
	}

	public void create(String alias, Object obj) {
		session.create(alias, obj);
	}

	public void delete(Resource resource) {
		session.delete(resource);
	}

	public void delete(Object obj) {
		session.delete(obj);
	}

	@Override
	public void delete(String alias, Object... ids) throws CompassException {
		session.delete(alias, ids);
	}

	@Override
	public void delete(Class<?> clazz, Object... ids) throws CompassException {
		session.delete(clazz, ids);
	}

	@Override
	public void delete(CompassQuery query) throws CompassException {
		session.delete(query);
	}

	public CompassHits find(String query) {
		return session.find(query);
	}

	public void delete(Class<?> clazz, Object obj) {
		session.delete(clazz, obj);
	}

	public void delete(String alias, Object obj) {
		session.delete(alias, obj);
	}

	public Resource getResource(Class<?> clazz, Object id) {
		return session.getResource(clazz, id);
	}

	public Resource getResource(String alias, Object id) {
		return session.getResource(alias, id);
	}

	public Resource loadResource(Class<?> clazz, Object id) {
		return session.loadResource(clazz, id);
	}

	public Resource loadResource(String alias, Object id) {
		return session.loadResource(alias, id);
	}

	public void save(Object obj) {
		session.save(obj);
	}

	public void save(String alias, Object obj) {
		session.save(alias, obj);
	}

	public void evict(Object obj) {
		session.evict(obj);
	}

	public void evict(String alias, Object id) {
		session.evict(alias, id);
	}

	@Override
	public CompassQueryBuilder queryBuilder() throws CompassException {
		return session.queryBuilder();
	}

	@Override
	public CompassQueryFilterBuilder queryFilterBuilder() throws CompassException {
		return session.queryFilterBuilder();
	}

	@Override
	public CompassMapping getMapping() {
		return session.getMapping();
	}

	@Override
	public CompassMetaData getMetaData() {
		return session.getMetaData();
	}

	@Override
	public void delete(Object value, DirtyOperationContext context) {
		session.delete(value, context);
	}

	@Override
	public void create(Object value, DirtyOperationContext context) {
		session.create(value, context);
	}

	@Override
	public void save(Object value, DirtyOperationContext context) {
		session.save(value, context);
	}

	@Override
	public Resource getResource(Class<?> clazz, Object... ids) throws CompassException {
		return session.getResource(clazz, ids);
	}

	@Override
	public Resource getResource(String alias, Object... ids) throws CompassException {
		return session.getResource(alias, ids);
	}

	@Override
	public Resource loadResource(Class<?> clazz, Object... ids) throws CompassException {
		return session.loadResource(clazz, ids);
	}

	@Override
	public Resource loadResource(String alias, Object... ids) throws CompassException {
		return session.loadResource(alias, ids);
	}

	@Override
	public <T> T get(Class<T> clazz, Object... ids) throws CompassException {
		return session.get(clazz, ids);
	}

	@Override
	public <T> T load(Class<T> clazz, Object... ids) throws CompassException {
		return session.load(clazz, ids);
	}

	@Override
	public Object load(String alias, Object... ids) throws CompassException {
		return session.load(alias, ids);
	}

	@Override
	public void evict(Resource resource) {
		session.evict(resource);
	}

	@Override
	public Object getByResource(Resource resource) throws CompassException {
		return session.getByResource(resource);
	}

	@Override
	public void addDelegateClose(InternalSessionDelegateClose delegateClose) {
		session.addDelegateClose(delegateClose);
	}

	@Override
	public MarshallingStrategy getMarshallingStrategy() {
		return session.getMarshallingStrategy();
	}

	@Override
	public CompassAnalyzerHelper analyzerHelper() throws CompassException {
		return session.analyzerHelper();
	}

	@Override
	public Resource getResourceByIdResource(Resource idResource) throws CompassException {
		return session.getResourceByIdResource(idResource);
	}

	@Override
	public Resource getResourceByIdResourceNoCache(Resource idResource) throws CompassException {
		return session.getResourceByIdResourceNoCache(idResource);
	}
	
	
}
