/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FilterNotFoundException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template.exceptions;

public class FilterNotFoundException extends TemplateException
{
	private static final long serialVersionUID = 3474427948218352405L;
	
	private String mXmlPath = null;

	public FilterNotFoundException(String xmlPath)
	{
		super("Couldn't find the filter '"+xmlPath+"'.");
		mXmlPath = xmlPath;
	}

	public String getXmlPath()
	{
		return mXmlPath;
	}
}
