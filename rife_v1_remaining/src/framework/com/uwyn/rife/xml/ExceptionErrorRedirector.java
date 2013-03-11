/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ExceptionErrorRedirector.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.xml;

import com.uwyn.rife.xml.exceptions.ParserExecutionErrorException;
import org.xml.sax.SAXParseException;

public class ExceptionErrorRedirector extends XmlErrorRedirector
{
	private String	mXmlPath = null;
	public ExceptionErrorRedirector(Xml2Data xml2Data)
	{
		super();
		
		mXmlPath = xml2Data.getXmlPath();
	}
	
	public void warning(SAXParseException e)
	{
		throw new ParserExecutionErrorException(mXmlPath, e);
	}

	public void error(SAXParseException e)
	{
		throw new ParserExecutionErrorException(mXmlPath, e);
	}

	public void fatalError(SAXParseException e)
	{
		throw new ParserExecutionErrorException(mXmlPath, e);
	}
}

