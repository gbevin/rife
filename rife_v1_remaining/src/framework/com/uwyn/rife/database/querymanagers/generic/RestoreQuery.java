/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com> and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RestoreQuery.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic;


import com.uwyn.rife.database.capabilities.Capabilities;
import com.uwyn.rife.database.queries.AbstractWhereDelegateQuery;
import com.uwyn.rife.database.queries.QueryParameters;
import com.uwyn.rife.database.queries.ReadQuery;
import com.uwyn.rife.database.queries.Select;

public class RestoreQuery extends AbstractWhereDelegateQuery<RestoreQuery, Select> implements ReadQuery, Cloneable
{
	private Select mDelegatePristine = null;
	
	public String toString()
	{
		return getSql();
	}
	
	public RestoreQuery clone()
	{
		return new RestoreQuery(mDelegate.clone());
	}
	
	public RestoreQuery(Select query)
	{
		super(query.clone());
		mDelegatePristine = query.clone();
	}
	
	public void clear()
	{
		mDelegate = mDelegatePristine.clone();
	}
	
	public Capabilities getCapabilities()
	{
		return mDelegate.getCapabilities();
	}
	
	public void setExcludeUnsupportedCapabilities(boolean flag)
	{
		mDelegate.setExcludeUnsupportedCapabilities(flag);
	}
	
	public QueryParameters getParameters()
	{
		return mDelegate.getParameters();
	}
	
	public String getSql()
	{
		return mDelegate.getSql();
	}
	
	public RestoreQuery distinctOn(String column)
	{
		mDelegate.distinctOn(column);
		
		return this;
	}
	
	public RestoreQuery distinctOn(String[] columns)
	{
		mDelegate.distinctOn(columns);
		
		return this;
	}
	
	public String getFrom()
	{
		return mDelegate.getFrom();
	}
	
	public RestoreQuery join(String table)
	{
		mDelegate.join(table);
		
		return this;
	}
	
	public RestoreQuery joinCross(String table)
	{
		mDelegate.joinCross(table);
		
		return this;
	}
	
	public RestoreQuery joinCustom(String customJoin)
	{
		mDelegate.joinCustom(customJoin);
		
		return this;
	}
	
	public RestoreQuery joinInner(String table, Select.JoinCondition condition, String conditionExpression)
	{
		mDelegate.joinInner(table, condition, conditionExpression);
		
		return this;
	}
	
	public RestoreQuery joinOuter(String table, Select.JoinType type, Select.JoinCondition condition, String conditionExpression)
	{
		mDelegate.joinOuter(table, type, condition, conditionExpression);
		
		return this;
	}
	
	public RestoreQuery limit(int limit)
	{
		mDelegate.limit(limit);
		
		return this;
	}
	
	public RestoreQuery offset(int offset)
	{
		mDelegate.offset(offset);
		
		return this;
	}
	
	public RestoreQuery orderBy(String column)
	{
		mDelegate.orderBy(column);
		
		return this;
	}
	
	public RestoreQuery orderBy(String column, Select.OrderByDirection direction)
	{
		mDelegate.orderBy(column, direction);
		
		return this;
	}
	
	public RestoreQuery union(Select union)
	{
		mDelegate.union(union);
		
		return this;
	}
	
	public RestoreQuery union(String union)
	{
		mDelegate.union(union);
		
		return this;
	}
	
	public RestoreQuery field(String field)
	{
		mDelegate.field(field);
		
		return this;
	}
	
    public RestoreQuery fields(Class bean)
	{
		mDelegate.fields(bean);
		
		return this;
	}
	
    public RestoreQuery fieldsExcluded(Class bean, String... excluded)
	{
		mDelegate.fieldsExcluded(bean, excluded);
		
		return this;
	}
	
    public RestoreQuery fields(String table, Class bean)
	{
		mDelegate.fields(table, bean);
		
		return this;
	}
	
    public RestoreQuery fieldsExcluded(String table, Class bean, String... excluded)
	{
		mDelegate.fieldsExcluded(table, bean, excluded);
		
		return this;
	}
	
    public RestoreQuery fields(String... fields)
	{
		mDelegate.fields(fields);
		
		return this;
	}
	
	public RestoreQuery field(String alias, Select query)
	{
		mDelegate.field(alias, query);
		
		return this;
	}
	
	public RestoreQuery fieldSubselect(Select query)
	{
		mDelegate.fieldSubselect(query);
		
		return this;
	}
}
