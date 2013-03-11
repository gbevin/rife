/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GlobalCookieExistsException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class GlobalCookieExistsException extends EngineException
{
	private static final long serialVersionUID = 5752897898318501204L;

	private String	mDeclarationName = null;
	private String	mGlobalCookieName = null;

	public GlobalCookieExistsException(String declarationName, String name)
	{
		super("The site '"+declarationName+"' already contains the global cookie '"+name+"' in the group that declares it.");
		
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
