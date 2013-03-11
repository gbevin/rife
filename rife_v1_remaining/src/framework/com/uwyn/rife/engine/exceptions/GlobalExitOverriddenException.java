/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GlobalExitOverriddenException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class GlobalExitOverriddenException extends EngineException
{
	private static final long serialVersionUID = -2058666225204891204L;

	private String	mDeclarationName = null;
	private String	mGlobalExitName = null;

	public GlobalExitOverriddenException(String declarationName, String name)
	{
		super("The site '"+declarationName+"' contains the global exit '"+name+"' which has also been defined by its parent site or another containing group.");
		
		mDeclarationName = declarationName;
		mGlobalExitName = name;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
	
	public String getGlobalExitName()
	{
		return mGlobalExitName;
	}
}
