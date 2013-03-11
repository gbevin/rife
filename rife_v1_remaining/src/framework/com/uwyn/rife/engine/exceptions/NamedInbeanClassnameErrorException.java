/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: NamedInbeanClassnameErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class NamedInbeanClassnameErrorException extends EngineException
{
	private static final long serialVersionUID = 3072269558888090287L;

	private String	mDeclarationName = null;
	private String	mInbeanName = null;
	private String	mClassName = null;

	public NamedInbeanClassnameErrorException(String declarationName, String inbeanName, String className)
	{
		super("The class '"+className+"' of the named inbean '"+inbeanName+"' of element '"+declarationName+"' couldn't be found.");
		
		mDeclarationName = declarationName;
		mInbeanName = inbeanName;
		mClassName = className;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
	
	public String getInbeanName()
	{
		return mInbeanName;
	}
	
	public String getClassName()
	{
		return mClassName;
	}
}
