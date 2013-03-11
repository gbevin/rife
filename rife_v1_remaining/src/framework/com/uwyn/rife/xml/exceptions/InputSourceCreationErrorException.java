/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InputSourceCreationErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.xml.exceptions;

public class InputSourceCreationErrorException extends XmlErrorException
{
	private static final long serialVersionUID = -2195826702300143715L;
	
	private String	mXmlPath = null;
	
	public InputSourceCreationErrorException(String xmlPath)
	{
		super("Can't get input source for '"+xmlPath+"'.");
		
		mXmlPath = xmlPath;
	}
	
	public String getXmlPath()
	{
		return mXmlPath;
	}
}
