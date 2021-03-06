/**
 *   Copyright Xavier Jodoin xjodoin@torpedoquery.org
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.torpedoquery.jpa.internal.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javassist.util.proxy.MethodHandler;

import org.torpedoquery.core.QueryBuilder;
import org.torpedoquery.jpa.internal.MethodCall;
import org.torpedoquery.jpa.internal.TorpedoProxy;
import org.torpedoquery.jpa.internal.TorpedoMagic;
import org.torpedoquery.jpa.internal.handlers.QueryHandler;

import com.google.common.base.Defaults;

public class TorpedoMethodHandler implements MethodHandler, TorpedoProxy {
	private final Map<Object, QueryBuilder<?>> proxyQueryBuilders = new IdentityHashMap<Object, QueryBuilder<?>>();
	private final Deque<MethodCall> methods = new LinkedList<MethodCall>();
	private final QueryBuilder<?> root;
	private final ProxyFactoryFactory proxyfactoryfactory;
	private List<Object> params = new ArrayList<Object>();

	public TorpedoMethodHandler(QueryBuilder<?> root,
			ProxyFactoryFactory proxyfactoryfactory) {
		this.root = root;
		this.proxyfactoryfactory = proxyfactoryfactory;
	}

	public QueryBuilder<?> addQueryBuilder(Object proxy,
			QueryBuilder<?> queryBuilder) {
		proxyQueryBuilders.put(proxy, queryBuilder);
		return queryBuilder;
	}

	@Override
	public Object invoke(Object self, Method thisMethod, Method proceed,
			Object[] args) throws Throwable {
		if (thisMethod.getDeclaringClass().equals(TorpedoProxy.class)) {
			try {
				return thisMethod.invoke(this, args);
			} catch (InvocationTargetException e) {
				throw e.getTargetException();
			}
		}

		methods.addFirst(new SimpleMethodCall((TorpedoProxy) self, thisMethod));
		TorpedoMagic.setQuery((TorpedoProxy) self);

		return createReturnValue(thisMethod.getReturnType());
	}

	private <T> T createReturnValue(final Class<T> returnType)
			throws NoSuchMethodException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		if (returnType.isPrimitive()) {
			return Defaults.defaultValue(returnType);
		} else if (!Modifier.isFinal(returnType.getModifiers())) {
			return createLinkedProxy(returnType);
		} else {
			return null;
		}
	}

	private <T> T createLinkedProxy(final Class<T> returnType)
			throws NoSuchMethodException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		MethodHandler mh = new MethodHandler() {

			@Override
			public Object invoke(Object self, Method thisMethod,
					Method proceed, Object[] args) throws Throwable {
				MethodCall pollFirst = methods.pollFirst();
				LinkedMethodCall linkedMethodCall = new LinkedMethodCall(
						pollFirst, new SimpleMethodCall(pollFirst.getProxy(),
								thisMethod));
				methods.addFirst(linkedMethodCall);
				return createReturnValue(thisMethod.getReturnType());
			}
		};

		return proxyfactoryfactory.createProxy(mh, returnType);
	}

	public <T> T handle(QueryHandler<T> handler) {
		final T result = handler.handleCall(proxyQueryBuilders, methods);
		return result;
	}

	public QueryBuilder<?> getQueryBuilder(Object proxy) {
		return proxyQueryBuilders.get(proxy);
	}

	public <T> QueryBuilder<T> getRoot() {
		return (QueryBuilder<T>) root;
	}

	@Override
	public TorpedoMethodHandler getTorpedoMethodHandler() {
		return this;
	}

	public Deque<MethodCall> getMethods() {
		return methods;
	}

	public void addParam(Object param) {
		params.add(param);
	}

	public Object[] params() {
		Object[] array = params.toArray();
		params.clear();
		return array;
	}

}
