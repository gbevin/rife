/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ConnectionStatusErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.exceptions;

import com.uwyn.rife.database.Datasource;

public class ConnectionStatusErrorException extends DatabaseException
{
	private static final long serialVersionUID = -6733548295573208721L;

	private Datasource	mDatasource = null;

	public ConnectionStatusErrorException(Datasource datasource, Throwable cause)
	{
		super("Error while checking the status of the connection with url '"+datasource.getUrl()+"'.", cause);
		mDatasource = datasource;
	}

	public Datasource getDatasource()
	{
		return mDatasource;
	}
}
