/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: UnsupportedSqlFeatureException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.exceptions;

public class UnsupportedSqlFeatureException extends DbQueryException
{
	private static final long serialVersionUID = 6597682956243876788L;

	private String	mFeature = null;
	private String	mDriver = null;
	
	public UnsupportedSqlFeatureException(String feature, String driver)
	{
		super("The '"+feature+"' feature isn't supported by the driver '"+driver+"'.");
		mFeature = feature;
		mDriver = driver;
	}

	public String getFeature()
	{
		return mFeature;
	}

	public String getDriver()
	{
		return mDriver;
	}
}
