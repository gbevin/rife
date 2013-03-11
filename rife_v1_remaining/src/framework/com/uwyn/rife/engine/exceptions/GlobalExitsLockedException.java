/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GlobalExitsLockedException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class GlobalExitsLockedException extends EngineException
{
	private static final long serialVersionUID = 2759759935234692902L;

	private String	mDeclarationName = null;
	private String	mGlobalExitName = null;

	public GlobalExitsLockedException(String declarationName, String name)
	{
		super("The site '"+declarationName+"' couldn't add the global exit '"+name+"' since the global exits of the declaring group are locked.");
		
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
