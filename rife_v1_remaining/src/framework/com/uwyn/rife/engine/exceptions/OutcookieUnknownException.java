/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: OutcookieUnknownException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class OutcookieUnknownException extends EngineException
{
	private static final long serialVersionUID = 8683658486588169941L;

	private String	mDeclarationName = null;
	private String	mOutcookieName = null;

	public OutcookieUnknownException(String declarationName, String outcookieName)
	{
		super("The element '"+declarationName+"' doesn't contain outcookie '"+outcookieName+"'.");
		
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
