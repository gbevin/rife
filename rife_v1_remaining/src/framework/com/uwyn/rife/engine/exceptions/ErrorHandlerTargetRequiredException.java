/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ErrorHandlerTargetRequiredException.java 3928 2008-04-22 16:25:18Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ErrorHandlerTargetRequiredException extends EngineException
{
	private static final long serialVersionUID = -422701037316698501L;

	private String	mSiteDeclarationName = null;

	public ErrorHandlerTargetRequiredException(String siteDeclarationName)
	{
		super("The site '"+siteDeclarationName+"' defines an error handler but no target has been defined for it, you have to point it to an element id.");

		mSiteDeclarationName = siteDeclarationName;
	}

	public String getSiteDeclarationName()
	{
		return mSiteDeclarationName;
	}
}