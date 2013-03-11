/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ExitNotAttachedException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ExitNotAttachedException extends EngineException
{
	private static final long serialVersionUID = 3721546993900760550L;

	private String	mDeclarationName = null;
	private String	mExitName = null;

	public ExitNotAttachedException(String declarationName, String exitName)
	{
		super("There's nothing attached to the exit '"+exitName+"' of the element '"+declarationName+"'.");
		
		mDeclarationName = declarationName;
		mExitName = exitName;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
	
	public String getExitName()
	{
		return mExitName;
	}
}
