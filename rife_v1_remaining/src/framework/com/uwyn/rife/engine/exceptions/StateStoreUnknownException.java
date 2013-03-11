/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: StateStoreUnknownException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class StateStoreUnknownException extends EngineException
{
	private static final long serialVersionUID = 5257772217553922873L;

	private String	mSiteDeclarationName = null;
	private String	mStoreName = null;

	public StateStoreUnknownException(String siteDeclarationName, String storeName)
	{
		super("The site '"+siteDeclarationName+"' uses the unknown state store '"+storeName+"'.");
		
		mSiteDeclarationName = siteDeclarationName;
		mStoreName = storeName;
	}
	
	public String getSiteDeclarationName()
	{
		return mSiteDeclarationName;
	}
	
	public String getStoreName()
	{
		return mStoreName;
	}
}
