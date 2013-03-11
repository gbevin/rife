/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CantFindResourceException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.xml.exceptions;

public class CantFindResourceException extends XmlErrorException
{
	private static final long serialVersionUID = -3540210087656613629L;
	
	private String	mResourcePath = null;
	
	public CantFindResourceException(String resourcePath, Throwable e)
	{
		super("Can't find the resource '"+resourcePath+"'.", e);
		
		mResourcePath = resourcePath;
	}
	
	public String getResourcePath()
	{
		return mResourcePath;
	}
}
