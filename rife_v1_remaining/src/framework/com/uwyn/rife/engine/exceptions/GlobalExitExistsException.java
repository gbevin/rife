/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GlobalExitExistsException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class GlobalExitExistsException extends EngineException
{
	private static final long serialVersionUID = 3987951428057161159L;

	private String	mDeclarationName = null;
	private String	mGlobalExitName = null;

	public GlobalExitExistsException(String declarationName, String name)
	{
		super("The site '"+declarationName+"' already contains global exit '"+name+"' in the group that declares it.");
		
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
