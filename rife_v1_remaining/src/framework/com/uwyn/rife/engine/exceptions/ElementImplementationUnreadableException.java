/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementImplementationUnreadableException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ElementImplementationUnreadableException extends EngineException
{
	private static final long serialVersionUID = 1210447313550285158L;

	private String	mDeclarationName = null;
	private String	mImplementation = null;

	public ElementImplementationUnreadableException(String declarationName, String implementation, Throwable cause)
	{
		super("The implementation '"+implementation+"' of element '"+declarationName+"' couldn't be read.", cause);
		
		mDeclarationName = declarationName;
		mImplementation = implementation;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
	
	public String getImplementation()
	{
		return mImplementation;
	}
}
