/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GlobalExitTargetRequiredException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class GlobalExitTargetRequiredException extends EngineException
{
	private static final long serialVersionUID = -8792176950098590819L;

	private String	mSiteDeclarationName = null;
	private String	mGlobalExitName = null;

	public GlobalExitTargetRequiredException(String siteDeclarationName, String exitName)
	{
		super("The site '"+siteDeclarationName+"' defines the global exit '"+exitName+"', but no target has been defined for it. Point it to an element id, make it reflective or make it snap back.");
		
		mSiteDeclarationName = siteDeclarationName;
		mGlobalExitName = exitName;
	}
	
	public String getSiteDeclarationName()
	{
		return mSiteDeclarationName;
	}
	
	public String getGlobalExitName()
	{
		return mGlobalExitName;
	}
}
