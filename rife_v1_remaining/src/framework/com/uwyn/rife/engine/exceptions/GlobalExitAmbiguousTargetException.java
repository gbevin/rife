/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GlobalExitAmbiguousTargetException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class GlobalExitAmbiguousTargetException extends EngineException
{
	private static final long serialVersionUID = 2412449349108378995L;

	private String	mSiteDeclarationName = null;
	private String	mGlobalExitName = null;

	public GlobalExitAmbiguousTargetException(String siteDeclarationName, String exitName)
	{
		super("The site '"+siteDeclarationName+"' has ambiguous targets defined for the global exit '"+exitName+"'. Pointing to an element id, being reflective or snapping back can't be defined at the same time.");
		
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
