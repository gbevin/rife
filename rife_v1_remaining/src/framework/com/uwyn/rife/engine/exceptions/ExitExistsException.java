/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ExitExistsException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ExitExistsException extends EngineException
{
	private static final long serialVersionUID = 2502365917596013599L;

	private String	mDeclarationName = null;
	private String	mExitName = null;

	public ExitExistsException(String declarationName, String exitName)
	{
		super("The element '"+declarationName+"' already contains exit '"+exitName+"'.");
		
		mDeclarationName = declarationName;
		mExitName = exitName;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
	
	public String getExitName()
	{
		return mExitName;
	}
}
