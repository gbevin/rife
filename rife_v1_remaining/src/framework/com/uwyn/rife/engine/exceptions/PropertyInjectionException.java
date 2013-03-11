/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: PropertyInjectionException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class PropertyInjectionException extends EngineException
{
	private static final long serialVersionUID = -4879554859976442737L;

	private String	mDeclarationName = null;
	private Class	mElementClass = null;
	private String	mPropertyName = null;

	public PropertyInjectionException(String declarationName, Class elementClass, String propertyName, Throwable e)
	{
		super("An error occurred while injecting the value for property '"+propertyName+"' of element '"+declarationName+"' into class '"+elementClass.getName()+"'.", e);
		
		mDeclarationName = declarationName;
		mElementClass = elementClass;
		mPropertyName = propertyName;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
	
	public Class getElementClass()
	{
		return mElementClass;
	}
	
	public String getPropertyName()
	{
		return mPropertyName;
	}
}
