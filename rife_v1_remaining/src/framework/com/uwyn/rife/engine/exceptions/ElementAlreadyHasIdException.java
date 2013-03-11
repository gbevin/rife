/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementAlreadyHasIdException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ElementAlreadyHasIdException extends EngineException
{
	private static final long serialVersionUID = 1793544495379968193L;

	private String	mDeclarationName = null;
	private String	mId = null;

	public ElementAlreadyHasIdException(String declarationName, String id)
	{
		super("The element '"+declarationName+"' already has the id '"+id+"'.");
		
		mDeclarationName = declarationName;
		mId = id;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
	
	public String getId()
	{
		return mId;
	}
}
