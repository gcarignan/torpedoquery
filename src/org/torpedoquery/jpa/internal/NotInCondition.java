package com.netappsid.jpaquery.internal;

public class NotInCondition<T> extends InCondition<T> {

	public NotInCondition(Selector selector, Parameter<T> parameter) {
		super(selector, parameter);
	}

	@Override
	protected String getFragment() {
		return "not " + super.getFragment();
	}

}