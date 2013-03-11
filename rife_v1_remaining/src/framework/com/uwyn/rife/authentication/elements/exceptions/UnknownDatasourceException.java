/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: UnknownDatasourceException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.elements.exceptions;

import com.uwyn.rife.engine.exceptions.EngineException;

public class UnknownDatasourceException extends EngineException
{
	private static final long serialVersionUID = -4632344197992992433L;

	private String	mDatasource = null;
	
	public UnknownDatasourceException(String datasource)
	{
		super("The datasource '"+datasource+"' is not known to the system.");
		mDatasource = datasource;
	}
	
	public String getDatasource()
	{
		return mDatasource;
	}
}
