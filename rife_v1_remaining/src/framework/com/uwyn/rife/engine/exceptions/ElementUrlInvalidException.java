/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementUrlInvalidException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ElementUrlInvalidException extends EngineException
{
	private static final long serialVersionUID = 2492976780181401863L;

	private String	mId = null;
	private String	mUrl = null;

	public ElementUrlInvalidException(String id, String url)
	{
		super("The element id '"+id+"' has an invalid url '"+url+"'.");
		
		mId = id;
		mUrl = url;
	}
	
	public String getId()
	{
		return mId;
	}
	
	public String getUrl()
	{
		return mUrl;
	}
}
