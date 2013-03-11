/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DataLinkAmbiguousOutputException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class DataLinkAmbiguousOutputException extends EngineException
{
	private static final long serialVersionUID = 8981240207273828414L;

	private String	mSiteDeclarationName = null;
	private String	mElementId = null;

	public DataLinkAmbiguousOutputException(String siteDeclarationName, String elementId)
	{
		super("The site '"+siteDeclarationName+"' has an ambiguous output defined for a datalink that originates at the element '"+elementId+"'. It defines a srcoutput and a srcoutbean at the same time.");
		
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
