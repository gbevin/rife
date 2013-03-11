/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ReadQueryString.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.capabilities.Capabilities;

/**
 * An instance of <code>ReadQueryString</code> can contain any kind of SQL
 * query for read purposes.
 * 
 * <p>This allows you to use any SQL together with the functionalities
 * that are provided by {@link com.uwyn.rife.database.DbQueryManager}
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.6
 */
public class ReadQueryString implements ReadQuery
{
	private String	mSql = null;
	
	/**
	 * Creates a new empty instance of <code>ReadQueryString</code>.
	 *
	 * @since 1.6
	 */
	public ReadQueryString()
	{
	}
	
	/**
	 * Creates a new instance of <code>ReadQueryString</code> with the
	 * specified SQL query.
	 *
	 * @param sql The SQL that should be executed by this query
	 * @since 1.6
	 */
	public ReadQueryString(String sql)
	{
		setSql(sql);
	}
	
	/**
	 * Replaces the SQL that is executed by this query.
	 *
	 * @param sql The SQL that should be executed by this query
	 * @return this <code>ReadQueryString</code> instance.
	 * @since 1.6
	 */
	public ReadQueryString sql(String sql)
	{
		setSql(sql);
		return this;
	}
	
	/**
	 * Replaces the SQL that is executed by this query.
	 *
	 * @param sql The SQL that should be executed by this query
	 * @since 1.6
	 */
	public void setSql(String sql)
	{
		mSql = sql;
	}
	
	public void clear()
	{
		mSql = null;
	}
	
	public String getSql()
	{
		return mSql;
	}
	
	public QueryParameters getParameters()
	{
		return null;
	}
	
	public Capabilities getCapabilities()
	{
		return null;
	}
	
	public void setExcludeUnsupportedCapabilities(boolean flag)
	{
	}
}
