/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ScriptErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ScriptErrorException extends EngineException
{
	private static final long serialVersionUID = -763100445680872006L;

	private String	mDeclarationName = null;

	public ScriptErrorException(String declarationName, Throwable cause)
	{
		super("The element '"+declarationName+"' produced an error while executing its script.", cause);
		
		mDeclarationName = declarationName;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
}
