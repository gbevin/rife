/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GlobalBeanGroupNotFoundException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class GlobalBeanGroupNotFoundException extends EngineException
{
	private static final long serialVersionUID = 7382206078077224292L;

	private String	mSiteDeclarationName = null;
	private String	mClassName = null;
	private String	mGroupName = null;
	
	public GlobalBeanGroupNotFoundException(String siteDeclarationName, String className, String groupName)
	{
		super("The site '"+siteDeclarationName+"' declared the global bean with class '"+className+"' and the group '"+groupName+"', however the group couldn't be found.");
		
		mSiteDeclarationName = siteDeclarationName;
		mClassName = className;
		mGroupName = groupName;
	}
	
	public String getSiteDeclarationName()
	{
		return mSiteDeclarationName;
	}
	
	public String getClassName()
	{
		return mClassName;
	}
	
	public String getGroupName()
	{
		return mGroupName;
	}
}
