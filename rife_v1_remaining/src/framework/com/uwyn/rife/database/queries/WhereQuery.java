/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Distributed under the terms of the GNU Lesser General License, v2.1 or later
 * $Id: WhereQuery.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.exceptions.DbQueryException;
import java.util.List;

interface WhereQuery<QueryType extends WhereQuery>
{
	QueryType whereSubselect(Select query);
	QueryType where(String where);
	QueryType whereAnd(String where);
	QueryType whereOr(String where);
	WhereGroup<QueryType> startWhere();
	WhereGroupAnd<QueryType> startWhereAnd();
	WhereGroupOr<QueryType> startWhereOr();
	QueryType where(String field, String operator, char value);
	QueryType where(String field, String operator, boolean value);
	QueryType where(String field, String operator, byte value);
	QueryType where(String field, String operator, double value);
	QueryType where(String field, String operator, float value);
	QueryType where(String field, String operator, int value);
	QueryType where(String field, String operator, long value);
	QueryType where(String field, String operator, short value);
	QueryType where(String field, String operator, Select query);
	QueryType where(String field, String operator, Object value);
	QueryType whereAnd(String field, String operator, char value);
	QueryType whereAnd(String field, String operator, boolean value);
	QueryType whereAnd(String field, String operator, byte value);
	QueryType whereAnd(String field, String operator, double value);
	QueryType whereAnd(String field, String operator, float value);
	QueryType whereAnd(String field, String operator, int value);
	QueryType whereAnd(String field, String operator, long value);
	QueryType whereAnd(String field, String operator, short value);
	QueryType whereAnd(String field, String operator, Select query);
	QueryType whereAnd(String field, String operator, Object value);
	QueryType whereOr(String field, String operator, char value);
	QueryType whereOr(String field, String operator, boolean value);
	QueryType whereOr(String field, String operator, byte value);
	QueryType whereOr(String field, String operator, double value);
	QueryType whereOr(String field, String operator, float value);
	QueryType whereOr(String field, String operator, int value);
	QueryType whereOr(String field, String operator, long value);
	QueryType whereOr(String field, String operator, short value);
	QueryType whereOr(String field, String operator, Select query);
	QueryType whereOr(String field, String operator, Object value);
	QueryType where(Object bean) throws DbQueryException;
	QueryType whereIncluded(Object bean, String[] includedFields) throws DbQueryException;
	QueryType whereExcluded(Object bean, String[] excludedFields) throws DbQueryException;
	QueryType whereFiltered(Object bean, String[] includedFields, String[] excludedFields) throws DbQueryException;

	void addWhereParameters(List<String> parameters);
}


