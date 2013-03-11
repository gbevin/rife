/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CantRetrieveResourceContentException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.resources.exceptions;

import java.net.URL;

public class CantRetrieveResourceContentException extends ResourceFinderErrorException
{
	private static final long serialVersionUID = 5514414842991049686L;
	
	private URL		mResource = null;
	private String	mEncoding = null;
	
	public CantRetrieveResourceContentException(URL resource, String encoding, Throwable e)
	{
		super("Error while retrieving the content of resource '"+resource.toString()+"' with encoding '"+encoding+"'.", e);
		
		mResource = resource;
		mEncoding = encoding;
	}
	
	public URL getResource()
	{
		return mResource;
	}
	
	public String getEncoding()
	{
		return mEncoding;
	}
}
