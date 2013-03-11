/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ValidationException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site.exceptions;

public class ValidationException extends RuntimeException
{
	private static final long serialVersionUID = -1743384767497780026L;

	public ValidationException(String message)
	{
		super(message);
	}

	public ValidationException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public ValidationException(Throwable cause)
	{
		super(cause);
	}
}
