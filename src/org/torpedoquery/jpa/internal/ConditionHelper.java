package com.netappsid.jpaquery.internal;

import com.netappsid.jpaquery.FJPAQuery;
import com.netappsid.jpaquery.Function;
import com.netappsid.jpaquery.OnGoingCondition;

public class ConditionHelper {
	public static <T, E extends OnGoingCondition<T>> E createCondition(LogicalCondition condition) {
		E handle = ConditionHelper.<T, E> createCondition(null, condition);
		return handle;
	}

	public static <T, E extends OnGoingCondition<T>> E createCondition(Function<T> function, LogicalCondition condition) {
		FJPAMethodHandler fjpaMethodHandler = FJPAQuery.getFJPAMethodHandler();
		WhereClauseHandler<T, E> whereClauseHandler = new WhereClauseHandler<T, E>(function, condition, new DoNothingQueryConfigurator<T>());
		E handle = fjpaMethodHandler.handle(whereClauseHandler);
		return handle;
	}

}