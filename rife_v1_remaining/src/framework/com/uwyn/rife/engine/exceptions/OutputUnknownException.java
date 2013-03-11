/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: OutputUnknownException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class OutputUnknownException extends EngineException
{
	private static final long serialVersionUID = -5365279076731735418L;

	private String	mDeclarationName = null;
	private String	mOutputName = null;

	public OutputUnknownException(String declarationName, String outputName)
	{
		super("The element '"+declarationName+"' doesn't contain output '"+outputName+"'.");
		
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
