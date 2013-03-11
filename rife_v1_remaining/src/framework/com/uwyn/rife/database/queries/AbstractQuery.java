/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AbstractQuery.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.Datasource;

abstract class AbstractQuery implements Query, Cloneable
{
	protected Datasource	mDatasource = null;
	protected String		mSql = null;
	protected boolean		mExcludeUnsupportedCapabilities = false;

	private AbstractQuery()
	{
	}

	protected AbstractQuery(Datasource datasource)
	{
		assert datasource != null;
		
		mDatasource = datasource;
	}

	public Datasource getDatasource()
	{
		return mDatasource;
	}

	public QueryParameters getParameters()
	{
		return null;
	}

	public void setExcludeUnsupportedCapabilities(boolean flag)
	{
		mExcludeUnsupportedCapabilities = flag;
	}

	public void clear()
	{
		mSql = null;
	}

	protected void clearGenerated()
	{
		mSql = null;
	}

	public String toString()
	{
		return getSql();
	}

	public AbstractQuery clone()
	{
        AbstractQuery new_instance = null;
		try
		{
			new_instance = (AbstractQuery)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			new_instance = null;
		}

		return new_instance;
	}
}

