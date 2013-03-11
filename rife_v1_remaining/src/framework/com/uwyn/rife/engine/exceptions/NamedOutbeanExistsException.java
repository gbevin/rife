/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: NamedOutbeanExistsException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class NamedOutbeanExistsException extends EngineException
{
	private static final long serialVersionUID = 3559785007755992637L;

	private String	mDeclarationName = null;
	private String	mOutbeanName = null;

	public NamedOutbeanExistsException(String declarationName, String outbeanName)
	{
		super("The element '"+declarationName+"' already contains the named outbean '"+outbeanName+"'.");
		
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
