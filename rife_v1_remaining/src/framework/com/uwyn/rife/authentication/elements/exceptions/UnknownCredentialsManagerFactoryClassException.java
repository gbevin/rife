/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com> and
 * Steven Grimm <koreth[remove] at midwinter dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: UnknownCredentialsClassException.java 3308 2006-06-15 18:54:14Z gbevin $
 */
package com.uwyn.rife.authentication.elements.exceptions;

import com.uwyn.rife.engine.exceptions.EngineException;

public class UnknownCredentialsManagerFactoryClassException extends EngineException
{
	private static final long serialVersionUID = 2327443941218492096L;
	
	private String	mFactoryClassName = null;
	
	public UnknownCredentialsManagerFactoryClassException(String className, Throwable e)
	{
		super("The credentials manager factory class '"+className+"' is not known to the system.", e);
		mFactoryClassName = className;
	}
	
	public String getManagerClassName()
	{
		return mFactoryClassName;
	}
}
