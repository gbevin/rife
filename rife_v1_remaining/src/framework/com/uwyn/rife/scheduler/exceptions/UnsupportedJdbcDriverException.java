/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: UnsupportedJdbcDriverException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.exceptions;

public class UnsupportedJdbcDriverException extends RuntimeException
{
	private static final long serialVersionUID = -6964747195201024883L;
	
	private String mDriver = null;

	public UnsupportedJdbcDriverException(String driver, Throwable cause)
	{
		super("The JDBC driver '"+driver+"' isn't supported, certain functionalities will not function correctly.", cause);
		mDriver = driver;
	}

	public String getDriver()
	{
		return mDriver;
	}
}
