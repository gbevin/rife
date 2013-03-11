/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: NamedInbeanInjectionException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class NamedInbeanInjectionException extends EngineException
{
	private static final long serialVersionUID = -2328136898217013756L;

	private String	mDeclarationName = null;
	private Class	mElementClass = null;
	private String	mInbeanName = null;

	public NamedInbeanInjectionException(String declarationName, Class elementClass, String inbeanName, Throwable e)
	{
		super("An error occurred while injecting the value for the named inbean '"+inbeanName +"' of element '"+declarationName+"' into class '"+elementClass.getName()+"'.", e);

		mDeclarationName = declarationName;
		mElementClass = elementClass;
		mInbeanName = inbeanName;
	}

	public String getDeclarationName()
	{
		return mDeclarationName;
	}

	public Class getElementClass()
	{
		return mElementClass;
	}

	public String getInbeanName()
	{
		return mInbeanName;
	}
}
