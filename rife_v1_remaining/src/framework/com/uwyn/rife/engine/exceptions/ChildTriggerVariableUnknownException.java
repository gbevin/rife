/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ChildTriggerVariableUnknownException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ChildTriggerVariableUnknownException extends EngineException
{
	private static final long serialVersionUID = -4742879501052842367L;

	private String	mDeclarationName = null;
	private String	mChildTriggerName = null;

	public ChildTriggerVariableUnknownException(String declarationName, String childTriggerName)
	{
		super("The element '"+declarationName+"' defines a child trigger for the variable '"+childTriggerName+"'. However, no input, incookie, output, outcookie, global var or global cookie with that name exists.");
		
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

