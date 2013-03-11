/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ReservedGlobalVarNameException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ReservedGlobalVarNameException extends EngineException
{
	private static final long serialVersionUID = -1543179660429222062L;

	private String	mDeclarationName = null;
	private String	mGlobalVarName = null;

	public ReservedGlobalVarNameException(String declarationName, String name)
	{
		super("The site '"+declarationName+"' couldn't add the global var '"+name+"' since the name is reserved.");
		
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
