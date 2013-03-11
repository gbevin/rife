/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InputInjectionException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class InputInjectionException extends EngineException
{
	private static final long serialVersionUID = 1187336359957438909L;

	private String	mDeclarationName = null;
	private Class	mElementClass = null;
	private String	mInputName = null;

	public InputInjectionException(String declarationName, Class elementClass, String inputName, Throwable e)
	{
		super("An error occurred while injecting the values for input '"+inputName+"' of element '"+declarationName+"' into class '"+elementClass.getName()+"'.", e);
		
		mDeclarationName = declarationName;
		mElementClass = elementClass;
		mInputName = inputName;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
	
	public Class getElementClass()
	{
		return mElementClass;
	}
	
	public String getInputName()
	{
		return mInputName;
	}
}
