/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DataLinkUnknownSrcOutbeanException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class DataLinkUnknownSrcOutbeanException extends EngineException
{
	private static final long serialVersionUID = 1146826772273924406L;

	private String	mSiteDeclarationName = null;
	private String	mSrcOutbean = null;
	private String	mElementId = null;
	private String	mDestinationId = null;
	private boolean	mSnapback = false;

	public DataLinkUnknownSrcOutbeanException(String siteDeclarationName, String srcoutbean, String elementId, String destinationId, boolean snapback)
	{
		super("The site '"+siteDeclarationName+"' references an unknown source element outbean '"+srcoutbean+"' in the "+(!snapback ? "" : "snapback ")+"datalink that originates at the element '"+elementId+"'"+(null == destinationId ? "" : " towards the element'"+destinationId+"'")+".");
		
		mSiteDeclarationName = siteDeclarationName;
		mSrcOutbean = srcoutbean;
		mElementId = elementId;
		mDestinationId = destinationId;
		mSnapback = snapback;
	}
	
	public String getSiteDeclarationName()
	{
		return mSiteDeclarationName;
	}
	
	public String getSrcOutbean()
	{
		return mSrcOutbean;
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
