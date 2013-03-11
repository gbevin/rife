/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ResourceWriterErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.resources.exceptions;

public class ResourceWriterErrorException extends Exception
{
	private static final long serialVersionUID = -3012453669174294722L;

	public ResourceWriterErrorException(String message)
	{
		super(message);
	}

	public ResourceWriterErrorException(String message, Throwable e)
	{
		super(message, e);
	}
}
