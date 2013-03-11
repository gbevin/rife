/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Jdk15RequiredForAnnotationsException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class Jdk15RequiredForAnnotationsException extends EngineException
{
	static final long serialVersionUID = 7036929351389355149L;
	
	private String	mSiteDeclarationName = null;
	private String	mDeclarationName = null;

	public Jdk15RequiredForAnnotationsException(String siteDeclarationName, String declarationName)
	{
		super("Element '"+declarationName+"' in site '"+siteDeclarationName+"' tries to use annotations for its declaration, however at least Java v1.5 is required for that. You are currently running Java v"+System.getProperty("java.version")+".");

		mSiteDeclarationName = siteDeclarationName;
		mDeclarationName = declarationName;
	}

	public String getSiteDeclarationName()
	{
		return mSiteDeclarationName;
	}

	public String getDeclarationName()
	{
		return mDeclarationName;
	}
}
