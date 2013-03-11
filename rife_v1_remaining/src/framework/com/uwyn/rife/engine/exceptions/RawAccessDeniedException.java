/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RawAccessDeniedException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class RawAccessDeniedException extends EngineException
{
	private static final long serialVersionUID = -5766881025444769980L;

	public RawAccessDeniedException()
	{
		super("Raw access to the request and the context is disabled. It has to be explicitely enabled before being granted access to these features.");
	}
}
