/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InputExistsException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class InputExistsException extends EngineException
{
	private static final long serialVersionUID = 2851054259398084802L;

	private String	mDeclarationName = null;
	private String	mInputName = null;

	public InputExistsException(String declarationName, String inputName)
	{
		super("The element '"+declarationName+"' already contains input '"+inputName+"'.");
		
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
