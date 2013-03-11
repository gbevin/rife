/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MissingResultsException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.exceptions;

import com.uwyn.rife.database.Datasource;

public class MissingResultsException extends DatabaseException
{
	private static final long serialVersionUID = 8032678779633066395L;

	private Datasource	mDatasource = null;

	public MissingResultsException(Datasource datasource)
	{
		super("Trying to fetch result from datasource '"+datasource.getUrl()+"' while no results are available.");
		mDatasource = datasource;
	}

	public Datasource getDatasource()
	{
		return mDatasource;
	}
}
