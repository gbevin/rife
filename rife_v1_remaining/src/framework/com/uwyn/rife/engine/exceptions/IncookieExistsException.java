/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: IncookieExistsException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class IncookieExistsException extends EngineException
{
	private static final long serialVersionUID = -3132957572860775116L;

	private String	mDeclarationName = null;
	private String	mIncookieName = null;

	public IncookieExistsException(String declarationName, String incookieName)
	{
		super("The element '"+declarationName+"' already contains incookie '"+incookieName+"'.");
		
		mDeclarationName = declarationName;
		mIncookieName = incookieName;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
	
	public String getIncookieName()
	{
		return mIncookieName;
	}
}
