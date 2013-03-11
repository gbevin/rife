/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MissingErrorRedirectorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.xml.exceptions;

public class MissingErrorRedirectorException extends XmlErrorException
{
	private static final long serialVersionUID = -169087962015254692L;
	
	private String	mXmlPath = null;
	
	public MissingErrorRedirectorException(String xmlPath)
	{
		super("An error redirector couldn't be obtained from Rep for the parsing of '"+xmlPath+"'.");
		
		mXmlPath = xmlPath;
	}
	
	public String getXmlPath()
	{
		return mXmlPath;
	}
}
