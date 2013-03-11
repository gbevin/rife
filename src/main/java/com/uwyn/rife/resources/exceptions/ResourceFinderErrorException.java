/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ResourceFinderErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.resources.exceptions;

public class ResourceFinderErrorException extends Exception
{
	private static final long serialVersionUID = 1319480895691642864L;

	public ResourceFinderErrorException(String message)
	{
		super(message);
	}

	public ResourceFinderErrorException(String message, Throwable e)
	{
		super(message, e);
	}
}
