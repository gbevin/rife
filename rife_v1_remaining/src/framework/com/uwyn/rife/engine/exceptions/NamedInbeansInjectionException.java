/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: NamedInbeansInjectionException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class NamedInbeansInjectionException extends EngineException
{
	private static final long serialVersionUID = -5264389058576941183L;

	private String	mDeclarationName = null;
	private Class	mElementClass = null;

	public NamedInbeansInjectionException(String declarationName, Class elementClass, Throwable e)
	{
		super("An error occurred while injecting the named inbeans of element '"+declarationName+"' into class '"+elementClass.getName()+"'.", e);

		mDeclarationName = declarationName;
		mElementClass = elementClass;
	}

	public String getDeclarationName()
	{
		return mDeclarationName;
	}

	public Class getElementClass()
	{
		return mElementClass;
	}
}
