/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MissingImplementationException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class MissingImplementationException extends EngineException
{
	private static final long serialVersionUID = -3406715969986531292L;

	private String	mDeclarationName = null;

	public MissingImplementationException(String declarationName)
	{
		super("The element '"+declarationName+"' doesn't declare an implementation or you used the wrong element processor identifier, causing the actual processing not to happen (ie. omit the .xml extension for an element declared in an xml file).");
		
		mDeclarationName = declarationName;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
}
