/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ResponseWriteErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ResponseWriteErrorException extends EngineException
{
	private static final long serialVersionUID = -2521821080319953311L;

	public ResponseWriteErrorException(Throwable e)
	{
		super("An error occurred during the writing to the response output stream.", e);
	}
}
