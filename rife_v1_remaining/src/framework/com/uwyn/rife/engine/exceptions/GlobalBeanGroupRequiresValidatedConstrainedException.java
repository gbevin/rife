/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GlobalBeanGroupRequiresValidatedConstrainedException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class GlobalBeanGroupRequiresValidatedConstrainedException extends EngineException
{
	private static final long serialVersionUID = 2324238714973217988L;

	private String	mSiteDeclarationName = null;
	private String	mClassName = null;
	private String	mGroupName = null;
	
	public GlobalBeanGroupRequiresValidatedConstrainedException(String siteDeclarationName, String className, String groupName)
	{
		super("The site '"+siteDeclarationName+"' declared the global bean with the group '"+groupName+"', however its class '"+className+"' doesn't implement ValidatedConstrained.");
		
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
