/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: OutbeanClassNotFoundException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class OutbeanClassNotFoundException extends EngineException
{
	private static final long serialVersionUID = -7203380988377127608L;

	private String	mDeclarationName = null;
	private String	mClassName = null;
	
	public OutbeanClassNotFoundException(String declarationName, String className)
	{
		super("The element '"+declarationName+"' declared an output bean with class '"+className+"', but the class couldn't be found.");
		
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
