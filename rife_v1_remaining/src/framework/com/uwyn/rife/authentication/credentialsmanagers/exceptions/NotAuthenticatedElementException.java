/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com) and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: NotAuthenticatedElementException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers.exceptions;

import com.uwyn.rife.engine.exceptions.EngineException;

public class NotAuthenticatedElementException extends EngineException
{
	private static final long serialVersionUID = -4829721163927481874L;

	private String	mElementId = null;
	
	public NotAuthenticatedElementException(String elementId)
	{
		super("The element '"+elementId+"' is not an authenticated element, so it's impossible to retrieve its credentials manager.");
		
		mElementId = elementId;
	}
	
	public String getElementId()
	{
		return mElementId;
	}
}

