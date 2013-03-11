/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GlobalBeanNotFoundException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class GlobalBeanNotFoundException extends EngineException
{
	private static final long serialVersionUID = 2179101833410272588L;

	private String	mSiteDeclarationName = null;
	private String	mClassName = null;

	public GlobalBeanNotFoundException(String siteDeclarationName, String className, Throwable cause)
	{
		super("The site '"+siteDeclarationName+"' declared a global bean with class '"+className+"', which couldn't be found.", cause);
		
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
