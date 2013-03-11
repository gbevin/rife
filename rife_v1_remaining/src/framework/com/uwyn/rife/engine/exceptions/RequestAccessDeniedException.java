/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RequestAccessDeniedException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class RequestAccessDeniedException extends EngineException
{
	private static final long serialVersionUID = 6953469185303741296L;

	public RequestAccessDeniedException()
	{
		super("The access to the request is disabled in this context.");
	}
}
