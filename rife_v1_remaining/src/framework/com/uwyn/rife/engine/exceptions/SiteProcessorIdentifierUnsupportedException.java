/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SiteProcessorIdentifierUnsupportedException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class SiteProcessorIdentifierUnsupportedException extends EngineException
{
	private static final long serialVersionUID = -7253563788439660688L;

	private String	mDeclarationName = null;
	private String	mIdentifier = null;

	public SiteProcessorIdentifierUnsupportedException(String declarationName, String identifier)
	{
		super("The site '"+declarationName+"' declares an unsupported site processor identifier '"+identifier+"'.");
		
		mDeclarationName = declarationName;
		mIdentifier = identifier;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
	
	public String getIdentifier()
	{
		return mIdentifier;
	}
}
