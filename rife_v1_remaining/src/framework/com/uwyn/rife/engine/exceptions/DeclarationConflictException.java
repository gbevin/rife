/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DeclarationConflictException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public abstract class DeclarationConflictException extends EngineException
{
	private String	mDeclarationName = null;
	private String	mConflictName = null;

	public DeclarationConflictException(String message, String declarationName, String conflictName)
	{
		super(message);
		
		mDeclarationName = declarationName;
		mConflictName = conflictName;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
	
	public String getConflictName()
	{
		return mConflictName;
	}
}
