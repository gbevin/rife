/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: EngineException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class EngineException extends RuntimeException
{
	private static final long serialVersionUID = -2808233897005420164L;

	public EngineException()
	{
		super();
	}

	public EngineException(String message)
	{
		super(message);
	}

	public EngineException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public EngineException(Throwable cause)
	{
		super(cause);
	}
}
