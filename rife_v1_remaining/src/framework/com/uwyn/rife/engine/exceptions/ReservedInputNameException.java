/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ReservedInputNameException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ReservedInputNameException extends EngineException
{
	private static final long serialVersionUID = -2796563561959164621L;

	private String	mDeclarationName = null;
	private String	mInputName = null;

	public ReservedInputNameException(String declarationName, String inputName)
	{
		super("The input '"+inputName+"' is a reserved name and thus can't be added to the element '"+declarationName+"'.");
		
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
