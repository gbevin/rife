/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InputUnknownException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class InputUnknownException extends EngineException
{
	private static final long serialVersionUID = -2474073430270815486L;

	private String	mDeclarationName = null;
	private String	mInputName = null;

	public InputUnknownException(String declarationName, String inputName)
	{
		super("The element '"+declarationName+"' doesn't contain input '"+inputName+"'.");
		
		mDeclarationName = declarationName;
		mInputName = inputName;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
	
	public String getInputName()
	{
		return mInputName;
	}
}
