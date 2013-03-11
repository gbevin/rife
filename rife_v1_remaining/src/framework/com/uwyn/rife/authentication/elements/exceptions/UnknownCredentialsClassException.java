/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: UnknownCredentialsClassException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.elements.exceptions;

import com.uwyn.rife.engine.exceptions.EngineException;

public class UnknownCredentialsClassException extends EngineException
{
	private static final long serialVersionUID = 3489576185667235013L;
	
	private String	mCredentialsClassName = null;
	
	public UnknownCredentialsClassException(String className, Throwable e)
	{
		super("The credentials class '"+className+"' is not known to the system.", e);
		mCredentialsClassName = className;
	}
	
	public String getCredentialsClassName()
	{
		return mCredentialsClassName;
	}
}
