/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InputsDeserializationException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class InputsDeserializationException extends EngineException
{
	static final long serialVersionUID = -7555003059031919239L;
	
	private String	mDeclarationName = null;
	private String	mInputName = null;

	public InputsDeserializationException(String declarationName, String inputName, Throwable e)
	{
		super("An error occurred while deserializing the input '"+inputName+"' of element '"+declarationName+"'.", e);
		
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
