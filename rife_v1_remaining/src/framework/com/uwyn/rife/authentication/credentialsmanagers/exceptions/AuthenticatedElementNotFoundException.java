/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AuthenticatedElementNotFoundException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers.exceptions;

import com.uwyn.rife.engine.exceptions.EngineException;

public class AuthenticatedElementNotFoundException extends EngineException
{
	private static final long serialVersionUID = -7377389371841870854L;
	
	private String	mElementId = null;
	
	public AuthenticatedElementNotFoundException(String elementId)
	{
		super("The element '"+elementId+"' couldn't be found in the site structure.");
		
		mElementId = elementId;
	}
	
	public String getElementId()
	{
		return mElementId;
	}
}

