/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InbeanGroupNotFoundException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class InbeanGroupNotFoundException extends EngineException
{
	private static final long serialVersionUID = 5439502631175327783L;

	private String	mDeclarationName = null;
	private String	mClassName = null;
	private String	mGroupName = null;
	
	public InbeanGroupNotFoundException(String declarationName, String className, String groupName)
	{
		super("The element '"+declarationName+"' declared an input bean with class '"+className+"' and the group '"+groupName+"', however the group couldn't be found.");
		
		mDeclarationName = declarationName;
		mClassName = className;
		mGroupName = groupName;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
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
