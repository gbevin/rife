/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: UrlExistsException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class UrlExistsException extends EngineException
{
	private static final long serialVersionUID = 6391041558659184980L;
	
	private String	mNewElementId = null;
	private String	mUrl = null;

	public UrlExistsException(String newElementId, String url, String existingElementId)
	{
		super("The element '"+newElementId+"' tried to register the url '"+url+"' which is already registered to element '"+existingElementId+"'.");
		
		mUrl = url;
	}
	
	public String getNewElementId()
	{
		return mNewElementId;
	}
	
	public String getUrl()
	{
		return mUrl;
	}
}
