/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com) and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CountQuery.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic;

import com.uwyn.rife.database.capabilities.Capabilities;
import com.uwyn.rife.database.queries.AbstractWhereDelegateQuery;
import com.uwyn.rife.database.queries.QueryParameters;
import com.uwyn.rife.database.queries.ReadQuery;
import com.uwyn.rife.database.queries.Select;

public class CountQuery extends AbstractWhereDelegateQuery<CountQuery, Select> implements ReadQuery, Cloneable
{
	private Select mDelegatePristine = null;
		
	public String toString()
	{
		return getSql();
	}
	
	public CountQuery clone()
	{
		return new CountQuery(mDelegate.clone());
	}
	
	public CountQuery(Select query)
	{
		super(query.clone());
		mDelegatePristine = query.clone();
	}
	
	public void clear()
	{
		mDelegate = mDelegatePristine.clone();
	}

	public String getFrom()
	{
		return mDelegate.getFrom();
	}

	public QueryParameters getParameters()
	{
		return mDelegate.getParameters();
	}

	public String getSql()
	{
		return mDelegate.getSql();
	}

	public Capabilities getCapabilities()
	{
		return mDelegate.getCapabilities();
	}
	
	public void setExcludeUnsupportedCapabilities(boolean flag)
	{
		mDelegate.setExcludeUnsupportedCapabilities(flag);
	}
	
	public CountQuery join(String table)
	{
		mDelegate.join(table);
		
		return this;
	}

	public CountQuery joinCross(String table)
	{
		mDelegate.joinCross(table);
		
		return this;
	}

	public CountQuery joinCustom(String customJoin)
	{
		mDelegate.joinCustom(customJoin);
		
		return this;
	}

	public CountQuery joinInner(String table, Select.JoinCondition condition, String conditionExpression)
	{
		mDelegate.joinInner(table, condition, conditionExpression);
		
		return this;
	}

	public CountQuery joinOuter(String table, Select.JoinType type, Select.JoinCondition condition, String conditionExpression)
	{
		mDelegate.joinOuter(table, type, condition, conditionExpression);
		
		return this;
	}
	
	public CountQuery union(Select union)
	{
		mDelegate.union(union);
		
		return this;
	}

	public CountQuery union(String union)
	{
		mDelegate.union(union);
		
		return this;
	}

	public Select getDelegate()
	{
		return mDelegate;
	}
}
