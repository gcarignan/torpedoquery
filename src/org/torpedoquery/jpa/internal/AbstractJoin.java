package com.netappsid.jpaquery.internal;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractJoin implements Join {

	private final QueryBuilder join;
	private final String fieldName;

	public AbstractJoin(QueryBuilder join, String fieldName) {
		this.join = join;
		this.fieldName = fieldName;
	}

	@Override
	public String getJoin(String parentAlias, AtomicInteger incrementor) {

		return (" " + getJoinType() + " join " + parentAlias + "." + fieldName + " " + join.getAlias(incrementor))
				+ (join.hasWithClause() ? join.getWithClause(incrementor) : "") + (join.hasSubJoin() ? join.getJoins(incrementor) : "");
	}

	@Override
	public void appendWhereClause(StringBuilder builder, AtomicInteger incrementor) {
		join.appendWhereClause(builder, incrementor);
	}

	@Override
	public void appendOrderBy(StringBuilder builder, AtomicInteger incrementor) {
		join.appendOrderBy(builder, incrementor);
	}

	@Override
	public void appendGroupBy(StringBuilder builder, AtomicInteger incrementor) {
		join.appendGroupBy(builder, incrementor);
	}

	@Override
	public List<ValueParameter> getParams() {
		return join.getValueParameters();
	}

	public abstract String getJoinType();

}