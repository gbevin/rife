/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GlobalVarExistsException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class GlobalVarExistsException extends EngineException
{
	private static final long serialVersionUID = 4561576510047327626L;

	private String	mDeclarationName = null;
	private String	mGlobalVarName = null;

	public GlobalVarExistsException(String declarationName, String name)
	{
		super("The site '"+declarationName+"' already contains the global var '"+name+"' in the group that declares it.");
		
		mDeclarationName = declarationName;
		mGlobalVarName = name;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}

	public String getGlobalVarName()
	{
		return mGlobalVarName;
	}
}
