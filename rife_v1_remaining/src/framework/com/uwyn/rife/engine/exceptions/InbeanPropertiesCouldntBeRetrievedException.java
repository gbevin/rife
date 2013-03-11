/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InbeanPropertiesCouldntBeRetrievedException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class InbeanPropertiesCouldntBeRetrievedException extends EngineException
{
	private static final long serialVersionUID = 2055360983476724648L;

	private String	mDeclarationName = null;
	private String	mClassName = null;
	
	public InbeanPropertiesCouldntBeRetrievedException(String declarationName, String className, Throwable e)
	{
		super("The element '"+declarationName+"' declared a input bean with class '"+className+"', but an error occurred while retrieving the properties.", e);
		
		mDeclarationName = declarationName;
		mClassName = className;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
	
	public String getClassName()
	{
		return mClassName;
	}
}
