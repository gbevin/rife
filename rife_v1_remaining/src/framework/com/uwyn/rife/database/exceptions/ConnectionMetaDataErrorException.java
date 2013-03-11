/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ConnectionMetaDataErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.exceptions;

import com.uwyn.rife.database.Datasource;

public class ConnectionMetaDataErrorException extends DatabaseException
{
	private static final long serialVersionUID = 8314476636892309174L;

	private Datasource	mDatasource = null;

	public ConnectionMetaDataErrorException(Datasource datasource, Throwable cause)
	{
		super("Error while obtaining the metadata of the connection with url '"+datasource.getUrl()+"'.", cause);
		mDatasource = datasource;
	}

	public Datasource getDatasource()
	{
		return mDatasource;
	}
}
