/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MultipartRequestException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class MultipartRequestException extends EngineException
{
	private static final long serialVersionUID = 2383362830269664082L;

	public MultipartRequestException()
	{
		super();
	}

	public MultipartRequestException(String message)
	{
		super(message);
	}

	public MultipartRequestException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public MultipartRequestException(Throwable cause)
	{
		super(cause);
	}
}
