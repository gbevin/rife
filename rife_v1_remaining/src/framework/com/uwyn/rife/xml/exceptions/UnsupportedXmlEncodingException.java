/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: UnsupportedXmlEncodingException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.xml.exceptions;

public class UnsupportedXmlEncodingException extends XmlErrorException
{
	private static final long serialVersionUID = 1760212231607214310L;
	
	private String	mXmlPath = null;
	
	public UnsupportedXmlEncodingException(String xmlPath, Throwable e)
	{
		super("Error while creation inputstream reader for '"+xmlPath+"'.", e);
		
		mXmlPath = xmlPath;
	}
	
	public String getXmlPath()
	{
		return mXmlPath;
	}
}
