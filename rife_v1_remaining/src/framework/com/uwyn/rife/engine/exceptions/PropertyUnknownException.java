/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: PropertyUnknownException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class PropertyUnknownException extends EngineException
{
	private static final long serialVersionUID = -9171475759495353565L;

	private String	mDeclarationName = null;
	private String	mPropertyName = null;

	public PropertyUnknownException(String declarationName, String propertyName)
	{
		super("The element '"+declarationName+"' doesn't contain property '"+propertyName+"'.");
		
		mDeclarationName = declarationName;
		mPropertyName = propertyName;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
	
	public String getPropertyName()
	{
		return mPropertyName;
	}
}
