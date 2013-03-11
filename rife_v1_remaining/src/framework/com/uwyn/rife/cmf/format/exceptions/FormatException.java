/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FormatException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.format.exceptions;

public class FormatException extends Exception
{
	private static final long serialVersionUID = -3292520395011984387L;

	public FormatException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public FormatException(Throwable cause)
	{
		super(cause);
	}
}
