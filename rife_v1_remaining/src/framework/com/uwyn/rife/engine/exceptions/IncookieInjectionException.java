/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: IncookieInjectionException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class IncookieInjectionException extends EngineException
{
	private static final long serialVersionUID = -5335163897214624456L;

	private String	mDeclarationName = null;
	private Class	mElementClass = null;
	private String	mIncookieName = null;

	public IncookieInjectionException(String declarationName, Class elementClass, String incookieName, Throwable e)
	{
		super("An error occurred while injecting the values for incookie '"+incookieName +"' of element '"+declarationName+"' into class '"+elementClass.getName()+"'.", e);

		mDeclarationName = declarationName;
		mElementClass = elementClass;
		mIncookieName = incookieName;
	}

	public String getDeclarationName()
	{
		return mDeclarationName;
	}

	public Class getElementClass()
	{
		return mElementClass;
	}

	public String getIncookieName()
	{
		return mIncookieName;
	}
}
