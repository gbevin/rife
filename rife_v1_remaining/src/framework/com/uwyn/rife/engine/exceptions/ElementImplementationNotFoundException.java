/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementImplementationNotFoundException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ElementImplementationNotFoundException extends EngineException
{
	private static final long serialVersionUID = -1575963789140791422L;

	private String	mDeclarationName = null;
	private String	mImplementation = null;

	public ElementImplementationNotFoundException(String declarationName, String implementation, Throwable cause)
	{
		super("The implementation '"+implementation+"' of element '"+declarationName+"' couldn't be found.", cause);
		
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
