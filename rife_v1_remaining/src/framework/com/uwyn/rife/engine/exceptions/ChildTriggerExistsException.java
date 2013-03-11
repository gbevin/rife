/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ChildTriggerExistsException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ChildTriggerExistsException extends EngineException
{
	private static final long serialVersionUID = -5972620393477955167L;

	private String	mDeclarationName = null;
	private String	mChildTriggerName = null;

	public ChildTriggerExistsException(String declarationName, String childTriggerName)
	{
		super("The element '"+declarationName+"' already contains child trigger '"+childTriggerName+"'.");
		
		mDeclarationName = declarationName;
		mChildTriggerName = childTriggerName;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
	
	public String getChildTriggerName()
	{
		return mChildTriggerName;
	}
}
