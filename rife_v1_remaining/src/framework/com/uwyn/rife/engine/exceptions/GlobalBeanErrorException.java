/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GlobalBeanErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class GlobalBeanErrorException extends EngineException
{
	private static final long serialVersionUID = -6226725951143752277L;

	private String	mSiteDeclarationName = null;
	private String	mClassName = null;

	public GlobalBeanErrorException(String siteDeclarationName, String className, Throwable cause)
	{
		super("The site '"+siteDeclarationName+"' triggered an unexpected error while obtaining the properties of the global bean with class '"+className+"'.", cause);
		
		mSiteDeclarationName = siteDeclarationName;
		mClassName = className;
	}
	
	public String getSiteDeclarationName()
	{
		return mSiteDeclarationName;
	}
	
	public String getClassName()
	{
		return mClassName;
	}
}
