/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementDeclarationNameMissingException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ElementDeclarationNameMissingException extends EngineException
{
	static final long serialVersionUID = 424012397939593175L;
	
	private String	mSiteDeclarationName = null;
	private String	mId = null;
	private String	mUrl = null;
	private String	mImplementation = null;

	public ElementDeclarationNameMissingException(String siteDeclarationName, String id, String url, String implementation, Throwable cause)
	{
		super("The declaration of "  + (null == id ? (null == url ? (null == implementation ? "an element" : "element with implementation '"+implementation+"'") : "element with url '"+url+"'") : "element with id '"+id+"'") + " in site '"+siteDeclarationName+"' is missing.", cause);
		
		mSiteDeclarationName = siteDeclarationName;
		mId = id;
		mUrl = url;
		mImplementation = implementation;
	}
	
	public String getSiteDeclarationName()
	{
		return mSiteDeclarationName;
	}
	
	public String getId()
	{
		return mId;
	}
	
	public String getUrl()
	{
		return mUrl;
	}
	
	public String getImplementation()
	{
		return mImplementation;
	}
}
