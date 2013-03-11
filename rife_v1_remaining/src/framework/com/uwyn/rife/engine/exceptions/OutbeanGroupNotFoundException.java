/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: OutbeanGroupNotFoundException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class OutbeanGroupNotFoundException extends EngineException
{
	private static final long serialVersionUID = -13022656866132724L;

	private String	mDeclarationName = null;
	private String	mClassName = null;
	private String	mGroupName = null;
	
	public OutbeanGroupNotFoundException(String declarationName, String className, String groupName)
	{
		super("The element '"+declarationName+"' declared an output bean with the class '"+className+"' and the group '"+groupName+"', however the group couldn't be found.");
		
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
