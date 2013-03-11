/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ExitUnknownException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ExitUnknownException extends EngineException
{
	private static final long serialVersionUID = 6298929690216222927L;

	private String	mDeclarationName = null;
	private String	mExitName = null;

	public ExitUnknownException(String declarationName, String exitName)
	{
		super("The element '"+declarationName+"' doesn't contain exit '"+exitName+"'.");
		
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
