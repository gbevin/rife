/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GlobalCookiesLockedException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class GlobalCookiesLockedException extends EngineException
{
	private static final long serialVersionUID = -8256654477307232422L;

	private String	mDeclarationName = null;
	private String	mGlobalCookieName = null;

	public GlobalCookiesLockedException(String declarationName, String name)
	{
		super("The site '"+declarationName+"' couldn't add the global cookie '"+name+"' since the global cookies of the declaring group are locked.");
		
		mDeclarationName = declarationName;
		mGlobalCookieName = name;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
	
	public String getGlobalCookieName()
	{
		return mGlobalCookieName;
	}
}
