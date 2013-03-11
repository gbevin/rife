/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementIdNotFoundSiteIdExistsException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ElementIdNotFoundSiteIdExistsException extends EngineException
{
	private static final long serialVersionUID = -7199884249916828341L;

	private String	mSiteId = null;
	private String	mId = null;

	public ElementIdNotFoundSiteIdExistsException(String siteId, String id)
	{
		super("The element id '"+id+"' was referenced in the site with id '"+siteId+"' and couldn't be found. A sub-site with this id exists however, but it hasn't got an arrival element. Maybe it should be defined.");
		
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
