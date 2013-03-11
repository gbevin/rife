/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FlowLinkSpecificDataLinkDestIdSpecifiedException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class FlowLinkSpecificDataLinkDestIdSpecifiedException extends EngineException
{
	private static final long serialVersionUID = -3645641138050695328L;

	private String	mSiteDeclarationName = null;
	private String	mElementId = null;
	private String	mExitName = null;

	public FlowLinkSpecificDataLinkDestIdSpecifiedException(String siteDeclarationName, String elementId, String exitName)
	{
		super("The site '"+siteDeclarationName+"' has a destination ID specified for a datalink of the flowlink that originates at the exit '"+exitName+"' of element '"+elementId+"'. This is redundant and can lead to errors.");
		
		mSiteDeclarationName = siteDeclarationName;
		mElementId = elementId;
		mExitName = exitName;
	}
	
	public String getSiteDeclarationName()
	{
		return mSiteDeclarationName;
	}
	
	public String getElementId()
	{
		return mElementId;
	}
	
	public String getExitName()
	{
		return mExitName;
	}
}
