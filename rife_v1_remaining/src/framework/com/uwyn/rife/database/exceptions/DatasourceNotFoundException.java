/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DatasourceNotFoundException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.exceptions;

public class DatasourceNotFoundException extends DatasourcesException
{
	private static final long serialVersionUID = 5512935424104175110L;

	private String	mDatasource = null;
	private String	mXmlPath = null;

	public DatasourceNotFoundException(String dataSource)
	{
		super("Couldn't find a valid resource for the datasource '"+dataSource+"'.");
		
		mDatasource = dataSource;
	}
	
	public DatasourceNotFoundException(String dataSource, String xmlPath)
	{
		super("Couldn't find a valid resource for the datasource '"+dataSource+"', tried xml path '"+xmlPath+"'.");
		
		mDatasource = dataSource;
		mXmlPath = xmlPath;
	}
	
	public String getDatasource()
	{
		return mDatasource;
	}
	
	public String getXmlPath()
	{
		return mXmlPath;
	}
}

