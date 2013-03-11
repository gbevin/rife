/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DataLinkTargetRequiredException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class DataLinkTargetRequiredException extends EngineException
{
	private static final long serialVersionUID = -2716560329689011469L;

	private String	mSiteDeclarationName = null;
	private String	mElementId = null;
	private String	mOutputName = null;
	private String	mOutbeanName = null;
	private String	mInputName = null;
	private String	mInbeanName = null;

	public DataLinkTargetRequiredException(String siteDeclarationName, String elementId, String outputName, String outbeanName, String inputName, String inbeanName)
	{
		super("The site '"+siteDeclarationName+"' has no target defined for the datalink that originates at the "+(null == outputName ? "outbean '"+outbeanName+"'" : "output '"+outputName+"'")+" of element '"+elementId+"'"+(null == inputName && null == inbeanName ? "" : " towards "+(null == inputName ? "inbean '"+inbeanName+"'" : "input '"+inputName+"'"))+", point it to an element id or make it snap back.");
		
		mSiteDeclarationName = siteDeclarationName;
		mElementId = elementId;
		mOutputName = outputName;
		mOutbeanName = outbeanName;
		mInputName = inputName;
		mInbeanName = inbeanName;
	}
	
	public String getSiteDeclarationName()
	{
		return mSiteDeclarationName;
	}
	
	public String getElementId()
	{
		return mElementId;
	}
	
	public String getOutputName()
	{
		return mOutputName;
	}
	
	public String getOutbeanName()
	{
		return mOutbeanName;
	}
	
	public String getInputName()
	{
		return mInputName;
	}
	
	public String getInbeanName()
	{
		return mInbeanName;
	}
}
