/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InheritsSelfException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class InheritsSelfException extends EngineException
{
	private static final long serialVersionUID = -6393879349604134577L;

	private String	mSiteDeclarationName = null;

	public InheritsSelfException(String siteDeclarationName)
	{
		super("The element '"+siteDeclarationName+"' inherits from itself.");
		
		mSiteDeclarationName = siteDeclarationName;
	}
	
	public String getSiteDeclarationName()
	{
		return mSiteDeclarationName;
	}
}
