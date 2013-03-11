/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementIdNotFoundInSiteException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ElementIdNotFoundInSiteException extends EngineException
{
	private static final long serialVersionUID = 2558093124162583579L;

	private String	mSiteId = null;
	private String	mId = null;

	public ElementIdNotFoundInSiteException(String siteId, String id)
	{
		super("The element id '"+id+"' couldn't be found, it was referenced in the site with id '"+siteId+"'.");
		
		mSiteId = siteId;
		mId = id;
	}
	
	public String getSiteId()
	{
		return mSiteId;
	}
	
	public String getId()
	{
		return mId;
	}
}
