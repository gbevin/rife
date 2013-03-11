/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FlowLinkTargetRequiredException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class FlowLinkTargetRequiredException extends EngineException
{
	private static final long serialVersionUID = 3846307205481834149L;

	private String	mSiteDeclarationName = null;
	private String	mElementId = null;
	private String	mExitName = null;

	public FlowLinkTargetRequiredException(String siteDeclarationName, String elementId, String exitName)
	{
		super("The site '"+siteDeclarationName+"' has no target defined for the flowlink that originates at the exit '"+exitName+"' of element '"+elementId+"', point it to an element id or make it snap back.");
		
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
