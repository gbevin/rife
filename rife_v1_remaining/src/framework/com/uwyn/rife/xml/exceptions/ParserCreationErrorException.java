/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParserCreationErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.xml.exceptions;

public class ParserCreationErrorException extends XmlErrorException
{
	private static final long serialVersionUID = 4369908122970192903L;
	
	private String	mXmlPath = null;
	
	public ParserCreationErrorException(String xmlPath, Throwable cause)
	{
		super("Error during the creation of the parser for '"+xmlPath+"'.", cause);
		
		mXmlPath = xmlPath;
	}
	
	public String getXmlPath()
	{
		return mXmlPath;
	}
}
