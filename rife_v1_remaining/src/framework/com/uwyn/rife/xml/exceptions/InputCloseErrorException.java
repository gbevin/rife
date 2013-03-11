/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InputCloseErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.xml.exceptions;

public class InputCloseErrorException extends XmlErrorException
{
	private static final long serialVersionUID = -6802885054707628583L;
	
	private String	mXmlPath = null;
	
	public InputCloseErrorException(String xmlPath, Throwable cause)
	{
		super("Error while closing the reader of '"+xmlPath+"'.", cause);
		
		mXmlPath = xmlPath;
	}
	
	public String getXmlPath()
	{
		return mXmlPath;
	}
}
