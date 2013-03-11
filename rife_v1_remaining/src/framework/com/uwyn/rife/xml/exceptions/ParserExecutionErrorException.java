/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParserExecutionErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.xml.exceptions;

public class ParserExecutionErrorException extends XmlErrorException
{
	private static final long serialVersionUID = -1818881432299217047L;
	
	private String	mXmlPath = null;
	
	public ParserExecutionErrorException(String xmlPath, Throwable cause)
	{
		super("Error during the parsing of '"+xmlPath+"'.", cause);
		
		mXmlPath = xmlPath;
	}
	
	public String getXmlPath()
	{
		return mXmlPath;
	}
}
