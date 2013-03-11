/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ExitTargetUrlMissingException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ExitTargetUrlMissingException extends EngineException
{
	private static final long serialVersionUID = 1308469860863411697L;

	private String	mSourceDeclarationName = null;
	private String	mExitName = null;
	private String	mTargetDeclarationName = null;

	public ExitTargetUrlMissingException(String sourceDeclarationName, String exitName, String targetDeclarationName)
	{
		super("The exit '"+exitName+"' of the element '"+sourceDeclarationName+"' has a flow link to the element '"+targetDeclarationName+"'. The latter hasn't been mapped to an URL in the site structure, so it's impossible to generate a direct URL for the exit. Should the flowlink point to another element in your site, then you're probably in an inheritance structure and the element without an URL is the actual target of the request. Either register is with an URL or cancel the inheritance in the flowlink.");
		
		mSourceDeclarationName = sourceDeclarationName;
		mExitName = exitName;
		mTargetDeclarationName = targetDeclarationName;
	}
	
	public String getSourceDeclarationName()
	{
		return mSourceDeclarationName;
	}
	
	public String getExitName()
	{
		return mExitName;
	}
	
	public String getTargetDeclarationName()
	{
		return mTargetDeclarationName;
	}
}
