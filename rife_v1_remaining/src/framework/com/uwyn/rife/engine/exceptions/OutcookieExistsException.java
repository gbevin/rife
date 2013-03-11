/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: OutcookieExistsException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class OutcookieExistsException extends EngineException
{
	private static final long serialVersionUID = -6711350953891535286L;
	
	private String	mDeclarationName = null;
	private String	mOutcookieName = null;

	public OutcookieExistsException(String declarationName, String outcookieName)
	{
		super("The element '"+declarationName+"' already contains outcookie '"+outcookieName+"'.");
		
		mDeclarationName = declarationName;
		mOutcookieName = outcookieName;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
	
	public String getOutcookieName()
	{
		return mOutcookieName;
	}
}
