/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DataLinkOutputRequiredException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class DataLinkOutputRequiredException extends EngineException
{
	private static final long serialVersionUID = -4257308390061488574L;

	private String	mSiteDeclarationName = null;
	private String	mElementId = null;

	public DataLinkOutputRequiredException(String siteDeclarationName, String elementId)
	{
		super("The site '"+siteDeclarationName+"' has no output defined for a datalink that originates at the element '"+elementId+"', provide either a srcoutput or a srcoutbean.");
		
		mSiteDeclarationName = siteDeclarationName;
		mElementId = elementId;
	}
	
	public String getSiteDeclarationName()
	{
		return mSiteDeclarationName;
	}
	
	public String getElementId()
	{
		return mElementId;
	}
}
