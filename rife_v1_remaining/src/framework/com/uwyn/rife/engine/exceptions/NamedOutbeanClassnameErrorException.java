/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: NamedOutbeanClassnameErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class NamedOutbeanClassnameErrorException extends EngineException
{
	private static final long serialVersionUID = 7655065621545565140L;

	private String	mDeclarationName = null;
	private String	mOutbeanName = null;
	private String	mClassName = null;

	public NamedOutbeanClassnameErrorException(String declarationName, String outbeanName, String className)
	{
		super("The class '"+className+"' of the named outbean '"+outbeanName+"' of element '"+declarationName+"' couldn't be found.");
		
		mDeclarationName = declarationName;
		mOutbeanName = outbeanName;
		mClassName = className;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
	
	public String getOutbeanName()
	{
		return mOutbeanName;
	}
	
	public String getClassName()
	{
		return mClassName;
	}
}
