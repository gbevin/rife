/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: NamedGlobalBeanExistsException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class NamedGlobalBeanExistsException extends EngineException
{
	private static final long serialVersionUID = -8921721160034278965L;

	private String	mDeclarationName = null;
	private String	mGlobalBeanName = null;

	public NamedGlobalBeanExistsException(String declarationName, String inbeanName)
	{
		super("The site '"+declarationName+"' already contains the named inbean '"+inbeanName+"' in the declaring group.");
		
		mDeclarationName = declarationName;
		mGlobalBeanName = inbeanName;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
	
	public String getGlobalBeanName()
	{
		return mGlobalBeanName;
	}
}
