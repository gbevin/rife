/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ValidationBuilderException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site.exceptions;

public class ValidationBuilderException extends RuntimeException
{
	private static final long serialVersionUID = -1875900584540609689L;

	public ValidationBuilderException(String message)
	{
		super(message);
	}

	public ValidationBuilderException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public ValidationBuilderException(Throwable cause)
	{
		super(cause);
	}
}
