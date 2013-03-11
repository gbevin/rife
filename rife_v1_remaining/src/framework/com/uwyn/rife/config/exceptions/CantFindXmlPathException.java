/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CantFindXmlPathException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.config.exceptions;

public class CantFindXmlPathException extends ConfigErrorException
{
	private static final long serialVersionUID = 379094904515534057L;

	private String	mXmlPath = null;
	
	public CantFindXmlPathException(String xmlPath)
	{
		super("The xml path '"+xmlPath+"' can't be found.");
		
		mXmlPath = xmlPath;
	}
	
	public String getXmlPath()
	{
		return mXmlPath;
	}
}
