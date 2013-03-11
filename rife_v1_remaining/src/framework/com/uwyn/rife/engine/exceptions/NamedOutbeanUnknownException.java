/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: NamedOutbeanUnknownException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class NamedOutbeanUnknownException extends EngineException
{
	private static final long serialVersionUID = -3917616464993896665L;

	private String	mDeclarationName = null;
	private String	mOutbeanName = null;

	public NamedOutbeanUnknownException(String declarationName, String outbeanName)
	{
		super("The element '"+declarationName+"' doesn't contain the named outbean '"+outbeanName+"'.");
		
		mDeclarationName = declarationName;
		mOutbeanName = outbeanName;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
	
	public String getOutbeanName()
	{
		return mOutbeanName;
	}
}
