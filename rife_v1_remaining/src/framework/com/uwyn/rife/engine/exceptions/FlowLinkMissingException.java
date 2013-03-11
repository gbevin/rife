/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FlowLinkMissingException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class FlowLinkMissingException extends EngineException
{
	private static final long serialVersionUID = 7537520664896017723L;

	private String	mSourceDeclarationName = null;
	private String	mTargetDeclarationName = null;

	public FlowLinkMissingException(String sourceDeclarationName, String targetDeclarationName)
	{
		super("Impossible to create a data link from element '"+sourceDeclarationName+"' to element '"+targetDeclarationName+"' since no flow link is available.");
		
		mSourceDeclarationName = sourceDeclarationName;
		mTargetDeclarationName = targetDeclarationName;
	}
	
	public String getSourceDeclarationName()
	{
		return mSourceDeclarationName;
	}
	
	public String getTargetDeclarationName()
	{
		return mTargetDeclarationName;
	}
}
