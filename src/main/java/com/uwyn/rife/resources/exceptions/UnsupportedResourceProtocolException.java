/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: UnsupportedResourceProtocolException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.resources.exceptions;

public class UnsupportedResourceProtocolException extends ResourceFinderErrorException
{
	private static final long serialVersionUID = 3763519896520670663L;
	
	private String	mFileName = null;
	private String	mProtocol = null;
	
	public UnsupportedResourceProtocolException(String fileName, String protocol)
	{
		super("The resource '"+fileName+"'  has the '"+protocol+"' protocol, which isn't supported.");
		
		mFileName = fileName;
		protocol = mProtocol;
	}
	
	public String getFileName()
	{
		return mFileName;
	}
	
	public String getProtocol()
	{
		return mProtocol;
	}
}
