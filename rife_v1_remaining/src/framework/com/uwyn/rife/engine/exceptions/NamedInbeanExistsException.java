/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: NamedInbeanExistsException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class NamedInbeanExistsException extends EngineException
{
	private static final long serialVersionUID = -2464890205814894965L;

	private String	mDeclarationName = null;
	private String	mInbeanName = null;

	public NamedInbeanExistsException(String declarationName, String inbeanName)
	{
		super("The element '"+declarationName+"' already contains the named inbean '"+inbeanName+"'.");
		
		mDeclarationName = declarationName;
		mInbeanName = inbeanName;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
	
	public String getInbeanName()
	{
		return mInbeanName;
	}
}
