/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FallbackUrlExistsException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class FallbackUrlExistsException extends EngineException
{
	private static final long serialVersionUID = 7659453791120098860L;

	private String	mUrl = null;

	public FallbackUrlExistsException(String url)
	{
		super("The url '"+url+"' is already registered as a fallback element in the site.");
		
		mUrl = url;
	}
	
	public String getUrl()
	{
		return mUrl;
	}
}
