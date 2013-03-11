/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com> and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DeleteQuery.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic;

import com.uwyn.rife.database.capabilities.Capabilities;
import com.uwyn.rife.database.exceptions.DbQueryException;
import com.uwyn.rife.database.queries.AbstractWhereDelegateQuery;
import com.uwyn.rife.database.queries.Delete;
import com.uwyn.rife.database.queries.Query;
import com.uwyn.rife.database.queries.QueryParameters;

public class DeleteQuery extends AbstractWhereDelegateQuery<DeleteQuery, Delete> implements Query, Cloneable
{
	private Delete mDelegatePristine = null;
		
	public String toString()
	{
		return getSql();
	}
	
	public DeleteQuery clone()
	{
		return new DeleteQuery(mDelegate.clone());
	}
	
	public DeleteQuery(Delete query)
	{
		super(query.clone());
		mDelegatePristine = query.clone();
	}
	
	public String getSql()
	throws DbQueryException
	{
		return mDelegate.getSql();
	}
	
	public void clear()
	{
		mDelegate = mDelegatePristine.clone();
	}
	
	public QueryParameters getParameters()
	{
		return mDelegate.getParameters();
	}
	
	public Capabilities getCapabilities()
	{
		return mDelegate.getCapabilities();
	}
	
	public void setExcludeUnsupportedCapabilities(boolean flag)
	{
		mDelegate.setExcludeUnsupportedCapabilities(flag);
	}

	public String getFrom()
	{
		return mDelegate.getFrom();
	}
}

