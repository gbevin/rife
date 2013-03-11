/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ReservedOutputNameException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ReservedOutputNameException extends EngineException
{
	private static final long serialVersionUID = 1028284937120087676L;

	private String	mDeclarationName = null;
	private String	mOutputName = null;

	public ReservedOutputNameException(String declarationName, String outputName)
	{
		super("The output '"+outputName+"' is a reserved name and thus can't be added to the element '"+declarationName+"'.");
		
		mDeclarationName = declarationName;
		mOutputName = outputName;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
	
	public String getOutputName()
	{
		return mOutputName;
	}
}
