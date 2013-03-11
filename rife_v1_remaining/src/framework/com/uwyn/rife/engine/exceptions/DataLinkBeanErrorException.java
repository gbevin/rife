/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DataLinkBeanErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class DataLinkBeanErrorException extends EngineException
{
	private static final long serialVersionUID = -7866308716224142324L;

	private String	mSiteDeclarationName = null;
	private String	mElementId = null;
	private String	mDestinationId = null;
	private boolean	mSnapback = false;
	private String	mOutBean = null;
	private String	mInBean = null;

	public DataLinkBeanErrorException(String siteDeclarationName, String outbean, String elementId, String destinationId, boolean snapback, String inbean, Throwable cause)
	{
		super("The site '"+siteDeclarationName+"' has a "+(!snapback ? "" : "snapback ")+"datalink that originates at the element '"+elementId+"'"+(null == destinationId ? "" : " towards the element'"+destinationId+"' ")+" and transfers the properties of outbean '"+outbean+"' towards inbean '"+inbean+"'. An unexpected error occurred during the processing of these beans.", cause);
		
		mSiteDeclarationName = siteDeclarationName;
		mOutBean = outbean;
		mElementId = elementId;
		mDestinationId = destinationId;
		mSnapback = snapback;
		mInBean = inbean;
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
	
	public String getOutBean()
	{
		return mOutBean;
	}
	
	public String getInBean()
	{
		return mInBean;
	}
}
