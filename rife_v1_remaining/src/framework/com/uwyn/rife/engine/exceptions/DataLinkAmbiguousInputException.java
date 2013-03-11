/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DataLinkAmbiguousInputException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class DataLinkAmbiguousInputException extends EngineException
{
	private static final long serialVersionUID = 670428373989053830L;

	private String	mSiteDeclarationName = null;
	private String	mElementId = null;
	private String	mDestinationId = null;
	private boolean	mSnapback = false;

	public DataLinkAmbiguousInputException(String siteDeclarationName, String elementId, String destinationId, boolean snapback)
	{
		super("The site '"+siteDeclarationName+"' has an ambiguous input defined for the "+(!snapback ? "" : "snapback ")+"datalink that originates at the element '"+elementId+"'"+(null == destinationId ? "" : " towards the element'"+destinationId+"' ")+". It defines a destinput and a destinbean at the same time.");
		
		mSiteDeclarationName = siteDeclarationName;
		mElementId = elementId;
		mDestinationId = destinationId;
		mSnapback = snapback;
	}
	
	public String getSiteDeclarationName()
	{
		return mSiteDeclarationName;
	}
	
	public String getElementId()
	{
		return mElementId;
	}
	
	public String getDestinationId()
	{
		return mDestinationId;
	}
	
	public boolean getSnapback()
	{
		return mSnapback;
	}
}
