/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FatalParsingErrorsException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.xml.exceptions;

import com.uwyn.rife.tools.StringUtils;
import java.util.Collection;

public class FatalParsingErrorsException extends XmlErrorException
{
	private static final long serialVersionUID = 1286210792114678095L;
	
	private String				mXmlPath = null;
	private Collection<String>	mFatalErrors = null;
	
	public FatalParsingErrorsException(String xmlPath, Collection<String> fatalErrors)
	{
		super("The following fatal XML errors occured during the parsing of "+xmlPath+"'\n"+StringUtils.join(fatalErrors, "\n"));
		
		mXmlPath = xmlPath;
		mFatalErrors = fatalErrors;
	}
	
	public String getXmlPath()
	{
		return mXmlPath;
	}
	
	public Collection<String> getFatalErrors()
	{
		return mFatalErrors;
	}
}
